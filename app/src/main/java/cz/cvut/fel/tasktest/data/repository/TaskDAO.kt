package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.tasktest.data.Note
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task

@Dao
interface TaskDAO {
    @Insert
    fun insertTask(task: Task)

    @Query("DELETE FROM task WHERE id = :id")
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

    @Query("UPDATE task SET cover = :cover WHERE id = :taskId")
    fun updateTaskCover(taskId: Long, cover: String)

    @Query("UPDATE task SET description = :description WHERE id = :taskId")
    fun updateTaskDescription(taskId: Long, description: String)
}