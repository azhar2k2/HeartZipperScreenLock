package com.example.zippermine.ui.activities

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zippermine.R
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.databinding.ActivityEnableFingerPrintBinding
import com.example.zippermine.ui.dialog.SetSecuritySetting
import com.google.android.material.snackbar.Snackbar
import com.preference.PowerPreference

class EnableFingerPrintAct : AppCompatActivity() {

    private lateinit var binding: ActivityEnableFingerPrintBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableFingerPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
