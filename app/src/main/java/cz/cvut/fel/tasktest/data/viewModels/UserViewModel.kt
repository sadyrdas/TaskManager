package cz.cvut.fel.tasktest.data.viewModels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.tasktest.data.User
import cz.cvut.fel.tasktest.data.events.UserEvent
import cz.cvut.fel.tasktest.data.repository.UserDAO
import cz.cvut.fel.tasktest.data.states.BoardState
import cz.cvut.fel.tasktest.data.states.UserState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class UserViewModel(
    private val userDAO: UserDAO
):
    ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState?> = _userState.asStateFlow()



    fun setUsername(newUsername: String) {
        _userState.value?.let { currentState ->
            _userState.value = currentState.copy(userName = newUsername)
        }
    }

    // Function to handle changes to the background image
    fun setBackground(newBackground: String) {
        _userState.value?.let { currentState ->
            _userState.value = currentState.copy(background = newBackground)
        }
    }

    // Function to save user changes to the database
    fun saveUserChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            _userState.value?.let { currentState ->
                val updatedUser = User(userName = currentState.userName, background = currentState.background)
                userDAO.insertOrUpdateUser(updatedUser)
            }
        }
    }

    fun clearAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userDAO.deleteAllUsers()
        }
    }

    private suspend fun checkAndInitializeUserData() {
        withContext(Dispatchers.IO) {
            val existingUserData = userDAO.getUser("Username")
            if (existingUserData == null) {
                // Database is empty or default user not created, insert default user data
                val defaultUserData = User(userName = "Username", background = "defaultBackground")
                userDAO.insertOrUpdateUser(defaultUserData)
                _userState.value = UserState(userName = defaultUserData.userName, background = defaultUserData.background)
            } else {
                // Database already has default user data, no need to insert
                _userState.value = UserState(userName = existingUserData.userName, background = existingUserData.background)
            }
        }
    }

    suspend fun fetchUser() {
        checkAndInitializeUserData() // Ensure default user is initialized

        viewModelScope.launch(Dispatchers.IO) {
            val user = userDAO.getUser("Username")
            if (user != null) {
                _userState.update { it.copy(userName = user.userName, background = user.background) }
            } else {
                // Handle the case where the user doesn't exist
                // For example, you might want to log an error or show a message to the user
            }
        }
    }



    fun handleImageSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePath = saveImageToInternalStorage(context, uri)
            withContext(Dispatchers.Main) {
                onEvent(UserEvent.SetBackground(imagePath))
            }
        }
    }

    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val directory = File(context.filesDir, "user_images") // Path to the directory
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
    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.SetUsername -> {
                setUsername(event.userName)
            }
            is UserEvent.SaveUser -> {
                val userName = _userState.value.userName
                val background = _userState.value.background
                val user = User(userName = userName, background = background)
                viewModelScope.launch(Dispatchers.IO) {
                    userDAO.insertOrUpdateUser(user)
                }
            }
            is UserEvent.SetBackground -> {
                setBackground(event.background)
            }
            is UserEvent.ImageSelected -> {
                _userState.update { it.copy(background = event.imagePath) }
            }
        }

    }

}