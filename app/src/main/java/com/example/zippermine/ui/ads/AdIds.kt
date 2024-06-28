package com.example.zippermine.ui.ads

import com.example.zippermine.BuildConfig

object AdIds {

    // Directly defining the AdMob IDs
    var interstitialAdIdAdMobSplash: String = "ca-app-pub-3940256099942544/1033173712"
    var interstitialAdId: String = "ca-app-pub-3940256099942544/1033173712"

    var nativeAdIdAdMob: String =
        "ca-app-pub-3940256099942544/2247696110"

    var appOpenAdIdOne: String = "ca-app-pub-3940256099942544/9257395921"

    var bannerAdIdAdOne: String =   "ca-app-pub-3940256099942544/6300978111"

    var collapsibleBannerAdIdAd: String =  "ca-app-pub-3940256099942544/2014213617"

    var mrecAdIdAd = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/6300978111"
    } else {
        ""
    }

}
