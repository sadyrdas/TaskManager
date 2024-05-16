package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cz.cvut.fel.tasktest.data.Note
import cz.cvut.fel.tasktest.data.Photos
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.TaskTagCrossRef

@Dao
interface TaskDAO {
    @Insert
    fun insertTask(task: Task): Long

    @Query("DELETE FROM task WHERE id = :id")
    fun deleteTask(id: Long)

    @Query("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskById(id: Long): Task

    @Insert
    fun insertNote(note: Note)

    @Insert
    fun insertTag(tag: Tag): Long


    @Query("SELECT * FROM tag INNER JOIN task_tag_cross_ref ON tag.id = task_tag_cross_ref.tagId WHERE task_tag_cross_ref.taskId = :taskId")
    fun getTagsForTask(taskId: Long): List<Tag>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTaskTagCrossRef(taskTagCrossRef: TaskTagCrossRef)

    // Other DAO methods...

    // Method to insert tags for an existing task
    @Transaction
    fun insertTagsForTask(taskId: Long, tagIds: List<Long>) {
        // Iterate over tag IDs
        for (tagId in tagIds) {
            // Insert task-tag cross-reference
            insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))
        }
    }


    @Query("SELECT * FROM note WHERE taskId = :taskId")
    fun getNotesForTask(taskId: Long): List<Note>

    @Query("UPDATE task SET cover = :cover WHERE id = :taskId")
    fun updateTaskCover(taskId: Long, cover: String)

    @Query("UPDATE task SET description = :description WHERE id = :taskId")
    fun updateTaskDescription(taskId: Long, description: String)
}