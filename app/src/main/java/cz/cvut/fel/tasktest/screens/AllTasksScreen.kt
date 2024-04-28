package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.TaskViewModel

@Composable
fun AllTasksScreen(drawerState: DrawerState, taskViewModel: TaskViewModel) {


    val (drawerStateForFilter, setDrawerStateForFilter) = remember { mutableStateOf(false) }

    val taskState by taskViewModel.state.collectAsState()
    val tasks = taskState.tasks
    val tags = taskState.tags
    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = "Create Task",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer , imageVector = Icons.Default.Close )
        }
    ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ){

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
                    IconButton(onClick = { setDrawerStateForFilter(true)}){
                        Icon(Icons.Filled.MoreVert, contentDescription = "Filter Icon",
                            modifier = Modifier
                                .padding(end = 16.dp)
                        )
                    }
                }

                // Выводим список задач
                tasks.forEach { task ->
                    TaskItem(task = task, tags)
                }
            }

    }
}

@Composable
fun TaskItem(task: Task, tags:List<Tag>) {
    Column {
        Row {
            tags.forEach { tag ->
                if (tag.id == task.tagId) {
                    Text(text = tag.name)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = task.title)
    }
}
