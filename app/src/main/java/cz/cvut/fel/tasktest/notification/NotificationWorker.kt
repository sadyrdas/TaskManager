package cz.cvut.fel.tasktest.notification

import android.Manifest
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.TaskNotification
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.repository.TaskNotificationDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NotificationWorker : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationWorker", "onReceive: Worker called")

        if (context == null){return}
        CoroutineScope(Dispatchers.IO).launch {
            val db = TaskifyDatabase.getDatabase(context.applicationContext)

            val notificationDAO = db.taskNotificationDAO()
            val taskDAO = db.taskDao()
            val tasks = taskDAO.getAllTasks()

            val tasksToBother = soonEnding(tasks)

            val notNotifiedTasks = filterNotificatedTasks(notificationDAO, tasksToBother)

            notifyAndUpdateDB(notNotifiedTasks, notificationDAO, context)
        }
    }

    private fun notifyAndUpdateDB(notNotifiedTasks: List<Task>, notificationDAO: TaskNotificationDAO, context: Context) {
        notNotifiedTasks.forEach{
            showNotification(context, it)
            notificationDAO.insert(TaskNotification(taskId = it.id, exceptedEndDate = it.endDate))
        }
    }

    private fun filterNotificatedTasks(notificationDAO: TaskNotificationDAO, tasks : List<Task>): List<Task> {
        val notifications = notificationDAO.getAll()
        val notificationsToDelete = mutableListOf<TaskNotification>()
        val taskMap: MutableMap<Long, Task?> = tasks.associateBy { it.id }.toMutableMap()


        notifications.forEach{notification->
            val associatedTask = taskMap[notification.taskId]
            if (associatedTask == null || associatedTask.endDate != notification.exceptedEndDate){
                notificationsToDelete.add(notification)
            }else{
                taskMap[notification.taskId] = null
            }
        }

        notificationDAO.deleteList(notificationsToDelete)
        return taskMap.toList().mapNotNull { it.second }
    }

    private fun soonEnding(tasks: List<Task>): List<Task> {
        val currentTime = Calendar.getInstance()
        currentTime.set(Calendar.HOUR_OF_DAY, 0) // Set time to midnight
        currentTime.set(Calendar.MINUTE, 0)
        currentTime.set(Calendar.SECOND, 0)
        currentTime.set(Calendar.MILLISECOND, 0)

        // Calculate cutoff date as tomorrow
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)
        tomorrow.set(Calendar.HOUR_OF_DAY, 0) // Set time to midnight
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)

        // Filter tasks based on startDate
        val filteredTasks = tasks.filter { task ->
            val tillDate = task.endDate
            if (tillDate.isNullOrEmpty()) {
                false // Exclude tasks with empty startDate
            } else {
                val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH)
                dateFormat.timeZone = TimeZone.getTimeZone("GMT+01:00")
                val date = dateFormat.parse(tillDate)

                // Extract date parts from the task's start date
                val taskDate = Calendar.getInstance()
                taskDate.timeInMillis = date?.time ?: 0
                taskDate.set(Calendar.HOUR_OF_DAY, 0) // Set time to midnight
                taskDate.set(Calendar.MINUTE, 0)
                taskDate.set(Calendar.SECOND, 0)
                taskDate.set(Calendar.MILLISECOND, 0)

                // Check if the task's start date is within the next day
                taskDate.before(tomorrow) && taskDate.after(currentTime)
            }
        }

        return filteredTasks
    }
    private fun showNotification(context: Context, taskToNotify: Task) {
        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, "taskify_notification")
            .setContentTitle("Taskify")
            .setContentText("Your task ${taskToNotify.title} ends soon!")
            .setSmallIcon(R.drawable.taskicon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(taskToNotify.id.toInt(),notification)
    }
}
