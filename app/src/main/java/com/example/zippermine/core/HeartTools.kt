package com.example.zippermine.core

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.example.zippermine.data.services.ScreenLockService

object HeartTools {


    fun startService(context: Context) {
        val intent = Intent(
            context,
            ScreenLockService::class.java
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            context.startService(intent)
        } else {
            context.startForegroundService(intent)
        }
    }

    fun stopService(context: Context) {
        val intent = Intent(
            context,
            ScreenLockService::class.java
        )
        context.stopService(intent)
    }

    fun isOverLayPermissionGranted(activity: Activity?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity)
        } else true
    }

    fun isLockPermissionGranted(
        activity: Activity
    ): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED &&
                    Settings.canDrawOverlays(activity))
        } else {
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_DENIED
        }
    }

}