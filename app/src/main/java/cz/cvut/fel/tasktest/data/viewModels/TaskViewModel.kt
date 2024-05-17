package cz.cvut.fel.tasktest.data.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import cz.cvut.fel.tasktest.data.Converters
import cz.cvut.fel.tasktest.data.Photos
import cz.cvut.fel.tasktest.data.SortTypeForBoard
import cz.cvut.fel.tasktest.data.SortTypeForTask
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.events.BoardEvent
import cz.cvut.fel.tasktest.data.events.TaskEvent
import cz.cvut.fel.tasktest.data.repository.PhotoDAO
import cz.cvut.fel.tasktest.data.repository.TaskDAO
import cz.cvut.fel.tasktest.data.states.BoardState
import cz.cvut.fel.tasktest.data.states.TaskState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.UUID

class TaskViewModel(
    private val taskDAO: TaskDAO,
    private val photoDAO: PhotoDAO
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    private var currentSortState: SortTypeForTask = SortTypeForTask.UNSORTED
    val state: StateFlow<TaskState> = _state.asStateFlow()
    private val _taskState = mutableStateOf<TaskState?>(null)
    val taskState: State<TaskState?> = _taskState
    private val _tagsForTask = MutableStateFlow<List<Tag>>(emptyList())
    private val _photosForTask = MutableStateFlow<List<Photos>>(emptyList())
    val tagsForTask: StateFlow<List<Tag>> = _tagsForTask.asStateFlow()
    val photosForTask: StateFlow<List<Photos>> = _photosForTask.asStateFlow()
    val navController: NavController? = null



    private val _tagsForTaskMap = MutableStateFlow<Map<Long, List<Tag>>>(emptyMap())
    val tagsForTaskMap: StateFlow<Map<Long, List<Tag>>> get() = _tagsForTaskMap

    fun fetchTagsForTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val tags = taskDAO.getTagsForTask(taskId)
            _tagsForTaskMap.update { currentMap ->
                currentMap + (taskId to tags)
            }
        }
    }

    fun fetchPhotosForTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val photos = photoDAO.getPhotosForTask(taskId)
            // Обновляем состояние для отображения списка фото
            _photosForTask.value = photos
        }
    }


    init {
        fetchTasks() // Fetch boards when ViewModel is initialized
    }

    fun fetchDates(taskId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val task = taskDAO.getTaskById(taskId)
            launch(Dispatchers.Main) {
                _state.update { it.copy(dateStart = task.startDate.toString(), dateEnd = task.endDate.toString()) }
            }
        }
    }


    fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = taskDAO.getAllTasks()
            launch(Dispatchers.Main) {
                _state.update { it.copy(tasks = tasks) }
            }
        }
    }

    fun handleImageSelection(taskId: Long, context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePath = saveImageToInternalStorage(context, uri)
            withContext(Dispatchers.Main) {
                updateTaskCover(taskId, imagePath)
            }
        }
    }

    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val directory = File(context.filesDir, "taskCover_images") // Path to the directory
        if (!directory.exists()) {
            directory.mkdirs() // Create the directory if it does not exist
        }

        // Now create the file within this directory
        val file = File(directory, "${UUID.randomUUID()}.jpg")
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }

        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output) ?: throw IllegalStateException("Couldn't copy file")
            }
        }

        return file.absolutePath // Return the file path
    }

    fun sortTasks(sortType: SortTypeForTask) {
        currentSortState = sortType
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = when (sortType) {
                SortTypeForTask.UNSORTED -> taskDAO.getAllTasks()
                SortTypeForTask.START_DATA_ASC -> taskDAO.getTasksSortedByStartDateAsc()
                SortTypeForTask.END_DATA_DESC -> taskDAO.getTasksSortedByEndDateDesc()
                SortTypeForTask.TITLE_ASC -> taskDAO.getTasksSortedByTitleAsc()
                SortTypeForTask.TITLE_DESC -> taskDAO.getTasksSortedByTitleDesc()
                SortTypeForTask.START_DATA_DESC -> taskDAO.getTasksSortedByStartDateDesc()
                SortTypeForTask.END_DATA_ASC -> taskDAO.getTasksSortedByEndDateAsc()
            }
            launch(Dispatchers.Main) {
                _state.update { it.copy(tasks = tasks) }
            }
        }
    }

    private fun updateTaskCover(taskId: Long, cover: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.updateTaskCover(taskId, cover)
            fetchTasks()
        }
    }
    private fun updateTaskDescription(taskId: Long, description: String) {
        Log.d("TaskViewModel", "Updating task description: taskId=$taskId, description=$description")
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.updateTaskDescription(taskId, description)
            // Fetch the updated task from the database
            val updatedTask = taskDAO.getTaskById(taskId)
            Log.d("TaskViewModel", "Updated task: $updatedTask")
            // Update the UI state if the task exists and its description is not null
            updatedTask?.let { task ->
                val newDescription = task.description ?: ""
                _taskState.value = _taskState.value?.copy(description = newDescription)
            }
        }
    }

    fun addTagsToTask(taskId: Long, tagIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            // Insert the new tags for the task
            taskDAO.insertTagsForTask(taskId, tagIds)

            // Refresh tasks after updating tags
            fetchTasks()
        }
    }



    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.SaveTask -> {
                val title = _state.value.title
                val description = _state.value.description
                val dateStart = _state.value.dateStart
                val dateEnd = _state.value.dateEnd
                val sectionid = _state.value.sectionid
                val task = Task(title = title, description = description,
                    startDate = dateStart,
                    endDate = dateEnd,
                    sectionId = sectionid
                    )
                viewModelScope.launch(Dispatchers.IO) {
                    taskDAO.insertTask(task)
                    launch(Dispatchers.Main) {
                        _state.update { it.copy(title = "", description = "", dateStart = "", dateEnd = "") }
                    }
                }
            }
            is TaskEvent.SetTaskName -> {
                _state.update { it.copy(title = event.name) }
            }
            is TaskEvent.SetTaskDescription -> {
                viewModelScope.launch(Dispatchers.IO) {
                    updateTaskDescription(event.id, event.description)
                }
                _state.update { it.copy(description = event.description) }
            }
            is TaskEvent.SetTaskDateStart -> {
                _state.update { it.copy(dateStart = event.dateStart) }
            }
            is TaskEvent.SetTaskDateEnd -> {
                _state.update { it.copy(dateEnd = event.dateEnd) }
            }

            is TaskEvent.AddTaskComment -> TODO()
            is TaskEvent.DeleteTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    taskDAO.deleteTask(event.id)
                    fetchTasks()
                }
            }
            is TaskEvent.DeleteTaskComment -> TODO()
            is TaskEvent.DeleteTaskTag -> TODO()
            is TaskEvent.SetTaskTag -> TODO()
            is TaskEvent.UpdateTaskTag -> TODO()
            is TaskEvent.updateDateStart -> {
                viewModelScope.launch(Dispatchers.IO) {
                    taskDAO.updateDateStart(event.id, event.dateStart)
                    val updatedTask = taskDAO.getTaskById(event.id)
                    updatedTask?.let { task ->
                        val newTaskState = _taskState.value?.copy(dateStart = task.startDate.toString())
                        _taskState.value = newTaskState
                    }
                }
            }
            is TaskEvent.updateDateEnd -> {
                viewModelScope.launch(Dispatchers.IO) {
                    taskDAO.updateDateEnd(event.id, event.dateEnd)
                    val updatedTask = taskDAO.getTaskById(event.id)
                    updatedTask?.let { task ->
                        val newTaskState = _taskState.value?.copy(dateEnd = task.endDate.toString())
                        _taskState.value = newTaskState
                    }
                }

            }
            is TaskEvent.SetTaskCover -> {
                viewModelScope.launch(Dispatchers.IO) {
                    taskDAO.updateTaskCover(event.id, event.cover)
                    val updatedTask = taskDAO.getTaskById(event.id)
                    updatedTask?.let { task ->
                        val newTaskState = task.cover?.let { _taskState.value?.copy(cover = it) }
                        _taskState.value = newTaskState
                    }

                }
            }
            is TaskEvent.SetPhoto -> {
                viewModelScope.launch(Dispatchers.IO) {
                    photoDAO.savePhotoToTask(event.id, event.photo)
                }
            }
        }
    }

    fun getTaskState(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch the task data from the repository based on the taskId
            val taskData = taskDAO.getTaskById(taskId)
            if (taskData != null) { // Check if taskData is not null
                // Convert Task to TaskState
                val stateOfTask = taskData.cover?.let {
                    taskData.startDate?.let { it1 ->
                        taskData.endDate?.let { it2 ->
                            TaskState(
                                taskData.title,
                                taskData.description,
                                0,
                                emptyList(),
                                it1,
                                it2,
                                emptyList(),
                                it,
                                0
                            )
                        }
                    }
                }
                // Update the _taskState mutable state
                _taskState.value = stateOfTask
            }
        }
    }

    fun updateDateStart(taskId: Long, dateStart: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.updateDateStart(taskId, dateStart)
            fetchTasks()
        }
    }

    fun updateDateEnd(taskId: Long, dateEnd: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDAO.updateDateEnd(taskId, dateEnd)
            fetchTasks()
        }
    }




    fun getPhotos(taskId: Long) {
        viewModelScope.launch {
            try {
                // Получаем список фото для задачи из базы данных через репозиторий
                val photos = photoDAO.getPhotosForTask(taskId)
                // Обновляем состояние для отображения списка фото
                // _photosState.value = photos
            } catch (e: Exception) {
                // Обработка ошибок, если не удалось получить список фото
            }
        }
    }

}