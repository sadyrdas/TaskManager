package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.tasktest.data.TaskNotification

@Dao
interface TaskNotificationDAO {
    @Insert
    fun insert(taskNotification: TaskNotification)

    @Delete
    fun delete(taskNotification: TaskNotification)

    @Query("SELECT * FROM task_notification")
    fun getAll(): List<TaskNotification>

    @Query("SELECT * FROM task_notification WHERE id = :id")
    fun getById(id: Long): TaskNotification

    @Query("SELECT * FROM task_notification WHERE taskId = :taskId")
    fun getByTaskId(taskId: Long) : List<TaskNotification>

    @Delete
    fun deleteList(notificationsToDelete: MutableList<TaskNotification>)
}