package cz.cvut.fel.tasktest.data.states

import cz.cvut.fel.tasktest.data.Board
import cz.cvut.fel.tasktest.data.Section

data class BoardState(
    val title: String = "",
    val id:Long = 0,
    val background: String = "",
    val boards: List<Board> = emptyList(),
    val sections: List<Section> = emptyList()
)
