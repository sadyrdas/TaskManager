package cz.cvut.fel.tasktest.data.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.tasktest.data.Converters
import cz.cvut.fel.tasktest.data.Section
import cz.cvut.fel.tasktest.data.events.SectionEvent
import cz.cvut.fel.tasktest.data.repository.SectionDAO
import cz.cvut.fel.tasktest.data.states.SectionState
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


    fun fetchSections(boardId:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val sections = sectionDAO.getSectionsByBoardId(boardId)
            _state.update { currentState ->
                currentState.copy(sections = sections)
            }
        }
    }

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