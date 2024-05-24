package com.example.zippermine.data.services;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.zippermine.R;
import com.example.zippermine.ui.activities.SplashAct;

public class ScreenLockService extends Service {
    public static IntentFilter filter;
    public static boolean isReceiverRegistered;
    public static BroadcastReceiver screenStateReceiver;
    public static Service doorservice;
    static boolean isPhoneOnCall = false;
    public PendingIntent pendingIntent;
    String CHANNEL_ID = "DoorLockForegroundService";

    @SuppressLint("ForegroundServiceType")
    @SuppressWarnings("deprecation")
    public void onCreate() {

        super.onCreate();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, SplashAct.class);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        }


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setTicker(getString(R.string.app_name))
                .setContentText("Device is protected with Zip Lock")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setColorized(true)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(111, notification);
        }
        KeyguardManager.KeyguardLock k1;
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        k1 = km.newKeyguardLock("IN");
        try {
            k1.disableKeyguard();
        } catch (Exception e) {
            e.printStackTrace();
        }

        doorservice = this;
        filter = new IntentFilter("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.SCREEN_ON");
        screenStateReceiver = new ScreenStateReceiver();
        registerReceiver(screenStateReceiver, filter);
        isReceiverRegistered = true;
        Log.e("Service", "started");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        try {
            if (isReceiverRegistered) {
                unregisterReceiver(this.screenStateReceiver);
                Log.e("CALL", "UNREGISTERED");
            } else {
                if (!this.isPhoneOnCall) {
                    Log.e("CALL", "REGISTER CALL");
                    Intent broadcastIntent = new Intent("RestartLockService");
                    sendBroadcast(broadcastIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}