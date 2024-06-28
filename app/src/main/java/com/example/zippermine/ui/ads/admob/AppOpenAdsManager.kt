package com.example.zippermine.ui.ads.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

interface OnShowAdCompleteListener {
    fun onShowAdComplete()
}

class AppOpenAdsManager(application: Application, adUnitId: String) : Application.ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null
    private var adUnit: String = adUnitId
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    var loadTime: Long = 0

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            currentActivity?.let {
                showAdIfAvailable(it)
            }
        }
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    override fun onActivityStopped(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }
        isLoadingAd = true
        val request: AdRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            adUnit,
            request,
            object : AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d("AppOpenAdsManager", "Ad failed to load: ${p0.message}")
                    isLoadingAd = false
                }

                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    Log.d("AppOpenAdsManager", "Ad loaded successfully")
                    appOpenAd = p0
                    isLoadingAd = false
                    loadTime = Date().time
                }
            }
        )
    }

    fun isAdAvailable(): Boolean {
        return appOpenAd != null
    }

    private fun showAdIfAvailable(activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener) {
        if (isShowingAd) {
            return
        }

        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete()
            loadAd(activity)
            return
        }

        appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                appOpenAd = null
                isShowingAd = false
                onShowAdCompleteListener.onShowAdComplete()
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        isShowingAd = true
        appOpenAd!!.show(activity)
    }

    private fun showAdIfAvailable(activity: Activity) {
        showAdIfAvailable(
            activity,
            object : OnShowAdCompleteListener {
                override fun onShowAdComplete() {}
            }
        )
    }
}
