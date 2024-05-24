package com.example.zippermine.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.core.HeartTools
import com.example.zippermine.core.SHOW_AD_ON_NEXT_SCREEN
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.databinding.ActivityEnableLockBinding
import com.example.zippermine.ui.ads.Ads
import com.example.zippermine.ui.dialog.PermissionDialog
import com.preference.PowerPreference

class EnableLockAct : AppCompatActivity() {

    private lateinit var binding: ActivityEnableLockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnableLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        Ads.showInterstitial(this, Ads.interstitialON, object : InterstitialCallBack {
            override fun onAdDisplayed() {
                // No action needed
            }

            override fun onDismiss() {
                backtoDashScreen()
                finish()
            }
        })
    }
}
