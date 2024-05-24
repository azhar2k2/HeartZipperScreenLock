package com.example.zippermine.ui.ads.admob

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.data.interfaces.LoadAdCallBack
import com.example.zippermine.ui.ads.AdIds
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


object AdmobInterstitialAd {
    var interAdmob: InterstitialAd? = null
    var isIntLoading = false
    var failedRequests = 0

    fun loadInterAdmob(
        context: Context,
        adId: String = AdIds.interstitialAdId,
        callback: LoadAdCallBack? = null
    ) {
        if (interAdmob != null) {
            callback?.onLoaded()
            return
        }

        if (failedRequests > 4) {
            callback?.onFailed()
            return
        }


        isIntLoading = true

        val admobRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adId,
            admobRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("loadAdmob?", adError.message + adError.code)
                    interAdmob = null
                    isIntLoading = false
                    failedRequests++
                    callback?.onFailed()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("loadAdmob?", "Ad was loaded.")
                    interAdmob = interstitialAd
                    failedRequests = 0
                    isIntLoading = false
                    callback?.onLoaded()
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, callback: InterstitialCallBack? = null) {
        if (interAdmob == null) {
            callback?.onDismiss()
            return
        }

        interAdmob?.show(activity)
        interAdmob?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("interAdmobShow", "Ad was dismissed.")
                interAdmob = null
                callback?.onDismiss()
                loadInterAdmob(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("interAdmobShow", "Ad failed to show." + adError.message + adError.code)
                interAdmob = null
                callback?.onDismiss()
            }

            override fun onAdShowedFullScreenContent() {
                callback?.onAdDisplayed()
                Log.d("interAdmobShow", "Ad showed fullscreen content.")
            }
        }

    }

}