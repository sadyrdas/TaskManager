package cz.cvut.fel.tasktest.data.states

import cz.cvut.fel.tasktest.data.Section
import cz.cvut.fel.tasktest.data.Task

data class SectionState (
    val title: String = "",
    var deskId: Long = 0,
    val tasks: List<Task> = emptyList(),
    val sections: List<Section> = emptyList()
)