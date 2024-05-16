package cz.cvut.fel.tasktest.data.states

import cz.cvut.fel.tasktest.data.Note
import cz.cvut.fel.tasktest.data.Photos
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import java.util.Date

data class TaskState (
    val title: String = "",
    val description: String = "",
    val id : Long = 0,
    val comments: List<Note> = emptyList(),
    val dateStart: String = "",
    val dateEnd: String = "",
    val tags: List<Tag> = emptyList(),
    val cover: String = "",
    var sectionid: Long = 0,
    val tagId: Long = 0,
    val tasks: List<Task> = emptyList(),
    val photo: String = "",
    val photos: List<Photos> = emptyList()
)