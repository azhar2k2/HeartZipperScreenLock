package com.example.zippermine.ui.activities

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zippermine.R
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.databinding.ActivityEnableFingerPrintBinding
import com.example.zippermine.ui.ads.AdmobBannerAds
import com.example.zippermine.ui.ads.Ads
//import com.example.zippermine.ui.ads.admob.AdmobNativeAds
import com.example.zippermine.ui.ads.admob.AdsConstant
import com.example.zippermine.ui.ads.admob.loadNdShowINterAd
import com.example.zippermine.ui.dialog.ALodingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.preference.PowerPreference

class EnableFingerPrintAct : AppCompatActivity() {

    private lateinit var binding: ActivityEnableFingerPrintBinding

    private lateinit var aLoadingDialog: ALodingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableFingerPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "fingerPrintNative" to "" // Default value for splashNative
        )

        firebaseRemoteConfig.setDefaultsAsync(defaults)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch remote configs
                    firebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                // Remote configs fetched and activated
                                Ads.fingerPrintNative = firebaseRemoteConfig.getString("fingerPrintNative")

                                // Load and show the native or MREC ad
                                Ads.loadAndShowNativeOrMRECAd(
                                    this,
                                    remoteKey = Ads.fingerPrintNative,
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

//      InterstitialAd
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




//        if (AdsConstant.banner_dashboard == "am") {
//            AdmobBannerAds.loadAndShowBanner(this, binding.bannerAd)
//        } else if (AdsConstant.banner_dashboard == "ab") {
//            AdmobBannerAds.loadAdmobAdaptiveBanner(this, binding.bannerAd)
//        }

        if (PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsZipLockEnable, false)) {
            binding.fingerprintCardSwitch.isChecked =
                PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsFingerPrintSet, false)
        } else {
            binding.fingerprintCardSwitch.isChecked = false
            PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsFingerPrintSet, false)
        }

        binding.backImg.setOnClickListener {
            onBackPressed()
        }

        setListeners()
    }

    private fun setListeners() {
        binding.fingerprintCard.setOnClickListener {
            if (binding.fingerprintCardSwitch.isChecked) {
                PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsFingerPrintSet, false)
                binding.fingerprintCardSwitch.isChecked = false
                Toast.makeText(this, "Fingerprint Disabled", Toast.LENGTH_SHORT).show()
                if (PowerPreference.getDefaultFile()
                        .getBoolean(HeartPrefConst.IsFingerPrintSet, false)
                ) {
                    DashboardAct.fingerPrint = false
                }
            } else {
                if (PowerPreference.getDefaultFile()
                        .getBoolean(HeartPrefConst.IsPasswordSet, false)
                ) {
                    if (DashboardAct.notRegistered) {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                        DashboardAct.startSettingAct = true
                    } else {
                        PowerPreference.getDefaultFile()
                            .setBoolean(HeartPrefConst.IsFingerPrintSet, true)
                        DashboardAct.fingerPrint = true
                        binding.fingerprintCardSwitch.isChecked = true
                        Toast.makeText(this, "Fingerprint Enabled", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.fingerprintCardSwitch.isChecked = false
                    DashboardAct.setLockFirst = true
                    Toast.makeText(this, "Set Password First", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fingerprintManager =
                    this.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                if (!fingerprintManager.isHardwareDetected) {
                    PowerPreference.getDefaultFile()
                        .setBoolean(HeartPrefConst.IsFingerPrintSet, false)
                    binding.fingerprintCard.isEnabled = false
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    DashboardAct.notRegistered = true
                    PowerPreference.getDefaultFile()
                        .setBoolean(HeartPrefConst.IsFingerPrintSet, false)
                } else {
                    DashboardAct.notRegistered = false
                }

                if (!DashboardAct.notRegistered && DashboardAct.startSettingAct) {
                    PowerPreference.getDefaultFile()
                        .setBoolean(HeartPrefConst.IsFingerPrintSet, true)
                    binding.fingerprintCardSwitch.isChecked = true
                    showSnackBar(binding.parent, getString(R.string.fingerprint_lock_on))
                } else {
                    binding.fingerprintCardSwitch.isChecked = PowerPreference.getDefaultFile()
                        .getBoolean(HeartPrefConst.IsFingerPrintSet, false)
                }
            } else {
                PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsFingerPrintSet, false)
                binding.fingerprintCard.isEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSnackBar(parent_view: View?, msg: String?) {
        val snackbar = Snackbar.make(parent_view!!, msg!!, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val parentParams = snackbarView.layoutParams as FrameLayout.LayoutParams
        parentParams.setMargins(0, 0, 0, 0)
        snackbarView.layoutParams = parentParams
        snackbar.show()
    }

    private fun backtoDashScreen(){
        startActivity(
            Intent(
                this@EnableFingerPrintAct,
                DashboardAct::class.java
            )
        )
    }

//    private fun loadNativeAds() {
//        if (AdsConstant.fingerprint_screen_native.contains("am")) {
//            binding.shimmerFing.visibility = View.VISIBLE
//            object : CountDownTimer(2000, 1000) {
//                override fun onTick(p0: Long) {}
//                override fun onFinish() {
//                    binding.shimmerFing.visibility = View.GONE
//                    AdmobNativeAds.showPreFetch(
//                        this@EnableFingerPrintAct,
//                        AdsConstant.fingerprint_screen_native,
//                        binding.nativeLayout
//                    )
//                }
//            }.start()
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        backtoDashScreen()
        finish()
    }
}
