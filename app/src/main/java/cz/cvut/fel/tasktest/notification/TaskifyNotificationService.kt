package cz.cvut.fel.tasktest.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.repository.TaskNotificationDAO
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import android.Manifest

class TaskifyNotificationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "taskify_notification"
//    private val PERIODIC_INTERVAL_MILLIS = 5 * 60 * 1000L // 5 minutes
    private val PERIODIC_INTERVAL_MILLIS = 1 * 30 * 1000L // 30 sec
    private lateinit var notificationManager: NotificationManager
    private lateinit var taskNotificationDao: TaskNotificationDAO

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        taskNotificationDao = TaskifyDatabase.getDatabase(applicationContext).taskNotificationDAO()
        createNotificationChannel()

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var returnValue = START_NOT_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isForegroundServiceAllowed(this)) {
                // Foreground service is allowed, start foreground service
                returnValue = START_STICKY
                startForeground(startId, createNotification(), FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                // Foreground service is not allowed, request permission
                requestForegroundServicePermission()
            }
        }
        schedulePeriodicWork()
        return returnValue
    }

    private fun requestForegroundServicePermission() {
        val NOTIFICATION_ID_PERMISSION_REQUEST = 1001228 // Use any unique ID you want

        // Create a notification explaining why the permission is needed
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Foreground Service Permission")
            .setContentText("To receive timely task notifications, Taskify requires permission to run in the foreground.")
            .setSmallIcon(R.drawable.settingsicon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createPermissionRequestPendingIntent())
            .build()

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID_PERMISSION_REQUEST, notification)
    }

    private fun createPermissionRequestPendingIntent(): PendingIntent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
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

@RequiresApi(Build.VERSION_CODES.P)
fun isForegroundServiceAllowed(context: Context ): Boolean {
    // Check if the app has the necessary permission to run a foreground service
    val foregroundServicePermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.FOREGROUND_SERVICE
    )
    return foregroundServicePermission == PackageManager.PERMISSION_GRANTED
}