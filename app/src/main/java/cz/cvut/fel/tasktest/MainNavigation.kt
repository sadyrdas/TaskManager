package cz.cvut.fel.tasktest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import cz.cvut.fel.tasktest.data.BoardViewModel
import cz.cvut.fel.tasktest.data.TagViewModel
import cz.cvut.fel.tasktest.data.TaskViewModel
import cz.cvut.fel.tasktest.data.UserViewModel
import cz.cvut.fel.tasktest.screens.AboutScreen
import cz.cvut.fel.tasktest.screens.AccountCustomizationScreen
import cz.cvut.fel.tasktest.screens.AllTasksScreen

import cz.cvut.fel.tasktest.screens.ArticlesScreen
import cz.cvut.fel.tasktest.screens.BoardCreationScreen
import cz.cvut.fel.tasktest.screens.SettingsScreen
import cz.cvut.fel.tasktest.screens.TagCreationScreen
import cz.cvut.fel.tasktest.screens.TaskCreationScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class MainRoute(value: String) {
    Boards("boards"),
    Statistics("statistics"),
    Settings("settings"),
    AccountCustomization("accountCustomization"),
    BoardCreation("boardCreation"),
    TagCreation("tagCreation"),
    TaskCreation("taskCreation"),
    AllTasks("allTasks")
}

private data class DrawerMenu(val id: Int, val title: String, val route: String)

private val menus = arrayOf(
    DrawerMenu(R.drawable.pdaboards, "Boards", MainRoute.Boards.name),
    DrawerMenu(R.drawable.settingsicon, "Settings", MainRoute.Settings.name),
    DrawerMenu(R.drawable.statisticsicon, "Statistics", MainRoute.Statistics.name),
    DrawerMenu(R.drawable.tasklisticon, "All Tasks", MainRoute.AllTasks.name)
)

@Composable
private fun DrawerContent(
    menus: Array<DrawerMenu>,
    viewModel: UserViewModel,
    onMenuClick: (String) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(150.dp),
                imageVector = Icons.Filled.AccountCircle,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = state.userName)
        Spacer(modifier = Modifier.height(12.dp))
        menus.forEach {
            NavigationDrawerItem(
                label = { Text(text = it.title) },
                icon = { Icon(painterResource(id = it.id), contentDescription = null)},
                selected = false,
                onClick = {
                    onMenuClick(it.route)
                }
            )
        }
    }
}

@Composable
fun MainNavigation(
    taskViewModel: TaskViewModel,
    viewModel: BoardViewModel,
    viewUserModel: UserViewModel,
    viewTagModel: TagViewModel,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus, viewUserModel) { route ->
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    navController.navigate(route)
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = MainRoute.Boards.name) {
            composable(MainRoute.Boards.name) {
                ArticlesScreen(navController,viewModel, drawerState)
            }
            composable(MainRoute.Statistics.name) {
                AboutScreen(drawerState)
            }
            composable(MainRoute.Settings.name) {
                SettingsScreen(navController,drawerState)
            }
            composable(MainRoute.AccountCustomization.name){
                AccountCustomizationScreen(drawerState, viewUserModel)
            }
            composable(MainRoute.BoardCreation.name){
                BoardCreationScreen(drawerState,viewModel, navController)
            }
            composable(MainRoute.TagCreation.name){
                TagCreationScreen(drawerState, viewTagModel)
            }
            composable(MainRoute.TaskCreation.name){
                TaskCreationScreen(drawerState, viewModel, taskViewModel)
            }
            composable(MainRoute.AllTasks.name) {
                AllTasksScreen(drawerState, taskViewModel)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainNavigationPreview() {
//    MainNavigation()
//}