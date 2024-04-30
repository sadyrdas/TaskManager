package cz.cvut.fel.tasktest.data

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromDateToString(date: Date?): String? {
        return date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(it) }
    }

    @TypeConverter
    fun fromStringToDate(dateString: String?): Date? {
        return dateString?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it) }
    }
}
