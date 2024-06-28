package com.lock.screen.zippermine.ads.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.data.interfaces.LoadAdCallBack
import com.example.zippermine.ui.ads.AdIds
import com.example.zippermine.ui.ads.Ads
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdmobInterstitialAd {


    var interAdmob: InterstitialAd? = null

    fun loadInterAdmob(
        context: Context,
        adId: String = AdIds.interstitialAdId,
        callBack: LoadAdCallBack? = null
    ) {
        if (adId == "") {
            callBack?.onFailed()
            return
        }

        val admobRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adId,
            admobRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("loadAdmob?", adError.message + adError.code)
                    callBack?.onFailed()
                    interAdmob = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("loadAdmob?", "Ad was loaded.")
                    interAdmob = interstitialAd
                    callBack?.onLoaded()
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, callback: InterstitialCallBack?) {
        if (interAdmob == null) {
            callback?.onDismiss()
//            loadInterAdmob(activity)
            return
        }

        interAdmob?.show(activity)
        interAdmob?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                callback?.onDismiss()
                Log.d("interAdmobShow", "Ad was dismissed.")
//                Ads.isShowingInt = false
                interAdmob = null
//                if (Ads.isIntPreLoad)
//                    loadInterAdmob(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("interAdmobShow", "Ad failed to show." + adError.message + adError.code)
                interAdmob = null
//                Ads.isShowingInt = false
                callback?.onDismiss()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("interAdmobShow", "Ad showed fullscreen content.")
//                Ads.isShowingInt = true
                callback?.onAdDisplayed()
            }
        }

    }

}