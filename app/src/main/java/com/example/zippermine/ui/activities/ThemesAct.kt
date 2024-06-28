package com.example.zippermine.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppDataGenerator
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.data.interfaces.ThemeSelected
import com.example.zippermine.databinding.ActivityThemesBinding
import com.example.zippermine.ui.adapters.ThemeAdapter
import com.example.zippermine.ui.ads.AdmobBannerAds
import com.example.zippermine.ui.ads.Ads
//import com.example.zippermine.ui.ads.admob.AdmobNativeAds
import com.example.zippermine.ui.ads.admob.AdsConstant
import com.example.zippermine.ui.ads.admob.loadNdShowINterAd
import com.example.zippermine.ui.dialog.ALodingDialog
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.preference.PowerPreference

class ThemesAct : AppCompatActivity() {

    var lockAdapterGold: ThemeAdapter? = null
    private lateinit var binding: ActivityThemesBinding
    private lateinit var aLoadingDialog: ALodingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemesBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      InterstitialAd
        //      InterstitialAd
        val interstFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "securityQuestionNative" to "" // Default value for splashNative
        )

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


        binding.doorLockThemeRv.setHasFixedSize(true)
        val mLayoutManager = GridLayoutManager(this, 2)
        binding.doorLockThemeRv.layoutManager = mLayoutManager
        lockAdapterGold =
            ThemeAdapter(this, getThemes(), object : ThemeSelected {
                override fun onThemeSelected(theme: Int) {
                    themeOpen(theme)
                }
            })
        binding.doorLockThemeRv.adapter = lockAdapterGold

        binding.backFromThemes.setOnClickListener {
            onBackPressed()
        }
    }

//    private fun loadNativeAds() {
//        if (AdsConstant.theme_small_native.contains("am")) {
//            binding.shimmerViewThe.visibility = View.VISIBLE
//            object : CountDownTimer(2000, 1000) {
//                override fun onTick(p0: Long) {}
//                override fun onFinish() {
//                    binding.shimmerViewThe.visibility = View.GONE
//                    AdmobNativeAds.showPreFetch(
//                        this@ThemesAct,
//                        AdsConstant.theme_small_native,
//                        binding.amNativeTh
//                    )
//                }
//            }.start()
//        }
//    }

    private fun displayThemeDialog(theme: Int, position: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.display_theme_dialog)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialog.findViewById<ImageView>(R.id.theme_img).setImageResource(theme)
        dialog.findViewById<ImageButton>(R.id.set_wallpaper).setOnClickListener {
            PowerPreference.getDefaultFile().putInt(HeartPrefConst.SetTheme, position)
            lockAdapterGold!!.notifyDataSetChanged()
            dialog.dismiss()
        }
    }

    private fun getThemes(): List<Int> {
        val list = ArrayList<Int>()
        list.add(HeartAppDataGenerator.theme1[0])
        list.add(HeartAppDataGenerator.theme2[0])
        list.add(HeartAppDataGenerator.theme3[0])
        list.add(HeartAppDataGenerator.theme4[0])

        return list
    }

    private fun backtoDashScreen(){
        startActivity(
            Intent(
                this@ThemesAct,
                DashboardAct::class.java
            )
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backtoDashScreen()
        finish()
    }

    private fun themeOpen(theme: Int) {
        onThemeSelectedInterstitialAd(theme)
    }

    private fun onThemeSelectedInterstitialAd(theme: Int) {
        if (theme == 0) {
            displayThemeDialog(HeartAppDataGenerator.theme1[0], theme)
        }
        if (theme == 1) {
            displayThemeDialog(HeartAppDataGenerator.theme2[0], theme)
        }
        if (theme == 2) {
            displayThemeDialog(HeartAppDataGenerator.theme3[0], theme)
        }
        if (theme == 3) {
            displayThemeDialog(HeartAppDataGenerator.theme4[0], theme)
        }
    }

}
