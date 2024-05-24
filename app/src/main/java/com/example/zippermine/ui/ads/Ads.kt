package com.example.zippermine.ui.ads

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.ui.ads.admob.AdmobInterstitialAd

object Ads {

    var interstitialON: String = "am_interst"


    fun showInterstitial(
        activity: Activity,
        remote: String,
        callback: InterstitialCallBack? = null
    ) {
        if (remote.contains("am")) {
//            Log.d(Misc.logKey, "Int am")
            AdmobInterstitialAd.showInterstitial(activity, callback)
        } else {
//            Log.d(Misc.logKey, "Int off")
            callback?.onDismiss()
        }
    }
}