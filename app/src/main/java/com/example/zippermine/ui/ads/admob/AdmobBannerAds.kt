package com.example.zippermine.ui.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.zippermine.ui.ads.admob.AdsConstant
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdRequest.Builder
import com.preference.PowerPreference


@SuppressLint("StaticFieldLeak")
object AdmobBannerAds {


    private var bannerAdView: AdView? = null
    private var adView: AdView? = null
    private var isLoaded = false


    fun loadAdmobAdaptiveBanner(context: Activity, layout: FrameLayout) =
        if (AdsConstant.banner_dashboard == "ab" && !PowerPreference.getDefaultFile().getBoolean(
                AdsConstant.PURCHASE
            )
        ) {
            adView = AdView(context)
            adView?.adUnitId = AdsConstant.admob_banner_ad_id
            layout.removeAllViews()
            layout.addView(adView)
            val adSize = getAdSize(context, layout)
            adView?.setAdSize(adSize)
            val adRequest = Builder().build()
            adView?.loadAd(adRequest)

            adView!!.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    layout.visibility = View.VISIBLE
                    isLoaded = true
                    showAdaptiveBanner(layout)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(
                        "TAG",
                        "onAdFailedToLoad:Adaptive Banner  ${adError.code}: ${adError.message}"
                    )
                    layout.visibility = View.GONE
                    isLoaded = false
                }

                override fun onAdOpened() {}
                override fun onAdClicked() {}
                override fun onAdClosed() {}
            }

        } else {
            layout.visibility = View.GONE
        }

    private fun getAdSize(context: Activity, adContainer: FrameLayout): AdSize {
        val display: Display = context.windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        val density = displayMetrics.density
        var adwidthpixels: Float = adContainer.width.toFloat()
        if (adwidthpixels == 0f) {
            adwidthpixels = displayMetrics.widthPixels.toFloat()
        }
        val adWith = (adwidthpixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWith)
    }


    private fun showAdaptiveBanner(view: FrameLayout) {
        if (adView != null && isLoaded) {
            view.visibility = View.VISIBLE
            if (adView?.parent != null) {
                (adView?.parent as ViewGroup).removeView(adView) // <- fix
            }
            view.removeAllViews()
            view.addView(adView)
        } else {
            view.visibility = View.GONE
        }

    }


    fun loadAndShowBanner(activity: Activity, view: FrameLayout) {

        if (AdsConstant.banner_dashboard == "am" && !PowerPreference.getDefaultFile().getBoolean(
                AdsConstant.PURCHASE
            )
        ) {

            val adView = AdView(activity)
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = AdsConstant.admob_banner_ad_id
            val adRequest: AdRequest = Builder().build()
            adView.loadAd(adRequest)

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    view.addView(adView)
                    // Code to be executed when an ad finishes loading.
                    view.visibility = View.VISIBLE
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Code to be executed when an ad request fails.
                    Log.e("TAG", "onAdFailedToLoad:Banner  ${adError.code}: ${adError.message}")
                    view.visibility = View.GONE
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            }

        } else {
            view.visibility = View.GONE
        }
    }
}