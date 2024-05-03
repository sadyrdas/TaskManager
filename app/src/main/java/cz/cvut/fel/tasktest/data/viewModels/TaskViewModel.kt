package cz.cvut.fel.tasktest.data.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.tasktest.data.Converters
import cz.cvut.fel.tasktest.data.SortTypeForBoard
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.events.TaskEvent
import cz.cvut.fel.tasktest.data.repository.TaskDAO
import cz.cvut.fel.tasktest.data.states.TaskState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskDAO: TaskDAO
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    private var currentSortState: SortTypeForBoard = SortTypeForBoard.UNSORTED
    val state: StateFlow<TaskState> = _state.asStateFlow()
    val converters = Converters()
    private val _taskState = mutableStateOf<TaskState?>(null)
    val taskState: State<TaskState?> = _taskState

    init {
        fetchTasks() // Fetch boards when ViewModel is initialized
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
                _state.update { it.copy(description = event.description) }
            }
            is TaskEvent.SetTaskDateStart -> {
                _state.update { it.copy(dateStart = event.dateStart) }
            }
            is TaskEvent.SetTaskDateEnd -> {
                _state.update { it.copy(dateEnd = event.dateEnd) }
            }

            is TaskEvent.AddTaskComment -> TODO()
            is TaskEvent.DeleteTask -> TODO()
            is TaskEvent.DeleteTaskComment -> TODO()
            is TaskEvent.DeleteTaskTag -> TODO()
            is TaskEvent.SetTaskTag -> TODO()
            is TaskEvent.UpdateTaskTag -> TODO()
        }
    }

    fun getTaskState(boardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // Fetch the board data from the repository based on the boardId
            val taskData = taskDAO.getTaskById(boardId)
            // Convert Board to BoardState
            val stateOfTask = TaskState(taskData.title)
            // Update the _boardState mutable state
            _taskState.value = stateOfTask
        }
    }

    fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = taskDAO.getAllTasks()
            _state.update { it.copy(tasks = tasks) }
        }
    }


}