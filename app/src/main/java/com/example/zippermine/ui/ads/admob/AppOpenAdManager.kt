package com.example.zippermine.ui.ads.admob

import android.app.Activity
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.data.interfaces.LoadAdCallBack
import com.example.zippermine.ui.ads.AdIds
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

object AppOpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false

    private var loadTime: Long = 0

    fun loadAd(
        context: Activity,
        adId: String = AdIds.appOpenAdIdOne,
        callBack: LoadAdCallBack? = null
    ) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }


        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adId,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time

                    callBack?.onLoaded()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    callBack?.onFailed()
                    isLoadingAd = false
                }
            }
        )
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun showIfAvailable(
        activity: Activity,
        remoteKey: Boolean = true,
        callBack: InterstitialCallBack? = null
    ) {
        if (!isAdAvailable()) {
            callBack?.onDismiss()
//            loadAd(activity)
            return
        }


        if (!remoteKey) {
            callBack?.onDismiss()
            return
        }
        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false

                callBack?.onDismiss()
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false

                callBack?.onDismiss()
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                callBack?.onAdDisplayed()
            }
        }
        isShowingAd = true
        appOpenAd!!.show(activity)
    }
}