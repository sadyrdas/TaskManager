package cz.cvut.fel.tasktest.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.FileOutputStream

class BootReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val serviceIntent = Intent(context, TaskifyNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            }
        }
    }
}

// Create the file name with current date and time

//            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
//            val currentDateTime = dateFormat.format(Date())
//            val fileName = "BootReceiver_$currentDateTime.txt"
//
//            // Content to write to the file
//            val content = "Boot completed at: ${Date()}"
//
//            // Save the file in the Documents directory
//            val documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//            val file = File(documentsDirectory, fileName)
//
//            try {
//                val outputStream = FileOutputStream(file)
//                outputStream.write(content.toByteArray())
//                outputStream.close()
//            } catch (e: Exception) {
//            }
//            Log.d("BootBip", "onReceive: Booty")
// Start the service if API level is Oreo or higher