package cz.cvut.fel.tasktest.data

import java.util.Date

data class TaskState (
    val title: String = "",
    val description: String = "",
    val comments: List<Note> = emptyList(),
    val dateStart: Date = Date(),
    val dateEnd: Date = Date(),
    val tags: List<Tag> = emptyList(),
    val cover: String = ""
)