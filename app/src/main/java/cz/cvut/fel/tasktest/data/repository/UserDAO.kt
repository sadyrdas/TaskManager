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


    @Query("INSERT INTO user (userName) VALUES (:userName)")
    fun insertUserName(userName: String)

    @Query("UPDATE user SET userName = :userName WHERE id = :id")
    fun updateUserName(id: Long, userName: String)

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>
    @Query("DELETE FROM user")
    fun deleteAllUsers()
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(user: User)

    @Query("SELECT * FROM user WHERE userName = :userName")
    fun getUser(userName: String): User

}
