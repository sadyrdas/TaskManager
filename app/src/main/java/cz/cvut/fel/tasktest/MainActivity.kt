package cz.cvut.fel.tasktest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted

import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.viewModels.UserViewModel
import cz.cvut.fel.tasktest.notification.TaskifyNotificationService
import cz.cvut.fel.tasktest.ui.theme.JetpackComposeDrawerNavigationTheme
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

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
                    MainNavigation(taskViewModel, viewModel, sectionViewModel, viewUserModel, viewTagModel)

                }
            }
        }
    }
}