package cz.cvut.fel.tasktest

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build

import cz.cvut.fel.tasktest.notification.TaskifyNotificationService
import android.Manifest

import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class TaskifyApplication : Application() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Check and request foreground service permission
        if (!isForegroundServicePermissionGranted()) {
            requestForegroundServicePermission()
        } else {
            startNotificationService()
        }
    }

    private fun startNotificationService() {
        val serviceIntent = Intent(applicationContext, TaskifyNotificationService::class.java)
        ContextCompat.startForegroundService(applicationContext, serviceIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel code
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun isForegroundServicePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForegroundServicePermission() {
        val intent = Intent(applicationContext, PermissionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
    }
}