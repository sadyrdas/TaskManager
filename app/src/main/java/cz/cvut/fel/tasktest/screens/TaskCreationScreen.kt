package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.data.events.TaskEvent
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    viewModel: BoardViewModel,
    taskViewModel: TaskViewModel,
    sectionViewModel: SectionViewModel,
    tagViewModel: TagViewModel,
    boardId: Long? = null,
) {
    val selectedBoardId = remember { mutableStateOf<Long?>(null) }
    val selectedSectionId = remember { mutableStateOf<Long?>(null) }
    val taskState by taskViewModel.state.collectAsState()
    val tagState by tagViewModel.state.collectAsState()

    var openDatePicker by remember { mutableStateOf(false) }
    var openEndDatePicker by remember { mutableStateOf(false) }

    val currentDate = Date()

    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    calendar.add(Calendar.WEEK_OF_YEAR, 1)

    val futureDate = calendar.time
    var selectedStartDate by remember { mutableStateOf<Date?>(currentDate) }
    var selectedEndDate by remember { mutableStateOf<Date?>(futureDate) }

    var showInvalidDateDialog by remember { mutableStateOf(false) }
    var showInvalidTitleDialog by remember { mutableStateOf(false) }

    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterDescription = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    var showSectionNotSelectedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "Create Task",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                imageVector = Icons.Default.Close,
                navigationAction = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        if (showInvalidDateDialog) {
            AlertDialog(
                onDismissRequest = { showInvalidDateDialog = false },
                title = { Text("Error") },
                text = { Text("End date cannot be before start date.") },
                confirmButton = {
                    Button(
                        onClick = { showInvalidDateDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        if (showInvalidTitleDialog) {
            AlertDialog(
                onDismissRequest = { showInvalidTitleDialog = false },
                title = { Text("Error") },
                text = { Text("Task name cannot be empty.") },
                confirmButton = {
                    Button(
                        onClick = { showInvalidTitleDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        if (showSectionNotSelectedDialog) {
            AlertDialog(
                onDismissRequest = { showSectionNotSelectedDialog = false },
                title = { Text("Section Not Selected") },
                text = { Text("Please select a section before saving the task.") },
                confirmButton = {
                    Button(
                        onClick = { showSectionNotSelectedDialog = false }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    DropDownBoard(
                        "Desk",
                        viewModel,
                        boardId
                    ) { selectedBoardId.value = it }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    DropDownSection(
                        label = "Section",
                        viewModel = sectionViewModel,
                        taskViewModel = taskViewModel,
                        boardId = selectedBoardId.value,
                        onItemSelected = { selectedSectionId.value = it },
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(36.dp)
                ) {
                    TextField(
                        value = taskState.title,
                        placeholder = { Text(text = "Task name") },
                        onValueChange = { newTaskName ->
                            taskViewModel.onEvent(TaskEvent.SetTaskName(newTaskName))
                        },
                        modifier = Modifier
                            .width(320.dp)
                            .padding(top = 16.dp)
                            .focusRequester(focusRequesterName),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                    TextField(
                        value = taskState.description,
                        placeholder = { Text(text = "Description") },
                        onValueChange = { newDescription ->
                            taskViewModel.onEvent(
                                TaskEvent.SetTaskDescription(
                                    newDescription,
                                    taskState.id
                                )
                            )
                        },
                        modifier = Modifier
                            .width(320.dp)
                            .padding(top = 16.dp)
                            .focusRequester(focusRequesterDescription),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
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
                            text = convertToDate(selectedStartDate.toString())
                                ?: "Starting..", // Display the selected date or "Starting.." if null
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable(onClick = { openDatePicker = true }),
                        )
                        Divider(
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .width(250.dp)
                        )
                        Text(
                            text = convertToDate(selectedEndDate.toString())
                                ?: "Date of end", // Display the selected end date or "Date of end" if null
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable(onClick = { openEndDatePicker = true }),
                        )
                    }
                    if (openDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        val confirmEnabled =
                            remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
                        DatePickerDialog(
                            onDismissRequest = { openDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        openDatePicker = false
                                        selectedStartDate =
                                            datePickerState.selectedDateMillis?.let { Date(it) }
                                        taskViewModel.onEvent(
                                            TaskEvent.SetTaskDateStart(
                                                selectedStartDate.toString()
                                            )
                                        )
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
                        val confirmEnabled =
                            remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
                        DatePickerDialog(
                            onDismissRequest = { openEndDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        openEndDatePicker = false
                                        selectedEndDate =
                                            datePickerState.selectedDateMillis?.let { Date(it) }
                                        if (selectedEndDate != null && selectedStartDate != null && selectedEndDate!!.after(
                                                selectedStartDate
                                            )
                                        ) {
                                            // Если выбранная дата конца больше даты начала, то сохраняем её
                                            taskViewModel.onEvent(
                                                TaskEvent.SetTaskDateStart(
                                                    selectedStartDate.toString()
                                                )
                                            )
                                        } else {
                                            // В противном случае показываем диалоговое окно с ошибкой
                                            showInvalidDateDialog = true
                                        }
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
                Button(onClick = {
                    taskViewModel.onEvent(TaskEvent.SetTaskDateStart(selectedStartDate.toString()))
                    taskViewModel.onEvent(TaskEvent.SetTaskDateEnd(selectedEndDate.toString()))
                    if (selectedSectionId.value == null) {
                        showSectionNotSelectedDialog = true
                    } else if(taskState.title == null || taskState.title == "") {
                        showInvalidTitleDialog = true
                    }
                    else if (selectedEndDate != null && selectedStartDate != null && selectedEndDate!!.after(selectedStartDate)) {
                        // If the end date is after the start date, save the task
                        taskViewModel.onEvent(TaskEvent.SaveTask)
                        navController.navigate("${MainRoute.CurrentBoard.name}/${boardId}")
                    }else{
                        // Show error dialog if the end date is before the start date
                        showInvalidDateDialog = true
                        }
                    },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 50.dp)) {
                    Text(text = "Save", color = androidx.compose.ui.graphics.Color.Black)
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
    boardId: Long? = null,
    onItemSelected: (Long) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("") }
    val boards = viewModel.state.collectAsState().value.boards

    // If boardId is provided, set the selectedText based on it
    LaunchedEffect(boardId) {
        if (boardId != null) {
            val board = boards.find { it.id == boardId }
            selectedText = board?.title ?: ""
        }
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { if (boardId == null) isExpanded = it }
    ) {
        TextField(
            value = selectedText,
            onValueChange = {},
            label = { Text(text = label, modifier = Modifier.padding(end = 8.dp)) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { if (boardId == null) ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
        )

        // Only show the dropdown menu if boardId is null
        if (boardId == null) {
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
                            onItemSelected(boards[index].id)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
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
