package cz.cvut.fel.tasktest

import ForegroundPermissionTextProvider
import PermissionDialog
import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room

import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.viewModels.UserViewModel
import cz.cvut.fel.tasktest.ui.theme.JetpackComposeDrawerNavigationTheme
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import cz.cvut.fel.tasktest.permissions.PermissionActivity

class MainActivity : ComponentActivity() {

    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            TaskifyDatabase::class.java,
            TaskifyDatabase.DATABASE_NAME
        ).build()
    }

    private val viewModel by viewModels<BoardViewModel>(
        factoryProducer ={
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BoardViewModel(db.boardDao(),) as T
                }
            }
        }
    )
    private val viewUserModel by viewModels<UserViewModel>(
        factoryProducer ={
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(db.userDAO()) as T
                }
            }
        }
    )
    private val viewTagModel by viewModels<TagViewModel>(
        factoryProducer ={
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TagViewModel(db.tagDAO()) as T
                }
            }
        }
    )

    private val taskViewModel by viewModels<TaskViewModel>(
        factoryProducer ={
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(db.taskDao(), db.photoDAO()) as T
                }
            }
        }
    )

    private val sectionViewModel by viewModels<SectionViewModel>(
        factoryProducer ={
            object: ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SectionViewModel(db.sectionDAO()) as T
                }
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JetpackComposeDrawerNavigationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    MainNavigation(taskViewModel, viewModel, sectionViewModel, viewUserModel, viewTagModel)
                    MainContent(
                        taskViewModel,
                        viewModel,
                        sectionViewModel,
                        viewUserModel,
                        viewTagModel
                    )
                }
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    private fun checkAndRequestPermissions() {
//        val permissions = arrayOf(
//            Manifest.permission.POST_NOTIFICATIONS,
//            Manifest.permission.FOREGROUND_SERVICE)
//
//        val sharedPreferences = getSharedPreferences("permissions_prefs", Context.MODE_PRIVATE)
//        val permissionRequestCount = sharedPreferences.getInt("permission_request_count", 0)
//        if (permissionRequestCount < 2 && permissions.any {
//            ContextCompat.checkSelfPermission(this, it) != PERMISSION_GRANTED
//        }) {
//            // Launch PermissionActivity if permissions are not granted
//            startActivity(Intent(this, PermissionActivity::class.java))
//            // Increment and save the request count
//            sharedPreferences.edit().putInt("permission_request_count", permissionRequestCount + 1).apply()
//        }
//    }
}


@Composable
fun MainContent(
    taskViewModel: TaskViewModel,
    boardViewModel: BoardViewModel,
    sectionViewModel: SectionViewModel,
    userViewModel: UserViewModel,
    tagViewModel: TagViewModel
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? ComponentActivity
    val sharedPreferences = context.getSharedPreferences("permissions_prefs", Context.MODE_PRIVATE)
    val permissionRequestCount = sharedPreferences.getInt("permission_request_count", 0)
    val permissionsToRequest = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE
    )
    val dialogQueue = remember { mutableStateListOf<String>() }
    val permissionsRequested = remember { mutableStateOf(false) }

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                val isGranted = perms[permission] == true
                if (!isGranted) {
                    dialogQueue.add(permission)
                }
            }
            // Check again if all permissions are granted after the request
            if (permissionsToRequest.all { permission ->
                    ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
                }) {
                // All permissions granted, proceed with main content
            } else {
                // Increment and save the request count
                sharedPreferences.edit().putInt("permission_request_count", permissionRequestCount + 1).apply()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionsRequested.value && permissionRequestCount < 2) {
        multiplePermissionResultLauncher.launch(permissionsToRequest)
        permissionsRequested.value = true
    }
    }

    dialogQueue.reversed().forEach { permission ->
        PermissionDialog(
            permissionTP = when (permission) {
                Manifest.permission.RECORD_AUDIO -> {
                    ForegroundPermissionTextProvider()
                }
                else -> return@forEach
            },
            isPermanentlyDeclined = activity?.shouldShowRequestPermissionRationale(permission) == false,
            onDismiss = { dialogQueue.remove(permission) },
            onOKClick = {
                dialogQueue.remove(permission)
                multiplePermissionResultLauncher.launch(arrayOf(permission))
            },
            onSettingsClick = {
                context.openAppSettings()
                dialogQueue.remove(permission)
            }
        )
    }

    // Your existing content
    MainNavigation(taskViewModel, boardViewModel, sectionViewModel, userViewModel, tagViewModel)
}


fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}