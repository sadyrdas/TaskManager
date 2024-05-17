package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.tasktest.data.User

@Dao
interface UserDAO {

    @Insert
    fun insertUser(user: User)


    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>
    @Query("DELETE FROM user")
    fun deleteAllUsers()

    @Query("SELECT * FROM user WHERE userName = :userName")
    fun getUser(userName: String): User
}
