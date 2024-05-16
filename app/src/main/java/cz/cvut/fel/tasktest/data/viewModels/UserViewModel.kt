package cz.cvut.fel.tasktest.data.viewModels


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.tasktest.data.User
import cz.cvut.fel.tasktest.data.events.UserEvent
import cz.cvut.fel.tasktest.data.repository.UserDAO
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

    init {
        fetchUser()
    }


    private fun clearAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            userDAO.deleteAllUsers()
        }
    }

    private fun checkAndInitializeUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = userDAO.getAllUsers()
            val userState = if (users.isEmpty()) {
                // If no users found, insert default user
                val defaultUser = UserState(userName = "username", background = "defaultbackground")
                userDAO.insertUser(User(userName = "username", background = "defaultbackground"))
                defaultUser
            } else {
                // If users found, get the first user (assuming there's only one user for simplicity)
                val user = users.first()
                UserState(userName = user.userName, background = user.background)
            }
            _userState.value = userState
        }
    }



    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            userDAO.getAllUsers()
        }
        checkAndInitializeUserData()
    }

    private fun updateUser() {
        clearAllUsers()
        val userName = _userState.value.userName
        val background = _userState.value.background
        val user = User(userName = userName, background = background)
        viewModelScope.launch(Dispatchers.IO) {
            userDAO.insertUser(user)
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
                _userState.update { it.copy(userName = event.userName) }
            }
            is UserEvent.SaveUser -> {
                updateUser()
            }
            is UserEvent.SetBackground -> {
                _userState.update { it.copy(background = event.background) }
            }
            is UserEvent.ImageSelected -> {
                _userState.update { it.copy(background = event.imagePath) }
            }
        }

    }

}