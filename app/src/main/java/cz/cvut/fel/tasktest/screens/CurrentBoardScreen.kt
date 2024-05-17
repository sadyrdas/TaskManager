package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
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
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.Section
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.events.SectionEvent
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel

@Composable
fun CurrentBoardScreen(navController: NavHostController, drawerState: DrawerState, boardViewModel: BoardViewModel, sectionViewModel: SectionViewModel, boardId:Long, taskViewModel: TaskViewModel) {

    val taskState by taskViewModel.state.collectAsState()

    val boardState by boardViewModel.boardState

    val sectionState by sectionViewModel.state.collectAsState()

    var focusSectionId:Long? = null


    LaunchedEffect(boardId) {
        boardViewModel.getBoardState(boardId)
    }

    LaunchedEffect(boardId) {
        sectionViewModel.fetchSections(boardId)
    }

    LaunchedEffect(key1 = null) {
        taskViewModel.fetchTasks()
    }

    var isEditTitleDialogOpen by remember { mutableStateOf(false) }
    fun toggleEditSectionDialog(sectionId: Long?=null) {
        isEditTitleDialogOpen = !isEditTitleDialogOpen
        focusSectionId = sectionId
    }

    val titleOfCurrentBoard = boardState?.title ?: ""
    val sections = sectionState.sections
    val tasks = taskState.tasks

    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = titleOfCurrentBoard,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                imageVector = Icons.Default.ArrowBack,
                navigationAction = {navController.navigate(MainRoute.Boards.name)}
            )
        },
        floatingActionButton = {
            FloatingButton(navController, boardId)
        }
    ) { paddingValues ->
        if (isEditTitleDialogOpen == true) {
            EditSectionDialog(
//                isOpen = isEditTitleDialogOpen,
                currentTitle = sectionState.title ?: "",
                onConfirm = { newTitle ->
                    sectionViewModel.onEvent(SectionEvent.EditSectionTitle(newTitle, boardId, focusSectionId!!))
                },
//                section = sections.find { it.id == currentSectionId }!!,
                onDismiss = { toggleEditSectionDialog() }
            )
        }
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .horizontalScroll(rememberScrollState(), reverseScrolling = true)

        ) {
            sections.forEach { section ->
                val tasksInSection = tasks.filter { it.sectionId == section.id }
                val minHeight = if (tasksInSection.isEmpty()) 300.dp else 180.dp * tasksInSection.size
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            ShapeDefaults.Medium
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .verticalScroll(rememberScrollState())
                            .heightIn(minHeight)
                            .width(300.dp)

                    ) {
                        Row {
                            Text(
                                text = section.title,
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { toggleEditSectionDialog(section.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Section Edit",
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(end = 8.dp)
                                )
                            }
                        }

                        Divider(color = Color.Gray, thickness = 2.dp)
                        Column(Modifier.weight(1f)) {
                            tasksInSection.forEach { task ->
                                Box(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .border(3.dp, Color.Black, ShapeDefaults.Large)
                                        .shadow(2.dp)
                                        .width(270.dp)
                                        .height(150.dp)
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    TaskCard(task, navController, taskViewModel, task.id)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .border(2.dp, Color.Black, ShapeDefaults.Medium)
                                .background(color = Color.LightGray, ShapeDefaults.Medium)
                                .height(40.dp)
                                .width(150.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "More Options",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(end = 8.dp)
                                    .clickable {
                                        navController.navigate(
                                            route =
                                            "${MainRoute.TaskCreation.name}/$boardId"
                                        )
                                    }
                            )
                            Text(
                                text = "Add new task",
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }

    }

}
@Composable
fun FloatingButton(navController: NavHostController, boardId: Long) {
    FloatingActionButton(
        onClick = {
            navController.navigate("${MainRoute.SectionCreation.name}/$boardId")
        },
        modifier = Modifier
            .size(56.dp)
            .background(color = Color(0xFF59D47B), ShapeDefaults.ExtraLarge)
            .padding(16.dp, 16.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.sections),
            contentDescription = "Add Section Icon",
            modifier = Modifier.background(color = Color(0xFF59D47B))
        )
    }
}

@Composable
fun TaskCard(task: Task, navController: NavHostController, taskViewModel: TaskViewModel, taskId: Long) {
    LaunchedEffect(taskId) {
        taskViewModel.fetchTagsForTask(taskId)
    }

    // Observe tags for the task from the map in TaskViewModel
    val tagsForTask by taskViewModel.tagsForTaskMap.collectAsState()
    val tags = tagsForTask[taskId] ?: emptyList()

    Column {
        Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
            tags.forEach { tag ->
                Text(
                    tag.name,
                    modifier = Modifier
                        .padding(end = 8.dp) // Add padding between tags
                        .background(Color(android.graphics.Color.parseColor(tag.background)))
                )
            }
        }
        Row(modifier = Modifier.padding(top = 20.dp)) {
            Text(
                text = task.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                fontSize = MaterialTheme.typography.headlineMedium.fontSize
            )
            IconButton(onClick = { navController.navigate("${MainRoute.CurrentTask.name}/${task.id}") }) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Task Edit",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EditSectionDialog(
    currentTitle: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newTitle by remember { mutableStateOf(currentTitle) } // Переменная для нового названия

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Title of Section") },
        text = {
            // Текстовое поле для ввода нового названия
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("New Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                // Вызываем функцию onConfirm с новым названием
                onConfirm(newTitle)
                onDismiss() // Закрываем диалог
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

