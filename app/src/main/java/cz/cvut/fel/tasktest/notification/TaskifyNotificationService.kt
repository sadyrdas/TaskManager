package cz.cvut.fel.tasktest.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.repository.TaskNotificationDAO
import kotlin.random.Random
import android.app.PendingIntent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class TaskifyNotificationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "taskify_notification"
//    private val PERIODIC_INTERVAL_MILLIS = 5 * 60 * 1000L // 5 minutes
    private val PERIODIC_INTERVAL_MILLIS = 1 * 30 * 1000L // 5 minutes
    private lateinit var notificationManager: NotificationManager
    private lateinit var taskNotificationDao: TaskNotificationDAO

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        taskNotificationDao = TaskifyDatabase.getDatabase(applicationContext).taskNotificationDAO()
        createNotificationChannel()

    }

    private fun isForegroundServiceAllowed(): Boolean {
        // Check if the app has the necessary permission to run a foreground service
        val manager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = manager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(), packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var returnValue = START_NOT_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isForegroundServiceAllowed()) {
                // Foreground service is not allowed, handle it here
                // You may prompt the user to allow foreground service or take appropriate action
                returnValue = START_STICKY// Or any appropriate action
                startForeground(startId, createNotification())
            }else{
                requestForegroundServicePermission()
            }
        }
        schedulePeriodicWork()
        Log.d("TaskifyNotificationService", "AAAAA: PERMISSISON")

        return returnValue
    }
    private fun requestForegroundServicePermission() {
        // You can create and show a dialog here explaining why the permission is needed
        // Then, launch the system permission request dialog

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Taskify Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun createNotification(): Notification {
        // Create and return a notification for the foreground service
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Taskify")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.taskicon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()
    }

    private fun schedulePeriodicWork() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, NotificationWorker::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            PERIODIC_INTERVAL_MILLIS,
            pendingIntent
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        // Cleanup resources or perform any necessary tasks when the service is stopped.
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}