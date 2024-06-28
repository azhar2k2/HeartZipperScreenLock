package com.example.zippermine.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppConstants
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.core.SHOW_AD_ON_NEXT_SCREEN
import com.example.zippermine.data.interfaces.FingerprintAdsDismiss
import com.example.zippermine.databinding.ActivityDashboardBinding
import com.example.zippermine.ui.ads.Ads
import com.example.zippermine.ui.dialog.ExitBottomSheet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.preference.PowerPreference

class DashboardAct : AppCompatActivity(), FingerprintAdsDismiss {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    companion object {
        var notRegistered = false
        var startSettingAct = false
        var fingerPrint = false
        var setLockFirst = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFuns()

        FirebaseApp.initializeApp(this)
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "dashboardNative" to "" // Default value for splashNative
        )

        firebaseRemoteConfig.setDefaultsAsync(defaults).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Fetch remote configs
                firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { fetchTask ->
                    if (fetchTask.isSuccessful) {
                        // Remote configs fetched and activated
                        Ads.dashboardNative = firebaseRemoteConfig.getString("dashboardNative")

                        // Load and show the native or MREC ad
                        Ads.loadAndShowNativeOrMRECAd(
                            this,
                            remoteKey = Ads.dashboardNative,
                            frameLayout = findViewById<FrameLayout>(R.id.am_native_dash)
                        )
                    } else {
                        // Fetch failed
                    }
                }
            } else {
                // Set defaults failed
            }
        }

        val interstFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        interstFirebaseRemoteConfig.setDefaultsAsync(defaults).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Fetch remote configs
                interstFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { fetchTask ->
                        if (fetchTask.isSuccessful) {
                            // Remote configs fetched and activated
                            Ads.interstitialON =
                                interstFirebaseRemoteConfig.getString("interstitialON")

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
    }

    private fun initFuns() {
        initToolbar()
        initComponent()
        setListeners()
    }

    private fun setListeners() {

        binding.rateUsCard.setOnClickListener {
            closeDrawer()
            HeartAppConstants.ratingDialog(this)
        }

        binding.navRemoveAds.setOnClickListener {
            closeDrawer()
        }

        binding.navShareApp.setOnClickListener {
            closeDrawer()
            HeartAppConstants.shareApp(this)
        }

        binding.navMoreApps.setOnClickListener {
            closeDrawer()
            HeartAppConstants.moreApps(this)
        }

        binding.navPrivacy.setOnClickListener {
            closeDrawer()
            HeartAppConstants.showPrivacyPolicy(this)
        }

        binding.passwordResetCard.setOnClickListener {

            securityQuestionScreen()
        }

        binding.changeLanguageCard.setOnClickListener {
            changeLanguageScreen()
        }

        binding.navRateUs.setOnClickListener {
            HeartAppConstants.ratingDialog(this)
        }

        binding.rateUsCard.setOnClickListener {
            HeartAppConstants.ratingDialog(this)
        }

        binding.fingerprintCard.setOnClickListener {

            enableFingerPrintScreen()
        }

        binding.previewIcon.setOnClickListener {

            previewScreen()
        }

        binding.enableZipLock.setOnClickListener {

            enableLockScreen()

        }

        binding.setPassword.setOnClickListener {
            if (PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsZipLockEnable)) {
                if (PowerPreference.getDefaultFile()
                        .getBoolean(HeartPrefConst.IsPasswordSet, false)
                ) {
                    PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsPasswordSet, false)
                    binding.setPasswordSwitch.isChecked = false
                    PowerPreference.getDefaultFile()
                        .setBoolean(HeartPrefConst.IsFingerPrintSet, false)
                } else {
                    setPAsswordScreen()
                }
            } else {
                showSnackBar(binding.root, "Enable Zip Lock First.")
            }
        }

        binding.themesCard.setOnClickListener {

            themesScreen()
        }

        binding.vibrationCard.setOnClickListener {
            if (binding.vibrationSwitch.isChecked) {
                binding.vibrationSwitch.isChecked = false
                PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsVibrationSet, false)
            } else {
                binding.vibrationSwitch.isChecked = true
                PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsVibrationSet, true)
            }
        }
    }

    private fun enableLockScreen() {
        startActivity(
            Intent(
                this, EnableLockAct::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun setPAsswordScreen() {
        startActivity(
            Intent(
                this, SetPasswordAct::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun enableFingerPrintScreen() {
        startActivity(
            Intent(
                this, EnableFingerPrintAct::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun previewScreen() {
        startActivity(
            Intent(
                this, PreviewAct::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun securityQuestionScreen() {
        startActivity(
            Intent(
                this, SecurityQuestionSet::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun changeLanguageScreen() {
        startActivity(
            Intent(
                this, LanguageAct::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun themesScreen() {
        startActivity(
            Intent(
                this, ThemesAct::class.java
            ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
        )
        finish()
    }

    private fun initToolbar() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.app_name)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
    }

    private fun initComponent() {
        initNavigationMenu()
    }

    private fun initNavigationMenu() {
        toggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {}
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers()
        } else {
            val bottomSheet = ExitBottomSheet()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    fun showSnackBar(parent_view: View?, msg: String?) {
        val snackbar = Snackbar.make(parent_view!!, msg!!, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val parentParams = snackbarView.layoutParams as FrameLayout.LayoutParams
        parentParams.setMargins(5, 0, 5, 10)
        snackbarView.layoutParams = parentParams
        snackbar.show()
    }

    override fun onResume() {
        super.onResume()

        binding.setPasswordSwitch.isChecked = PowerPreference.getDefaultFile().getBoolean(
            HeartPrefConst.IsPasswordSet, false
        )

        binding.vibrationSwitch.isChecked = PowerPreference.getDefaultFile().getBoolean(
            HeartPrefConst.IsVibrationSet, false
        )
    }

    override fun fingerprintAdDismiss() {
        if (!fingerPrint) {
            showSnackBar(
                binding.drawerLayout, getString(R.string.fingerprint_lock_off)
            )
        }

        if (fingerPrint) {
            showSnackBar(
                binding.drawerLayout, getString(R.string.fingerprint_lock_on)
            )
            fingerPrint = false
        }

        if (setLockFirst) {
            showSnackBar(
                binding.drawerLayout, getString(R.string.enable_lock_error)
            )
            setLockFirst = false
        }
    }
}