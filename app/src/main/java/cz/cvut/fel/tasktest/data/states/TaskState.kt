package cz.cvut.fel.tasktest.data.states

import cz.cvut.fel.tasktest.data.Note
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import java.util.Date

data class TaskState (
    val title: String = "",
    val description: String = "",
    val comments: List<Note> = emptyList(),
    val dateStart: Date = Date(),
    val dateEnd: Date = Date(),
    val tags: List<Tag> = emptyList(),
    val cover: String = "",
    val sectionid: Long = 0,
    val tagId: Long = 0,
    val tasks: List<Task> = emptyList()
)