package cz.cvut.fel.tasktest.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.events.BoardEvent
import cz.cvut.fel.tasktest.data.events.TaskEvent
import cz.cvut.fel.tasktest.data.viewModels.CameraView
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import cz.cvut.fel.tasktest.ui.theme.Primary
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(drawerState: DrawerState, taskViewModel: TaskViewModel, tagViewModel: TagViewModel, navController: NavController, taskId:Long) {
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
    var showInvalidDateDialog by remember { mutableStateOf(false) }

    var showConfirmDialogAboutDeleteBoard by remember { mutableStateOf(false) }

    val taskState by taskViewModel.taskState

    val dataStart:String = taskState?.dateStart ?: "Starting.."
    val dataEnd:String = taskState?.dateEnd ?: "Ending"

    val tagsForTask by taskViewModel.tagsForTask.collectAsState()

    LaunchedEffect(taskId) {
        taskViewModel.fetchTagsForTask(taskId)
    }
    val photosForTask by taskViewModel.photosForTask.collectAsState()
    LaunchedEffect(taskId){
        taskViewModel.getPhotos(taskId)
    }


    LaunchedEffect(taskId) {
        taskViewModel.getTaskState(taskId)
    }
    LaunchedEffect(taskId) { // key1 = true ensures this only runs once when the composable enters the composition
        taskViewModel.fetchPhotosForTask(taskId) // Call fetch boards if not automatically handled in ViewModel init
    }

    LaunchedEffect(key1 = null) {
        taskViewModel.fetchTasks()
    }

    LaunchedEffect(taskId) {
        taskViewModel.fetchDates(taskId)
    }

    var isEditTitleDialogOpen by remember { mutableStateOf(false) }

    fun toggleEditTitleDialog() {
        isEditTitleDialogOpen = !isEditTitleDialogOpen
    }

    // State to manage dialog visibility
    var isDialogOpen by remember { mutableStateOf(false) }

    // Function to open/close the dialog
    fun toggleDialog() {
        isDialogOpen = !isDialogOpen
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                taskViewModel.handleImageSelection(taskId, context, uri)
                TaskEvent.SetTaskCover(uri.toString(), taskId)
            }
        }
    )

    val title = taskState?.title
    val description = taskState?.description
    var editedDescription by remember { mutableStateOf(description ?: "") }

    // image state
    var selectedImageUri:Uri? by remember { mutableStateOf(Uri.parse(taskState?.photo ?: "" )) }
    var showCamera by remember { mutableStateOf(false) }

    fun getOutputDirectory(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if ((mediaDir != null) && mediaDir.exists()) mediaDir else context.filesDir
    }

    fun handleImageCapture(uri: Uri) {
        selectedImageUri = uri
        showCamera = false
        taskViewModel.onEvent(TaskEvent.SetPhoto(uri.toString(), taskId))
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

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
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Круглая маленькая аватарка
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .align(Alignment.CenterVertically)
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
            }
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
        if (isEditTitleDialogOpen == true) {
            EditTitleDialog(
                isOpen = isEditTitleDialogOpen,
                currentTitle = title ?: "",
                onConfirm = { newTitle ->
                    taskViewModel.onEvent(TaskEvent.editTaskTitle(newTitle, taskId))
                },
                onDismiss = { toggleEditTitleDialog() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState(), reverseScrolling = true)
        ) {
            val imageUri = taskState?.cover
            Box() {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Background",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
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
                                        navController.navigate(MainRoute.Boards.name)
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
                        onClick = { toggleEditTitleDialog()
                             },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                            .width(150.dp)
                    ) {
                        Text("Edit title")
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.DarkGray, // Цвет разделителя
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
                            TextField(
                                value = editedDescription,
                                onValueChange = { newDescription ->
                                    editedDescription = newDescription
                                    taskViewModel.onEvent(TaskEvent.SetTaskDescription(newDescription, taskId))                                },
                                placeholder = {
                                    if (description.isEmpty()) {
                                        Text("Add description..")
                                    }
                                    Text(description)
                                },
                                modifier = Modifier
                                    .width(320.dp)
                            )
                        }
                        IconButton(onClick = { taskViewModel.onEvent(TaskEvent.SetTaskDescription(editedDescription, taskId)) }) {
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
                color = Color.DarkGray // Цвет разделителя
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Row {
                    Text("Tags:", modifier = Modifier
                        .padding(start = 16.dp)
                        .clickable { toggleDialog() })
                    Row(modifier = Modifier.padding(start = 16.dp, top = 8.dp)) {
                        tagsForTask.forEach { tag ->
                            Text(
                                tag.name,
                                modifier = Modifier
                                    .padding(end = 8.dp) // Add padding between tags
                                    .background(Color(android.graphics.Color.parseColor(tag.background)))
                            )
                        }
                    }
                }
                if (isDialogOpen) {
                    TagsDialog(
                        tagViewModel,
                        taskViewModel,
                        taskId,
                        onDismiss = { toggleDialog() },
                        navController
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.DarkGray // Цвет разделителя
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
                    if (dataStart != "Starting.."){
                        Text(
                            text = convertToDate(dataStart),
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable { openDatePicker = true })

                    } else {
                        Text(
                            text = "Starting..",
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable { openDatePicker = true })
                    }
                    Divider(
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .width(320.dp)
                    )
                    if (dataEnd != "Ending"){
                        Text(
                            text = convertToDate(dataEnd),
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable { openEndDatePicker = true })
                    } else {
                        Text(
                            text = "Ending",
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .clickable { openEndDatePicker = true })
                    }

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
                                taskViewModel.onEvent(TaskEvent.updateDateStart(selectedStartDate.toString(), taskId))
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

                                if (selectedEndDate != null && fromStringToDate(dataStart) != null && selectedEndDate!!.after(fromStringToDate(dataStart))) {
                                    // Если выбранная дата конца больше даты начала, то сохраняем её
                                    taskViewModel.onEvent(TaskEvent.updateDateEnd(selectedEndDate.toString(), taskId))
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


            photosForTask.forEach(){photos ->
                Image(
                    painter = rememberAsyncImagePainter(photos.photo),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 16.dp),
                    contentScale = ContentScale.Crop
                )
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
                                ),
                                color = Color.Black
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
                                        .clickable
                                        {
                                            if (!hasCameraPermission) {
                                                permissionLauncher.launch(Manifest.permission.CAMERA)
                                            } else {
                                                showCamera = true
                                                isDrawerOpen = false
                                            }
                                        }
                                    , color = Color.Black
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
                                    text = "Add cover",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clickable { imagePickerLauncher.launch("image/*") },
                                    color = Color.Black
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
    if (showCamera) {
        CameraView(
            outputDirectory = getOutputDirectory(),
            executor = Executors.newSingleThreadExecutor(),
            onImageCaptured = ::handleImageCapture,
            onError = {},
            navController = navController
        )
    }
}

@Composable
fun EditTitleDialog(
    isOpen: Boolean,
    currentTitle: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newTitle by remember { mutableStateOf(currentTitle) } // Переменная для нового названия

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Title") },
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

fun fromStringToDate(dataStart: String): Date? {
    val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
    return try {
        format.parse(dataStart)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
@Composable
fun TagsDialog(
    tagViewModel: TagViewModel,
    taskViewModel: TaskViewModel,
    taskId: Long,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val tagState by tagViewModel.state.collectAsState()
    val tags = tagState.tags

    val selectedTagIds = remember { mutableStateListOf<Long>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Tags") },
        text = {
            LaunchedEffect(key1 = null) {
                tagViewModel.fetchTags()
            }
            LazyColumn {
                items(tags) { tag ->
                    val isSelected = selectedTagIds.contains(tag.id)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                if (isSelected) {
                                    selectedTagIds.remove(tag.id)
                                } else {
                                    selectedTagIds.add(tag.id)
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = tag.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTagIds.isNotEmpty()) {
                        taskViewModel.addTagsToTask(taskId, selectedTagIds)
                    } else {
                        navController.navigate(MainRoute.TagCreation.name)
                    }
                    taskViewModel.addTagsToTask(taskId, selectedTagIds)
                    onDismiss()
                }
            ) {
                Text("Add Tags")
            }
        }
    )
}

fun convertToDate(input: String): String {
    val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val date = dateFormat.parse(input)
    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return outputFormat.format(date)
}
