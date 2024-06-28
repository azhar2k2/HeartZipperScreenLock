package com.example.zippermine.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.zippermine.R
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.core.HeartTools
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.databinding.ActivityEnableLockBinding
import com.example.zippermine.ui.ads.AdmobBannerAds
import com.example.zippermine.ui.ads.Ads
//import com.example.zippermine.ui.ads.admob.AdmobNativeAds
import com.example.zippermine.ui.ads.admob.AdsConstant
import com.example.zippermine.ui.ads.admob.loadNdShowINterAd
import com.example.zippermine.ui.dialog.ALodingDialog
import com.example.zippermine.ui.dialog.PermissionDialog
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.preference.PowerPreference

class EnableLockAct : AppCompatActivity() {

    private lateinit var binding: ActivityEnableLockBinding

    private lateinit var aLoadingDialog : ALodingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "enableLockNative" to "" // Default value for splashNative
        )

        firebaseRemoteConfig.setDefaultsAsync(defaults)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch remote configs
                    firebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                // Remote configs fetched and activated
                                Ads.enableLockNative = firebaseRemoteConfig.getString("enableLockNative")

                                // Load and show the native or MREC ad
                                Ads.loadAndShowNativeOrMRECAd(
                                    this,
                                    remoteKey = Ads.enableLockNative,
                                    frameLayout = findViewById<FrameLayout>(R.id.nativeLayout)
                                )
                            } else {
                                // Fetch failed
                            }
                        }
                } else {
                    // Set defaults failed
                }
            }



      //InterstitialAd
        val interstFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        interstFirebaseRemoteConfig.setDefaultsAsync(defaults)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch remote configs
                    interstFirebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                // Remote configs fetched and activated
                                Ads.interstitialON = interstFirebaseRemoteConfig.getString("interstitialON")

                                // Load and show the native or MREC ad
                                Ads.loadAndShowInterstitial(
                                    this,
                                    remoteKey = Ads.interstitialON,
//                                    frameLayout = findViewById<FrameLayout>(R.id.am_native_dash)
                                )
                            } else {
                                // Fetch failed
                            }
                        }
                } else {
                    // Set defaults failed
                }
            }

        if (AdsConstant.banner_dashboard == "am") {
            AdmobBannerAds.loadAndShowBanner(this, binding.amBanner)
        } else if (AdsConstant.banner_dashboard == "ab") {
            AdmobBannerAds.loadAdmobAdaptiveBanner(this, binding.amBanner)
        }


        setListeners()
    }

    private fun setListeners() {

        binding.backImg.setOnClickListener {
            onBackPressed()
        }

        binding.enableZipCard.setOnClickListener {
            if (HeartTools.isLockPermissionGranted(this)) {
                if (binding.enableZipCardSwitch.isChecked) {
                    binding.enableZipCardSwitch.isChecked = false
                    PowerPreference.getDefaultFile()
                        .setBoolean(HeartPrefConst.IsZipLockEnable, false)
                    HeartTools.stopService(this)
                } else {
                    binding.enableZipCardSwitch.isChecked = true
                    PowerPreference.getDefaultFile()
                        .setBoolean(HeartPrefConst.IsZipLockEnable, true)
                    HeartTools.startService(this)
                }
            } else {
                PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsZipLockEnable, true)
                PermissionDialog(this).show()
            }
        }
    }

    private fun backtoDashScreen(){
        startActivity(
            Intent(
                this@EnableLockAct,
                DashboardAct::class.java
            )
        )
    }

//    private fun loadNativeAds() {
//        if (AdsConstant.dashboard_native.contains("am")) {
//            binding.shimmer.visibility = View.VISIBLE
//            object : CountDownTimer(2000, 1000) {
//                override fun onTick(p0: Long) {}
//                override fun onFinish() {
//                    binding.shimmer.visibility = View.GONE
//                    AdmobNativeAds.showPreFetch(
//                        this@EnableLockAct,
//                        AdsConstant.dashboard_native,
//                        binding.nativeLayout
//                    )
//                }
//            }.start()
//        }
//    }

    override fun onResume() {
        super.onResume()

        binding.enableZipCardSwitch.isChecked =
            HeartTools.isLockPermissionGranted(this) && PowerPreference.getDefaultFile().getBoolean(
                HeartPrefConst.IsZipLockEnable,
                false
            )

        if (HeartTools.isLockPermissionGranted(this) && PowerPreference.getDefaultFile().getBoolean(
                HeartPrefConst.IsZipLockEnable,
                false
            )
        ) {
            HeartTools.startService(this)
        } else {
            HeartTools.stopService(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
                backtoDashScreen()
                finish()
            }
}