package cz.cvut.fel.tasktest.data.states

import cz.cvut.fel.tasktest.data.Task

data class SectionState (
    val title: String = "",
    val deskId: Long = 0,
    val tasks: List<Task> = emptyList()
)