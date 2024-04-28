package cz.cvut.fel.tasktest.data

data class SectionState (
    val title: String = "",
    val deskId: Long = 0,
    val tasks: List<Task> = emptyList()
)