package com.example.zippermine.ui.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.example.zippermine.ui.ads.Ads
import com.preference.PowerPreference

class ThemesAct : AppCompatActivity() {

    var lockAdapterGold: ThemeAdapter? = null
    private lateinit var binding: ActivityThemesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemesBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
