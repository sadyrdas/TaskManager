package cz.cvut.fel.tasktest.data

import android.net.Uri

sealed interface BoardEvent {
    object SaveBoard : BoardEvent
    data class SetBoardTitle(val name: String) : BoardEvent
    data class SetBoardBackground(val background: String) : BoardEvent
    data class GetAllBoards(val boards: List<Board>) : BoardEvent
    data class DeleteBoard(val board: Board) : BoardEvent
    data class ImageSelected(val imagePath: String) : BoardEvent
    data class SetSectionToBoard(val section: Section) : BoardEvent
    data class DeleteSectionFromBoard(val section: Section) : BoardEvent

}