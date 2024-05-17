package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.SortTypeForTask
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel

@Composable
fun AllTasksScreen(drawerState: DrawerState, taskViewModel: TaskViewModel, navController: NavHostController) {
    val taskState by taskViewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        taskViewModel.fetchTasks()
    }

    var selectedSortType by remember { mutableStateOf(SortTypeForTask.UNSORTED) }

    LaunchedEffect(selectedSortType) {
        taskViewModel.sortTasks(selectedSortType)
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "All tasks",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                imageVector = Icons.Default.Close
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Text(text = "Filter your task by $selectedSortType", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(8.dp))

                // Display the sorting dropdown
                SortingDropdown(
                    sortingOptions = listOf(
                        "Unsorted",
                        "Title (ASC)",
                        "Title (DESC)",
                        "Start Date (ASC)",
                        "Start Date (DESC)",
                        "End Date (ASC)",
                        "End Date (DESC)"
                    ),
                    onItemSelected = { selectedSortType = it }
                )
            }

            taskState.tasks.forEach { task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .border(3.dp, Color.Black, ShapeDefaults.Large)
                            .shadow(2.dp)
                            .width(270.dp)
                            .height(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                ShapeDefaults.Medium
                            )
                    ) {
                        TaskCard(task = task, navController, taskViewModel, task.id)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}




@Composable
fun SortingDropdown(
    sortingOptions: List<String>,
    onItemSelected: (SortTypeForTask) -> Unit
) {

    var selectedOption by remember { mutableStateOf(sortingOptions[0]) }


    var isDropdownMenuVisible by remember { mutableStateOf(false) }


    Box {
        IconButton(
            onClick = { isDropdownMenuVisible = !isDropdownMenuVisible }
        ) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Filter")
        }
        DropdownMenu(
            expanded = isDropdownMenuVisible,
            onDismissRequest = { isDropdownMenuVisible = false }
        ) {
            sortingOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption = option

                        onItemSelected(getSortTypeForTask(option))
                        isDropdownMenuVisible = false
                    }
                ) {
                    Text(text = option)
                }
            }
        }
    }
}



fun getSortTypeForTask(option: String): SortTypeForTask {
    return when (option) {
        "Unsorted" -> SortTypeForTask.UNSORTED
        "Title (ASC)" -> SortTypeForTask.TITLE_ASC
        "Title (DESC)" -> SortTypeForTask.TITLE_DESC
        "Start Date (ASC)" -> SortTypeForTask.START_DATA_ASC
        "Start Date (DESC)" -> SortTypeForTask.START_DATA_DESC
        "End Date (ASC)" -> SortTypeForTask.END_DATA_ASC
        "End Date (DESC)" -> SortTypeForTask.END_DATA_DESC
        else -> SortTypeForTask.UNSORTED
    }
}


