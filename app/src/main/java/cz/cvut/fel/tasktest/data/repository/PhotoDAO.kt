package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.tasktest.data.Photos

@Dao
interface PhotoDAO {

    @Query("INSERT INTO photos (taskId, photo) VALUES (:taskId, :photo)")
    fun savePhotoToTask(taskId: Long, photo: String)

    @Query("SELECT * FROM photos WHERE taskId = :taskId")
    fun getPhotosForTask(taskId: Long): List<Photos>
}