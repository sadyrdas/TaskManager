package cz.cvut.fel.tasktest.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagViewModel(
    private val tagDAO: TagDAO
) : ViewModel(){
    private val _state = MutableStateFlow(TagState())
    val state: StateFlow<TagState> = _state.asStateFlow()

    fun fetchTags(){
        viewModelScope.launch(Dispatchers.IO) {
            val tags = tagDAO.getAll()
            _state.update { it.copy(tags = tags) }
        }
    }


    fun onEvent(event: TagEvent){
        when(event){
            is TagEvent.SetTagName -> {
                _state.value = state.value.copy(name = event.name)
            }
            is TagEvent.SetTagBackground -> {
                val hexCode = "#${event.background}"
                _state.value = state.value.copy(background = hexCode)
            }
            is TagEvent.SaveTag -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val name = state.value.name
                    val background = state.value.background
                    val tag = Tag(name = name, background = background)

                    tagDAO.insert(tag)
                    launch(Dispatchers.Main) {
                        _state.update { it.copy(name = "", background = "") }
                    }
                    fetchTags()
                }
            }
            is TagEvent.DeleteTag -> {
                viewModelScope.launch(Dispatchers.IO) {
                    tagDAO.delete(event.tag)
                    fetchTags()
                }
            }
        }
    }
}