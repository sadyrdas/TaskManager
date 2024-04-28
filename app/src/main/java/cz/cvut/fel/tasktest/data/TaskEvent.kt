package cz.cvut.fel.tasktest.data

import java.util.Date

sealed interface TaskEvent {
    object SaveTask: TaskEvent
    data class SetTaskName(val name: String) : TaskEvent
    data class SetTaskDescription(val description: String) : TaskEvent
    data class SetTaskTag(val tag: Tag) : TaskEvent
    data class SetTaskDateStart(val dateStart: Date) : TaskEvent
    data class SetTaskDateEnd(val dateEnd: Date) : TaskEvent
    data class AddTaskComment(val comment: Note) : TaskEvent
    data class DeleteTaskComment(val comment: Note) : TaskEvent
    data class DeleteTaskTag(val tag: Tag) : TaskEvent
    data class DeleteTask(val task: Task) : TaskEvent
    data class UpdateTaskTag(val tag: Tag) : TaskEvent
}