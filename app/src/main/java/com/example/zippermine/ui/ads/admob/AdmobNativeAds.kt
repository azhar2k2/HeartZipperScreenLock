package com.lock.screen.zippermine.ads.admob

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.zippermine.R
import com.example.zippermine.data.interfaces.LoadAdCallBack
import com.example.zippermine.ui.ads.AdIds
import com.example.zippermine.ui.ads.Ads
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

object AdmobNativeAds {
    var amNative: NativeAd? = null

    @SuppressLint("MissingPermission")
    fun loadAdmobNative(
        context: Context,
        adId: String = AdIds.nativeAdIdAdMob,
        remoteKey: String = "off",
        callBack: LoadAdCallBack? = null,
        frameLayout: FrameLayout? = null
    ) {
        if (adId == "") {
            callBack?.onFailed()
            return
        }

        if (remoteKey.contains("am")) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                    as LayoutInflater

            val shimmerView =
                inflater.inflate(R.layout.large_native_shimmer, null)
            frameLayout?.addView(shimmerView)
            frameLayout?.visibility = View.VISIBLE
        }

        if (amNative != null) {
            callBack?.onLoaded()
        } else {
            val adLoader = AdLoader.Builder(
                context,
                adId
            ).forNativeAd { ad: NativeAd ->
                amNative = ad
            }.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    amNative = null
                    Log.e("logKey", "Native Ad Failed: " + adError.code + " | " + adError.message)
                    frameLayout?.removeAllViews()
                    callBack?.onFailed()

                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    callBack?.onLoaded()
                    Log.e("logKey", "Native Ad Loaded")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    amNative = null
                }
            }).withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            ).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun showNativeAd(
        context: Context,
        remoteKey: String,
        amLayout: FrameLayout
    ) {
        if (amNative != null) {
            val nativeAdToShow = amNative
            if (remoteKey.contains("am")) {
                amLayout.visibility = View.VISIBLE

                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                        as LayoutInflater

                val adView = inflater.inflate(
                    if (remoteKey.contains("hctr"))
                        R.layout.admob_native_hctr
                    else if (remoteKey.contains("small"))
                        R.layout.admob_small_native_ad_hctr
                    else
                        R.layout.admob_native_lctr,
                    null
                ) as NativeAdView

                amLayout.removeAllViews()
                amLayout.addView(adView)

                if (remoteKey.contains("small"))
                    adView.mediaView = null
                else
                    adView.mediaView = adView.findViewById(R.id.ad_media)

                adView.headlineView = adView.findViewById(R.id.ad_headline)
                adView.bodyView = adView.findViewById(R.id.ad_body)
                adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                adView.iconView = adView.findViewById(R.id.ad_app_icon)

                (adView.headlineView as TextView).text = nativeAdToShow?.headline
                if (adView.mediaView != null)
                    adView.mediaView?.mediaContent = nativeAdToShow?.mediaContent

                if (nativeAdToShow?.body == null) {
                    adView.bodyView?.visibility = View.INVISIBLE
                } else {
                    (adView.bodyView as TextView).text = nativeAdToShow.body
                }

                if (nativeAdToShow?.callToAction == null) {
                    adView.callToActionView?.visibility = View.INVISIBLE
                } else {
                    (adView.callToActionView as Button).text = nativeAdToShow.callToAction
                }

                if (nativeAdToShow?.icon == null) {
                    adView.iconView?.visibility = View.GONE
                } else {
                    (adView.iconView as ImageView).setImageDrawable(
                        nativeAdToShow.icon!!.drawable
                    )
                }

                adView.setNativeAd(nativeAdToShow!!)
                amNative = null

                if (Ads.isNativeAdPreload)
                    loadAdmobNative(context, AdIds.nativeAdIdAdMob)
            } else {
                amLayout.visibility = View.GONE
            }
        } else {
            if (Ads.isNativeAdPreload)
                loadAdmobNative(context, AdIds.nativeAdIdAdMob)

            amLayout.visibility = View.GONE
        }
    }
}