package cz.cvut.fel.tasktest.data.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.tasktest.data.events.UserEvent
import cz.cvut.fel.tasktest.data.repository.UserDAO
import cz.cvut.fel.tasktest.data.states.UserState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val userDAO: UserDAO
):
    ViewModel() {
    val state = MutableStateFlow(UserState())

    private fun fetchUsername() {
        viewModelScope.launch(Dispatchers.IO) {
            val username = userDAO.getUsername()  // Assume this method fetches the latest username
            withContext(Dispatchers.Main) {
                // Update the state with the latest username from the database
                state.value = state.value.copy(userName = username ?: "")
            }
        }
    }

    fun clearAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userDAO.deleteAllUsers()
        }
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.SetUsername -> {
                state.update { it.copy(userName = event.userName) }
            }
            is UserEvent.SaveUser -> {
                val userName = state.value.userName
                viewModelScope.launch(Dispatchers.IO) {
                    userDAO.insertUserName(userName)
                    fetchUsername() // Fetch the latest username after saving
                }
            }
        }
    }
}