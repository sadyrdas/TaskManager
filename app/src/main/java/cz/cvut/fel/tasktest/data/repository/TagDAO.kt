package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.tasktest.data.Tag

@Dao
interface TagDAO {
    @Insert
    fun insert(tag: Tag)

    @Delete
    fun delete(tag: Tag)

    @Query("SELECT * FROM tag")
    fun getAll(): List<Tag>
}