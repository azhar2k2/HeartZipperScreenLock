package com.example.zippermine.data.interfaces

import com.google.android.gms.ads.interstitial.InterstitialAd

interface LoadAdCallBack {
    fun onLoaded(){}
    fun onFailed(){}
}