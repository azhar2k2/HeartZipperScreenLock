package com.example.zippermine.data.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.zippermine.core.HeartPrefConst;
import com.example.zippermine.ui.activities.LockScreenAct;
import com.preference.PowerPreference;

public class ScreenStateReceiver extends BroadcastReceiver {
    static boolean isPhoneOnCall = false;

    public void onReceive(final Context paramContext, Intent paramIntent) {

        if (PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsZipLockEnable, false)) {
            if (paramIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && paramIntent.getAction().equals(
                    "android.intent.action.SCREEN_ON")) {
                Intent localIntent3 = new Intent(paramContext, LockScreenAct.class);
                localIntent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                paramContext.startActivity(localIntent3);
            }
            if (paramIntent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                Intent localIntent1 = new Intent(paramContext, LockScreenAct.class);
                localIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                paramContext.startActivity(localIntent1);
            }
        }

        try {
            StateListener localStateListener = new StateListener();
            ((TelephonyManager) ScreenLockService.doorservice.getSystemService(Context.TELEPHONY_SERVICE)).listen(localStateListener, 32);
            Log.e("CALL", "REGISTERED");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CALL", "ERROR");
        }
    }

    class StateListener extends PhoneStateListener {
        StateListener() {
        }

        public void onCallStateChanged(int paramInt, String paramString) {
            try {
                Log.e("CALL", "received");
                if (paramInt == TelephonyManager.CALL_STATE_RINGING) {
                    Log.e("CALL", "Ringing");
                    ScreenLockService.doorservice.sendBroadcast(new Intent("com.lock.call"));
                    ScreenLockService.doorservice.unregisterReceiver(ScreenLockService.screenStateReceiver);
                    ScreenLockService.isReceiverRegistered = false;
                    try {
                        LockScreenAct.Companion.getActivity().finish();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    isPhoneOnCall = true;
                } else if (paramInt == TelephonyManager.CALL_STATE_OFFHOOK) {
                    Log.e("CALL", "hook");
                    ScreenLockService.doorservice.sendBroadcast(new Intent("com.lock.call"));
                    ScreenLockService.doorservice.unregisterReceiver(ScreenLockService.screenStateReceiver);
                    ScreenLockService.isReceiverRegistered = false;
                    LockScreenAct.Companion.getActivity().finish();
                    isPhoneOnCall = true;

                } else if (paramInt == TelephonyManager.CALL_STATE_IDLE) {
                    if (!ScreenLockService.isReceiverRegistered) {
                        Log.e("CALL", "idle");
                        ScreenLockService.doorservice.registerReceiver(
                                ScreenLockService.screenStateReceiver, ScreenLockService.filter);
                        ScreenLockService.isReceiverRegistered = true;
                        isPhoneOnCall = false;
                        Intent localIntent = new Intent(ScreenLockService.doorservice, LockScreenAct.class);
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ScreenLockService.doorservice.startActivity(localIntent);
                    }
                }
            } catch (Exception e) {
                Log.e("CALL", "FAiled");
                e.printStackTrace();
            }
            super.onCallStateChanged(paramInt, paramString);
        }
    }
}