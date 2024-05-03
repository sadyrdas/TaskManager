package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel

@Composable
fun AllTasksScreen(drawerState: DrawerState, taskViewModel: TaskViewModel, navController: NavController) {


    val (drawerStateForFilter, setDrawerStateForFilter) = remember { mutableStateOf(false) }

    val taskState by taskViewModel.state.collectAsState()
    val tasks = taskState.tasks
    val tags = taskState.tags

    LaunchedEffect(key1 = true) {
        taskViewModel.fetchTasks()
    }

    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = "All tasks",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer , imageVector = Icons.Default.Close )
        }
    ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                taskState.tasks.forEach() { task ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp)
                    ) {
                        Text(
                            text = "All Tasks",
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { setDrawerStateForFilter(true) }) {
                            Icon(
                                Icons.Filled.MoreVert, contentDescription = "Filter Icon",
                                modifier = Modifier
                                    .padding(end = 16.dp)
                            )
                        }
                    }
                    TaskItem(task = task, navController)
                    // Выводим список задач
//                    tasks.forEach { task ->
//                        TaskItem(task = task, tags, navController)
//                    }
                }
            }
    }
}

@Composable
fun TaskItem(task: Task, navController: NavController) {
    val taskId = task.id
    Column(
        modifier = Modifier.clickable { navController.navigate("${MainRoute.CurrentTask.name}/${taskId}") }
    ) {
//        Row {
//            tags.forEach { tag ->
//                if (tag.id == task.tagId) {
//                    Text(text = tag.name)
//                }
//            }
//        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = task.title)
    }
}
