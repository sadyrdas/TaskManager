package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.tasktest.data.Section

@Dao
interface SectionDAO {
    @Insert
    fun insert(section: Section)

    @Query("SELECT * FROM section")
    fun getAll(): List<Section>

    @Query("SELECT * FROM section WHERE id = :id")
    fun getById(id: Long): Section
    @Delete
    fun delete(section: Section)

    @Query("SELECT * FROM section WHERE boardId = :boardId")
    fun getSectionsByBoardId(boardId: Long): List<Section>

}