package cz.cvut.fel.tasktest.data

data class BoardState(
    val title: String = "",
    val background: String = "",
    val boards: List<Board> = emptyList(),
    val sections: List<Section> = emptyList()
)
