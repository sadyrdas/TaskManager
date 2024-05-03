package cz.cvut.fel.tasktest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.cvut.fel.tasktest.data.repository.BoardDAO
import cz.cvut.fel.tasktest.data.repository.SectionDAO
import cz.cvut.fel.tasktest.data.repository.TagDAO
import cz.cvut.fel.tasktest.data.repository.TaskDAO
import cz.cvut.fel.tasktest.data.repository.UserDAO

@Database(entities = [Task::class, Board::class, User::class, Tag::class, Section::class, Note::class, TaskTagCrossRef::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskifyDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDAO
    abstract fun boardDao(): BoardDAO
    abstract fun userDAO(): UserDAO
    abstract fun tagDAO(): TagDAO
    abstract fun sectionDAO(): SectionDAO
    companion object {
        private const val DATABASE_NAME = "taskify_database"

        @Volatile
        private var INSTANCE: TaskifyDatabase? = null

        fun getDatabase(context: Context): TaskifyDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, TaskifyDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }

        }
    }

}