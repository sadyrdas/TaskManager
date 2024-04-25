package cz.cvut.fel.tasktest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.jetpackcompose.navigation.ui.theme.JetpackComposeDrawerNavigationTheme
import cz.cvut.fel.tasktest.data.BoardState
import cz.cvut.fel.tasktest.data.BoardViewModel
import cz.cvut.fel.tasktest.data.TagViewModel
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.UserViewModel
import cz.cvut.fel.tasktest.screens.BoardCreationScreen

class MainActivity : ComponentActivity() {

    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            TaskifyDatabase::class.java,
            "tasktest-db"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeDrawerNavigationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(viewModel, viewUserModel, viewTagModel)
                }
            }
        }
    }
}