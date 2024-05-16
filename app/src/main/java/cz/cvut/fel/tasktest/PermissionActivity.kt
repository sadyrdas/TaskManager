package cz.cvut.fel.tasktest

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request foreground service permission
        if (!isForegroundServicePermissionGranted()) {
            requestForegroundServicePermissionLauncher.launch(Manifest.permission.FOREGROUND_SERVICE)
        } else {
            // Permission already granted, return result
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun isForegroundServicePermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
    }

    private val requestForegroundServicePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setResult(Activity.RESULT_OK)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
}