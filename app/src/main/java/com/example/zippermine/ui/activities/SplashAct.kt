package com.example.zippermine.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.zippermine.BuildConfig
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppConstants
import com.example.zippermine.data.interfaces.LoadAdCallBack
import com.example.zippermine.ui.ads.AdIds
import com.example.zippermine.ui.ads.Ads
import com.example.zippermine.ui.ads.admob.AdsConstant
import com.example.zippermine.ui.dialog.ALodingDialog
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.lock.screen.zippermine.ads.admob.AdmobNativeAds

class SplashAct : AppCompatActivity() {

    private lateinit var consentInformation: ConsentInformation
    private var isRemoteConfigCompleted = false
    private var isAdRequestSend = false

    private var isNativeAdLoaded = true

    private lateinit var aLodingDialog: ALodingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)



        MobileAds.initialize(this)

        FirebaseApp.initializeApp(this)
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "splashNative" to "" // Default value for splashNative
        )

        firebaseRemoteConfig.setDefaultsAsync(defaults)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch remote configs
                    firebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                // Remote configs fetched and activated
                                Ads.splashNative = firebaseRemoteConfig.getString("splashNative")

                                // Load and show the native or MREC ad
                                Ads.loadAndShowNativeOrMRECAd(
                                    this,
                                    remoteKey = Ads.splashNative,
                                    frameLayout = findViewById<FrameLayout>(R.id.nativeAdFrameLayout)
                                )
                            } else {
                                // Fetch failed
                            }
                        }
                } else {
                    // Set defaults failed
                }
            }

        val prog = findViewById<LottieAnimationView>(R.id.prog)

        // Start a timer to show prog animation for 5 seconds then move to LanguageAct
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                prog.visibility = View.VISIBLE
            }

            override fun onFinish() {
                showLanguageAct()
            }
        }.start()

        if (HeartAppConstants.checkInternetConnection(this)) {
            setupRemoteConfig()
//            FcmFireBaseID.subscribeToTopic()

            val params = ConsentRequestParameters
                .Builder()
                .build()

            consentInformation = UserMessagingPlatform.getConsentInformation(this@SplashAct)
            consentInformation.requestConsentInfoUpdate(
                this@SplashAct,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this@SplashAct) { loadAndShowError ->
                        if (loadAndShowError != null) {
                            Log.d("TAG", loadAndShowError.message)
                        }
                        // Consent gathering process has completed
                        if (consentInformation.canRequestAds()) {
                            MobileAds.initialize(this@SplashAct) {}
                            object : CountDownTimer(8000, 1000) {
                                @SuppressLint("SetTextI18n")
                                override fun onTick(p0: Long) {
                                    Log.d("counter", "onTick called")
                                    if (isRemoteConfigCompleted) {
                                        if (!isAdRequestSend) {
//                                            loadAds()
                                            isAdRequestSend = true
                                            Log.d("test", "ad loaded")
                                        }
                                    }
                                }

                                override fun onFinish() {
                                    Log.d("counter", "onFinish go to next screen")
                                    if (AdsConstant.splash_native.contains("am")) {
                                        prog.visibility = View.GONE
                                    } else {
//                                        nextScreen()
                                    }
                                }
                            }.start()
                        } else {
                            Handler().postDelayed({
//                                nextScreen()
                            }, 2000)
                        }
                    }
                },
                {
                    Log.d("TAG", it.message)
                }
            )
        } else {
            prog.visibility = View.GONE
        }
    }



    private fun showLanguageAct() {
        startActivity(
            Intent(
                this@SplashAct,
                LanguageAct::class.java
            ).putExtra(
                "FromSplash",
                true
            )
            )
        finish()
    }


    //remote config data
    private fun setupRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build()
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful && !BuildConfig.DEBUG) {
                remoteConfig.activate()
                FirebaseRemoteConfig.getInstance().activate()
                remoteConfig.activate()



                AdsConstant.admob_interstitial_id =
                    remoteConfig.getString("admob_interstitial_id").trim()
                AdsConstant.admob_native_id = remoteConfig.getString("admob_native_id").trim()
                AdsConstant.admob_banner_ad_id = remoteConfig.getString("admob_banner_ad_id").trim()
                AdsConstant.admob_app_open = remoteConfig.getString("admob_app_open").trim()



                AdsConstant.splash_interstitial =
                    remoteConfig.getString("splash_interstitial").trim()
                AdsConstant.dashboard_enable_zip_lock_front_interstitial =
                    remoteConfig.getString("dashboard_enable_zip_lock_front_interstitial").trim()
                AdsConstant.enable_zip_lock_back_interstitial =
                    remoteConfig.getString("enable_zip_lock_back_interstitial").trim()
                AdsConstant.security_question_activity_done_interstitial =
                    remoteConfig.getString("security_question_activity_done_interstitial").trim()
                AdsConstant.security_question_activity_back_interstitial =
                    remoteConfig.getString("security_question_activity_back_interstitial").trim()
                AdsConstant.dashboard_set_password_interstitial =
                    remoteConfig.getString("dashboard_set_password_interstitial").trim()
                AdsConstant.set_password_activity_back_interstitial =
                    remoteConfig.getString("set_password_activity_back_interstitial").trim()
                AdsConstant.set_password_confirm_interstitial =
                    remoteConfig.getString("set_password_confirm_interstitial").trim()
                AdsConstant.theme_item_click_interstitial =
                    remoteConfig.getString("theme_item_click_interstitial").trim()
                AdsConstant.dashboard_select_theme_interstitial =
                    remoteConfig.getString("dashboard_select_theme_interstitial").trim()
                AdsConstant.theme_activity_back_interstitial =
                    remoteConfig.getString("theme_activity_back_interstitial").trim()
                AdsConstant.change_security_question_dashboard_interstitial =
                    remoteConfig.getString("change_security_question_dashboard_interstitial").trim()
                AdsConstant.billing_screen_back_interstitial =
                    remoteConfig.getString("billing_screen_back_interstitial").trim()
                AdsConstant.billing_screen_skip_interstitial =
                    remoteConfig.getString("billing_screen_skip_interstitial").trim()
                AdsConstant.billing_screen_continue_with_ads_interstitial =
                    remoteConfig.getString("billing_screen_continue_with_ads_interstitial").trim()
                AdsConstant.dashboard_select_fingerprint_interstitial =
                    remoteConfig.getString("dashboard_select_fingerprint_interstitial").trim()
                AdsConstant.fingerprintScreen_back_interstitial =
                    remoteConfig.getString("fingerprintScreen_back_interstitial").trim()
                AdsConstant.preview_lock_interstitial =
                    remoteConfig.getString("preview_lock_interstitial").trim()

                AdsConstant.inner_items_click_counter_for_ads =
                    remoteConfig.getString("inner_items_click_counter_for_ads").trim()


                AdsConstant.splash_native = remoteConfig.getString("splash_native").trim()
                AdsConstant.permission_native = remoteConfig.getString("permission_native").trim()
                AdsConstant.dashboard_native = remoteConfig.getString("dashboard_native").trim()
                AdsConstant.enable_zip_lock_screen_native =
                    remoteConfig.getString("enable_zip_lock_screen_native").trim()
                AdsConstant.theme_small_native = remoteConfig.getString("theme_small_native").trim()
                AdsConstant.security_question_native =
                    remoteConfig.getString("security_question_native").trim()
                AdsConstant.set_password_small_native =
                    remoteConfig.getString("set_password_small_native").trim()
                AdsConstant.fingerprint_screen_native =
                    remoteConfig.getString("fingerprint_screen_native").trim()

                AdsConstant.banner_dashboard = remoteConfig.getString("banner_dashboard").trim()

//                loadAds()
                isRemoteConfigCompleted = true

            } else {
//                loadAds()
                isRemoteConfigCompleted = true
            }


        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}