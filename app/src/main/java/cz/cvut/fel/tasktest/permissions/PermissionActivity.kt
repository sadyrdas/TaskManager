package cz.cvut.fel.tasktest.permissions

import ForegroundPermissionTextProvider
import NotificationPermissionTextProvider
import PermissionDialog
import PermissionViewModel
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

class PermissionActivity : ComponentActivity() {

    private val permissionsToRequest = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (permissionsToRequest.all { permission ->
                ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
            }) {
            // If all permissions are granted, finish the activity
            finish()
            return
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PermissionScreen(permissionsToRequest)
                }
            }
        }
    }
}

@Composable
fun PermissionScreen(permissionsToRequest: Array<String>) {
    val viewModel: PermissionViewModel = viewModel()
    val dialogQueue = remember { mutableStateListOf<String>() }

    val context = LocalContext.current as Activity

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                val isGranted = perms[permission] == true
                viewModel.onPermissionResult(permission, isGranted)
                if (!isGranted) {
                    dialogQueue.add(permission)
                }
            }
            // Check again if all permissions are granted after the request
            if (permissionsToRequest.all { permission ->
                    ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED
                }) {
                context.finish()
            }
        }
    )

    LaunchedEffect(Unit) {
        multiplePermissionResultLauncher.launch(permissionsToRequest)
    }

    dialogQueue.reversed().forEach { permission ->
        PermissionDialog(
            permissionTP = when (permission) {
                Manifest.permission.POST_NOTIFICATIONS -> {
                    NotificationPermissionTextProvider()
                }
                Manifest.permission.RECORD_AUDIO -> {
                    ForegroundPermissionTextProvider()
                }
                else -> return@forEach
            },
            isPermanentlyDeclined = !context.shouldShowRequestPermissionRationale(permission),
            onDismiss = viewModel::dismissDialog,
            onOKClick = {
                viewModel.dismissDialog()
                multiplePermissionResultLauncher.launch(arrayOf(permission))
            },
            onSettingsClick = context::openAppSettings
        )
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
