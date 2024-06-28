package com.example.zippermine.ui.ads

import androidx.multidex.MultiDexApplication
import com.example.zippermine.ui.ads.admob.AppOpenAdsManager
import com.google.android.gms.ads.MobileAds

class Application : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
        AppOpenAdsManager(this, "ca-app-pub-3940256099942544/9257395921")
    }
}
