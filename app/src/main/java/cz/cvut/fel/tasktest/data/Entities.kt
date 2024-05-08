package cz.cvut.fel.tasktest.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "task", foreignKeys = [ForeignKey(entity = Section::class, parentColumns = arrayOf("id"), childColumns = arrayOf("sectionId"), onDelete = ForeignKey.CASCADE)])
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val sectionId: Long,
    val cover: String? = ""
)

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val taskId: Long

)

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val background: String
)

@Entity(tableName = "task_tag_cross_ref", primaryKeys = ["taskId", "tagId"])
data class TaskTagCrossRef(
    val taskId: Long,
    val tagId: Long
)


@Entity(tableName = "board")
data class Board(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val background: String
)

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userName: String,
    val background: String
)

@Entity(tableName = "section", foreignKeys = [ForeignKey(entity = Board::class, parentColumns = arrayOf("id"), childColumns = arrayOf("boardId"), onDelete = ForeignKey.CASCADE)])
data class Section(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val boardId: Long
)