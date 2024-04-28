package cz.cvut.fel.tasktest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

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

}