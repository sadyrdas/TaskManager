package cz.cvut.fel.tasktest.data.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.tasktest.data.Board
import cz.cvut.fel.tasktest.data.SortTypeForBoard
import cz.cvut.fel.tasktest.data.events.BoardEvent
import cz.cvut.fel.tasktest.data.repository.BoardDAO
import cz.cvut.fel.tasktest.data.states.BoardState
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

class BoardViewModel(
    private val boardDAO: BoardDAO
) : ViewModel() {
    private val _state = MutableStateFlow(BoardState())
    private var currentSortState: SortTypeForBoard = SortTypeForBoard.UNSORTED
    val state: StateFlow<BoardState> = _state.asStateFlow()

    init {
        fetchBoards() // Fetch boards when ViewModel is initialized
    }



    fun fetchBoards() {
        viewModelScope.launch(Dispatchers.IO) {
            val boards = when (currentSortState) {
                SortTypeForBoard.SORTED_BY_TITLE_ASC -> boardDAO.getBoardSortedASCByTitle()
                else -> boardDAO.getAll()
            }
            _state.update { currentState ->
                currentState.copy(boards = boards)
            }
        }
    }

    fun sortBoardsByTitleAsc() {
        if (currentSortState != SortTypeForBoard.SORTED_BY_TITLE_ASC) {
            currentSortState = SortTypeForBoard.SORTED_BY_TITLE_ASC
            fetchBoards()
        }
    }

    fun handleImageSelection(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val imagePath = saveImageToInternalStorage(context, uri)
            withContext(Dispatchers.Main) {
                onEvent(BoardEvent.SetBoardBackground(imagePath))
            }
        }
    }

    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val directory = File(context.filesDir, "board_images") // Path to the directory
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

    fun onEvent(event: BoardEvent) {
        when (event) {
            is BoardEvent.SaveBoard -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val title = _state.value.title
                    val background = _state.value.background
                    val board = Board(title = title, background = background)
                    boardDAO.insert(board)
                    launch(Dispatchers.Main) {
                        _state.update { it.copy(title = "", background = "") }
                    }
                }
            }
            is BoardEvent.SetBoardTitle -> {
                _state.update { it.copy(title = event.name) }
            }
            is BoardEvent.SetBoardBackground -> {
                _state.update { it.copy(background = event.background) }
            }
            is BoardEvent.GetAllBoards -> {
                fetchBoards()
            }
            is BoardEvent.DeleteBoard -> {
                viewModelScope.launch(Dispatchers.IO) {
                    boardDAO.delete(event.board)
                    fetchBoards()
                }
            }
            is BoardEvent.ImageSelected -> {
                _state.update { it.copy(background = event.imagePath) }
            }
            is BoardEvent.SetSectionToBoard -> {
                val sections = _state.value.sections.toMutableList()
                sections.add(event.section)
                _state.update { it.copy(sections = sections) }
            }
            is BoardEvent.DeleteSectionFromBoard -> {
                val sections = _state.value.sections.toMutableList()
                sections.remove(event.section)
                _state.update { it.copy(sections = sections) }
            }
        }
    }
}
