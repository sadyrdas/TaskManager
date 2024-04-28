package cz.cvut.fel.tasktest.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SectionViewModel (
    private val sectionDAO: SectionDAO
): ViewModel() {


    private val _state = MutableStateFlow(SectionState())
    val state: StateFlow<SectionState> = _state.asStateFlow()
    val converters = Converters()

    fun onEvent(event: SectionEvent) {
        when (event) {
            is SectionEvent.SaveSection -> {
                val title = _state.value.title
                val deskId = _state.value.deskId
                val section = Section(title = title, boardId = deskId)
                viewModelScope.launch(Dispatchers.IO) {
                    sectionDAO.insert(section)
                    launch(Dispatchers.Main) {
                        _state.update { it.copy(title = "") }
                    }
                }
            }
            is SectionEvent.SetSectionName -> {
                _state.update { it.copy(title = event.name) }
            }
            is SectionEvent.SetSectionDeskId -> {
                _state.update { it.copy(deskId = event.deskId) }
            }
            is SectionEvent.SetTaskToSection -> {
                val tasks = _state.value.tasks.toMutableList()
                tasks.add(event.task)
                _state.update { it.copy(tasks = tasks) }
            }
            is SectionEvent.DeleteTaskFromSection -> {
                val tasks = _state.value.tasks.toMutableList()
                tasks.remove(event.task)
                _state.update { it.copy(tasks = tasks) }
            }
            is SectionEvent.DeleteSection -> {
                val section = event.section
                viewModelScope.launch(Dispatchers.IO) {
                    sectionDAO.delete(section)
                    launch(Dispatchers.Main) {
                        _state.update { it.copy(title = "") }
                    }
                }
            }
        }
    }
}