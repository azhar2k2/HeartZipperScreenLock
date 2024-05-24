package com.example.zippermine.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.zippermine.ui.dialog.ExitBottomSheet
import com.google.android.material.snackbar.Snackbar
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
            startActivity(
                Intent(
                    this,
                    SecurityQuestionSet::class.java
                ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
            )
        }

        binding.navRateUs.setOnClickListener {
            HeartAppConstants.ratingDialog(this)
        }

        binding.rateUsCard.setOnClickListener {
            HeartAppConstants.ratingDialog(this)
        }

        binding.fingerprintCard.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardAct,
                    EnableFingerPrintAct::class.java
                )
            )
        }

        binding.previewIcon.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardAct, PreviewAct::class.java
                )
            )
        }

        binding.enableZipLock.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    EnableLockAct::class.java
                ).putExtra(SHOW_AD_ON_NEXT_SCREEN, true)
            )
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
                    startActivity(
                        Intent(
                            this@DashboardAct,
                            SetPasswordAct::class.java
                        )
                    )
                }
            } else {
                showSnackBar(binding.root, "Enable Zip Lock First.")
            }
        }

        binding.themesCard.setOnClickListener {
            startActivity(
                Intent(
                    this@DashboardAct,
                    ThemesAct::class.java
                )
            )
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

    override fun onBackPressed() {
        super.onBackPressed()
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
            HeartPrefConst.IsPasswordSet,
            false
        )

        binding.vibrationSwitch.isChecked = PowerPreference.getDefaultFile().getBoolean(
            HeartPrefConst.IsVibrationSet,
            false
        )
    }

    override fun fingerprintAdDismiss() {
        if (!fingerPrint) {
            showSnackBar(
                binding.drawerLayout,
                getString(R.string.fingerprint_lock_off)
            )
        }

        if (fingerPrint) {
            showSnackBar(
                binding.drawerLayout,
                getString(R.string.fingerprint_lock_on)
            )
            fingerPrint = false
        }

        if (setLockFirst) {
            showSnackBar(
                binding.drawerLayout,
                getString(R.string.enable_lock_error)
            )
            setLockFirst = false
        }
    }
}