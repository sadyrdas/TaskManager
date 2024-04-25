package cz.cvut.fel.tasktest.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskDAO: TaskDAO
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    private var currentSortState: SortTypeForBoard = SortTypeForBoard.UNSORTED
    val state: StateFlow<TaskState> = _state.asStateFlow()
    val converters = Converters()

//    fun onEvent(event: TaskEvent) {
//        when (event) {
//            is TaskEvent.SaveTask -> {
//                val title = _state.value.title
//                val description = _state.value.description
//                val dateStart = _state.value.dateStart
//                val dateEnd = _state.value.dateEnd
//                val cover = _state.value.cover
//                val task = Task(title = title, description = description,
//                    startDate = converters.dateToTimestamp(dateStart) ,
//                    endDate = converters.dateToTimestamp(dateEnd))
//                viewModelScope.launch(Dispatchers.IO) {
//                    taskDAO.insertTask(task)
//                    launch(Dispatchers.Main) {
//                        _state.update { it.copy(title = "", description = "", dateStart = Date(), dateEnd = Date()) }
//                    }
//                }
//            }
//            is TaskEvent.SetTaskTitle -> {
//                _state.update { it.copy(title = event.title) }
//            }
//            is TaskEvent.SetTaskDescription -> {
//                _state.update { it.copy(description = event.description) }
//            }
//            is TaskEvent.SetTaskDateStart -> {
//                _state.update { it.copy(dateStart = event.dateStart) }
//            }
//            is TaskEvent.SetTaskDateEnd -> {
//                _state.update { it.copy(dateEnd = event.dateEnd) }
//            }
//        }
//    }


}