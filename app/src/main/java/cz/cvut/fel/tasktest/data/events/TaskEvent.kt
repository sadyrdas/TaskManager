package cz.cvut.fel.tasktest.data.events

import cz.cvut.fel.tasktest.data.Note
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import java.util.Date

sealed interface TaskEvent {
    object SaveTask: TaskEvent
    data class SetTaskName(val name: String) : TaskEvent
    data class SetTaskDescription(val description: String, val id:Long) : TaskEvent
    data class SetTaskTag(val tag: Tag) : TaskEvent
    data class SetTaskDateStart(val dateStart: String) : TaskEvent
    data class SetTaskDateEnd(val dateEnd: String) : TaskEvent
    data class AddTaskComment(val comment: Note) : TaskEvent
    data class DeleteTaskComment(val comment: Note) : TaskEvent
    data class DeleteTaskTag(val tag: Tag) : TaskEvent
    data class DeleteTask(val id: Long) : TaskEvent
    data class SetTaskCover(val cover: String, val id: Long) : TaskEvent
    data class UpdateTaskTag(val tag: Tag) : TaskEvent
    data class SetPhoto(val photo: String, val id: Long) : TaskEvent
    data class updateDateStart(val dateStart: String, val id: Long) : TaskEvent
    data class updateDateEnd(val dateEnd: String, val id: Long) : TaskEvent
}