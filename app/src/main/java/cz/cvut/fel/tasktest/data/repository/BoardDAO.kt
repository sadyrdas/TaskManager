package cz.cvut.fel.tasktest.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.tasktest.data.Board

@Dao
interface BoardDAO {
    @Insert
    fun insert(board: Board)

    @Delete
    fun delete(board: Board)

    @Query("SELECT * FROM board")
    fun getAll(): List<Board>

    @Query("SELECT * FROM board WHERE id = :id")
    fun getById(id: Long): Board

    @Query("SELECT * FROM board ORDER BY title ASC")
    fun getBoardSortedASCByTitle(): List<Board>
}