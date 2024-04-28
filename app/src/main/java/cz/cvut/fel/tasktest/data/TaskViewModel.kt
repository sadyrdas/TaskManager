package cz.cvut.fel.tasktest.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(
    private val taskDAO: TaskDAO
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    private var currentSortState: SortTypeForBoard = SortTypeForBoard.UNSORTED
    val state: StateFlow<TaskState> = _state.asStateFlow()
    val converters = Converters()

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.SaveTask -> {
                val title = _state.value.title
                val description = _state.value.description
                val dateStart = _state.value.dateStart
                val dateEnd = _state.value.dateEnd
                val cover = _state.value.cover
                val sectionid = _state.value.sectionid
                val tagId = _state.value.tagId
                val task = Task(title = title, description = description,
                    startDate = converters.dateToTimestamp(dateStart) ,
                    endDate = converters.dateToTimestamp(dateEnd),
                    cover = cover, sectionId = sectionid, tagId = tagId
                    )
                viewModelScope.launch(Dispatchers.IO) {
                    taskDAO.insertTask(task)
                    launch(Dispatchers.Main) {
                        _state.update { it.copy(title = "", description = "", dateStart = Date(), dateEnd = Date()) }
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


}