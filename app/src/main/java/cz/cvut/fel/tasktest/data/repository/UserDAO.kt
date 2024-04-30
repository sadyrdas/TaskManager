package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Query
import cz.cvut.fel.tasktest.data.User

@Dao
interface UserDAO {


    @Query("INSERT INTO user (userName) VALUES (:userName)")
    fun insertUserName(userName: String)

    @Query("UPDATE user SET userName = :userName WHERE id = :id")
    fun updateUserName(id: Long, userName: String)

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    @Query("SELECT userName FROM user ORDER BY id DESC LIMIT 1")
    fun getUsername(): String

    @Query("DELETE FROM user")
    fun deleteAllUsers()


}
