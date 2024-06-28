package com.example.zippermine.ui.ads

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.data.interfaces.LoadAdCallBack
import com.example.zippermine.ui.dialog.ALodingDialog
import com.lock.screen.zippermine.ads.admob.AdmobInterstitialAd
import com.lock.screen.zippermine.ads.admob.AdmobMRECAds
import com.lock.screen.zippermine.ads.admob.AdmobNativeAds

object Ads {


    var interstitialON: String = "am_interst"
    var isNativeAdPreload: Boolean = true

    var splashNative: String = "am_native"
    var languageNative: String = "am_native"
    var dashboardNative: String = "am_native"
    var enableLockNative: String = "am_native"
    var fingerPrintNative: String = "am_native"
    var securityQuestionNative: String = "am_native"


    var mrec_ad: String = "am_mrec"


    fun loadAndShowNativeOrMRECAd(
        activity: Activity,
        adId: String = AdIds.nativeAdIdAdMob,
        remoteKey: String,
        frameLayout: FrameLayout,
    ) {
        if (remoteKey.contains("am_native")) {
            AdmobNativeAds.loadAdmobNative(activity, adId, remoteKey, object : LoadAdCallBack {
                override fun onLoaded() {
                    AdmobNativeAds.showNativeAd(activity, remoteKey, frameLayout)
                }

                override fun onFailed() {
                    frameLayout.removeAllViews()
                }
            }, frameLayout)
        }

        if (remoteKey.contains("am_mrec")) {
            AdmobMRECAds.showMREC(activity, frameLayout, remoteKey)
        }
    }


    fun loadAndShowInterstitial(
        activity: Activity,
        remoteKey: String,
        adId: String = AdIds.interstitialAdId,
        callBack: InterstitialCallBack? = null,
    ) {
        if (remoteKey.contains("am")) {
            val objDialog = ALodingDialog(activity)
            objDialog.show()
//            isShowingInt = true

            if (AdmobInterstitialAd.interAdmob != null) {
                android.os.Handler().postDelayed({
                    AdmobInterstitialAd.showInterstitial(activity, callBack)
//                    isShowingInt = false
                    objDialog.dismiss()
                }, 100)
            } else {
                AdmobInterstitialAd.loadInterAdmob(activity, adId, object : LoadAdCallBack {
                    override fun onLoaded() {
                        AdmobInterstitialAd.showInterstitial(activity,
                            object : InterstitialCallBack {
                                override fun onDismiss() {
                                    callBack?.onDismiss()
                                    objDialog.dismiss()
                                }
                            })
//                        isShowingInt = false
                    }

                    override fun onFailed() {
                        objDialog.dismiss()
//                        isShowingInt = false
                        callBack?.onDismiss()
                    }

                })
            }
        } else {
            callBack?.onDismiss()
        }
    }


}