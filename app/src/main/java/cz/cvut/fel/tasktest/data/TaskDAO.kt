package cz.cvut.fel.tasktest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDAO {
    @Insert
    fun insertTask(task: Task)

    @Query("DELETE FROM task WHERE id = :id and id != 0")
    fun deleteTask(id: Long)

    @Query("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskById(id: Long): Task

    @Query("SELECT * FROM task WHERE tagId = :tagId")
    fun getTasksByTagId(tagId: Long): List<Task>

    @Insert
    fun insertNote(note: Note)

    @Insert
    fun insertTag(tag: Tag)

    @Query("SELECT * FROM note WHERE taskId = :taskId")
    fun getNotesForTask(taskId: Long): List<Note>

    @Query("SELECT tagId FROM task WHERE id = :taskId")
    fun getTagsForTask(taskId: Long): List<Long>
}