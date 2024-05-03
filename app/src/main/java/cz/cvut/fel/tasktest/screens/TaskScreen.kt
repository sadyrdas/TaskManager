package cz.cvut.fel.tasktest.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.cvut.fel.tasktest.CustomAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import cz.cvut.fel.tasktest.data.events.BoardEvent
import cz.cvut.fel.tasktest.data.events.TaskEvent
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import cz.cvut.fel.tasktest.ui.theme.Primary
import kotlinx.coroutines.launch
import java.util.Date


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(drawerState: DrawerState, taskViewModel: TaskViewModel, navController: NavController, taskId:Long) {
    var isEditingDescription by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val bottomSheetState = androidx.compose.material3.rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    var openDatePicker by remember { mutableStateOf(false) }
    var openEndDatePicker by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<Date?>(null) }
    var selectedEndDate by remember { mutableStateOf<Date?>(null) }

    var showConfirmDialogAboutDeleteBoard by remember { mutableStateOf(false) }

    val taskState by taskViewModel.taskState

    val dataStart:String = taskState?.dateStart ?: "Starting.."
    val dataEnd:String = taskState?.dateEnd ?: "Ending"

    LaunchedEffect(taskId) {
        taskViewModel.getTaskState(taskId)
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                taskViewModel.handleImageSelection(taskId, context, uri)
            }
        }
    )

    val title = taskState?.title
    val description = taskState?.description
    var editedDescription by remember { mutableStateOf(description ?: "") }


    Scaffold(
        topBar = {
            if (title != null) {
                CustomAppBar(
                    drawerState = drawerState,
                    title = title,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.Default.ArrowBack,
                    navigationAction = {
                        navController.popBackStack()
                    }
                )
            }
        },

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val imageUri = taskState?.cover
            Box() {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Background",
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp)
                        .padding(top = 40.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurface,
                            MaterialTheme.shapes.extraLarge
                        ),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp) // Отступы слева и сверху
            ) {
                Text(
                    text = "Quick Actions",
                    modifier = Modifier.weight(1f) // Растягиваем текст на всю доступную ширину
                )
                Spacer(modifier = Modifier.width(8.dp)) // Отступ между текстом и иконкой
                Icon(
                    imageVector = if (isDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Filter Icon",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = {
                            isDropdownExpanded = !isDropdownExpanded
                        }) // Изменение состояния при нажатии
                )
            }
            if (isDropdownExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (showConfirmDialogAboutDeleteBoard){
                        AlertDialog(
                            onDismissRequest = {
                                // Dismiss the dialog when the user taps outside or on the back button
                                showConfirmDialogAboutDeleteBoard = false
                            },
                            title = { Text("Confirmation") },
                            text = { Text("Are you sure you want to delete this board?.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showConfirmDialogAboutDeleteBoard = false
                                        taskViewModel.onEvent(TaskEvent.DeleteTask(taskId))
                                    }
                                ) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showConfirmDialogAboutDeleteBoard = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                    Button(
                        onClick = {
                            showConfirmDialogAboutDeleteBoard = true},
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                            .width(150.dp)
                    ) {
                        Text("Delete Task")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                            .width(150.dp)
                    ) {
                        Text("Add cover")
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.Red, // Цвет разделителя
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Icon(
                    Icons.Filled.List, contentDescription = "Description Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )



                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        if (description != null) {
                            Text(text = description)
                            TextField(
                                value = editedDescription,
                                onValueChange = { newDescription ->
                                    editedDescription = newDescription
                                    taskViewModel.onEvent(TaskEvent.SetTaskDescription(newDescription))
                                },
                                placeholder = {
                                    if (description.isEmpty()) {
                                        Text("Add description..")
                                    }
                                },
                                modifier = Modifier
                                    .width(320.dp)
                            )
                        }
                        IconButton(onClick = { taskViewModel.onEvent(TaskEvent.SetTaskDescription(editedDescription)) }) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.Black
                            )

                        }
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.Red, // Цвет разделителя
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Icon(
                    Icons.Filled.Warning, contentDescription = "Tag Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = "Tags..",
                    modifier = Modifier.clickable { /*...*/ }
                )
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.Red, // Цвет разделителя
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.DateRange, contentDescription = "Tag Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(

                        text = dataStart,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .clickable { openDatePicker = true }

                    )
                    Divider(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .width(320.dp)
                    )
                    Text(text = dataEnd, modifier = Modifier.clickable { openEndDatePicker = true })
                }
                if (openDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
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
                    val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
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
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                // Круглая маленькая аватарка
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    // аватара сюда
                }

                // Текстовое поле с плейсхолдером "Add comment"
                TextField(
                    value = "", // Ваше значение комментария
                    onValueChange = { /* Обработчик изменения значения комментария */ },
                    placeholder = { Text("Add comment") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    shape = MaterialTheme.shapes.extraLarge
                )

                // Иконка отправки
                IconButton(
                    onClick = { /* Действие при отправке комментария */ }
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black // Цвет иконки
                    )
                }

                // Иконка вложения
                IconButton(
                    onClick = {
                        isDrawerOpen = true
                    }
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Attachment",
                        tint = Color.Black // Цвет иконки
                    )
                }
            }

            if (isDrawerOpen) {
                ModalBottomSheet(
                    onDismissRequest = { isDrawerOpen = false },
                    sheetState = bottomSheetState,
                    containerColor = Primary,
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        Column() {
                            Text(
                                text = "Add attachments",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    bottom = 16.dp
                                )
                            )

                            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope
                                        .launch { bottomSheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!bottomSheetState.isVisible) {
                                                isDrawerOpen = false
                                            }
                                        }
                                }) {
                                Text(
                                    text = "Add photo",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(16.dp)
                                )
                            }

                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope
                                        .launch { bottomSheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!bottomSheetState.isVisible) {
                                                isDrawerOpen = false
                                            }
                                        }
                                }) {
                                Text(
                                    text = "Add audio",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(16.dp)
                                )
                            }
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope
                                        .launch { bottomSheetState.hide() }
                                        .invokeOnCompletion {
                                            if (!bottomSheetState.isVisible) {
                                                isDrawerOpen = false
                                            }
                                        }
                                }) {
                                Text(
                                    text = "Add file",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

//@Preview(showBackground =  true)
//@Composable
//fun TaskScreenPreview() {
//    TaskScreen(drawerState = rememberDrawerState(DrawerValue.Closed))
//}