package cz.cvut.fel.tasktest

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build

import cz.cvut.fel.tasktest.notification.TaskifyNotificationService
import android.Manifest
import android.content.Context

import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import cz.cvut.fel.tasktest.notification.isForegroundServiceAllowed

class TaskifyApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()


        startNotificationService()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startNotificationService() {
        if (isForegroundServiceAllowed(this as Context)) {
            val serviceIntent = Intent(applicationContext, TaskifyNotificationService::class.java)
            ContextCompat.startForegroundService(applicationContext, serviceIntent)
        }
    }

}