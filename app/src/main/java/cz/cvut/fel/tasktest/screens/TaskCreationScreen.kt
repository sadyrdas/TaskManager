package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel
import cz.cvut.fel.tasktest.data.events.TaskEvent
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(navController: NavHostController, drawerState: DrawerState, viewModel: BoardViewModel, taskViewModel: TaskViewModel, sectionViewModel: SectionViewModel, tagViewModel: TagViewModel) {

    val selectedBoardId = remember { mutableStateOf<Long?>(null) }
    val taskState by taskViewModel.state.collectAsState()
    val tagState by tagViewModel.state.collectAsState()


    var openDatePicker by remember { mutableStateOf(false) }
    var openEndDatePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<Date?>(null) }
    var selectedEndDate by remember { mutableStateOf<Date?>(null) }

    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = "Create Task",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer , imageVector = Icons.Default.Close )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(0.dp)
                .fillMaxWidth()
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                DropDownBoard("Desk",  viewModel) { selectedBoardId.value = it }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                DropDownSection("Section",sectionViewModel, taskViewModel, selectedBoardId.value) { }
            }

            Column(modifier = Modifier
                .padding(36.dp)

            )
            {
                TextField(
                    value = taskState.title,
                    placeholder = {Text(text = "Task name")},
                    onValueChange = { newTaskName ->
                        taskViewModel.onEvent(TaskEvent.SetTaskName(newTaskName))},
                    modifier = Modifier
                        .width(320.dp)
                        .padding(top = 16.dp)
                )
                TextField(
                    value = taskState.description,
                    placeholder = {Text(text = "Description")},
                    onValueChange = { newDescription ->
                        taskViewModel.onEvent(TaskEvent.SetTaskDescription(newDescription, taskState.id))},
                    modifier = Modifier
                        .width(320.dp)
                        .padding(top = 16.dp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )
                Row {

                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.DateRange, contentDescription = "Tag Icon",
                        modifier = Modifier.padding(end = 16.dp),
                    )
                    Column {
                        Text(
                            text = selectedStartDate?.toString() ?: "Starting..", // Display the selected date or "Starting.." if null
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable(onClick = { openDatePicker = true }),
                        )
                        Divider(modifier = Modifier
                            .padding(bottom = 4.dp)
                            .width(250.dp))
                        Text(
                            text = selectedEndDate?.toString() ?: "Date of end", // Display the selected end date or "Date of end" if null
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable(onClick = { openEndDatePicker = true }),
                        )
                    }
                    if (openDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        val confirmEnabled = remember {derivedStateOf { datePickerState.selectedDateMillis != null }}
                        DatePickerDialog(
                            onDismissRequest = { openDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    openDatePicker = false
                                    selectedStartDate = datePickerState.selectedDateMillis?.let { Date(it) }
                                    taskViewModel.onEvent(TaskEvent.SetTaskDateStart(selectedStartDate.toString()))
                                },
                                    enabled = confirmEnabled.value
                                ) {
                                    Text("Confirm")
                                }
                            }) {
                            DatePicker(
                                state = datePickerState,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    if (openEndDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        val confirmEnabled = remember {derivedStateOf { datePickerState.selectedDateMillis != null }}
                        DatePickerDialog(
                            onDismissRequest = { openEndDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    openEndDatePicker = false
                                    selectedEndDate = datePickerState.selectedDateMillis?.let { Date(it) }
                                    taskViewModel.onEvent(TaskEvent.SetTaskDateEnd(selectedEndDate.toString()))
                                },
                                    enabled = confirmEnabled.value
                                ) {
                                    Text("Confirm")
                                }
                            }) {
                            DatePicker(
                                state = datePickerState,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 26.dp)
                ) {
                    Icon(
                        Icons.Filled.AddCircle, contentDescription = "Tag Icon",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(
                        text = "Add attachment",
                        modifier = Modifier.clickable { /*...*/ }
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )


                Button(onClick = {
                    taskViewModel.onEvent(TaskEvent.SaveTask)
                    navController.navigate("${MainRoute.CurrentBoard.name}/${selectedBoardId.value}") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 50.dp)) {
                    Text(text = "Save")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownBoard(
    label: String,
    viewModel: BoardViewModel,
    onItemSelected: (Long) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val boards = viewModel.state.collectAsState().value.boards

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it })
    {
        TextField(
            value = selectedText,
            onValueChange = {},
            label = { Text(text = label, modifier = Modifier.padding(end = 8.dp)) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            boards.forEachIndexed { index, board ->
                DropdownMenuItem(
                    text = { Text(text = board.title) },
                    onClick = {
                        selectedText = boards[index].title
                        isExpanded = false
                        onItemSelected(boards[index].id) // Вызываем обработчик выбора элемента
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSection(
    label: String,
    viewModel: SectionViewModel,
    taskViewModel: TaskViewModel,
    boardId: Long?,
    onItemSelected: (Long) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val sections = viewModel.state.collectAsState().value.sections
    val taskState by taskViewModel.state.collectAsState()

    // Функция для обновления списка секций после выбора доски
    LaunchedEffect(boardId) {
        if (boardId != null) {
            viewModel.fetchSections(boardId)
        }
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it })
    {
        TextField(
            value = selectedText,
            onValueChange = {},
            label = { Text(text = label, modifier = Modifier.padding(end = 8.dp)) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            sections.forEachIndexed { index, section ->
                DropdownMenuItem(
                    text = { Text(text = section.title) },
                    onClick = {
                        selectedText = sections[index].title
                        isExpanded = false
                        onItemSelected(sections[index].id) // Вызываем обработчик выбора элемента
                        taskState.sectionid = sections[index].id
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
