package com.example.zippermine.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.zippermine.R
import com.example.zippermine.databinding.ActivityLanguageBinding
import com.example.zippermine.ui.ads.Ads
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.util.Locale

class LanguageAct : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding
    private var selectedLayout: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "langaugeNative" to "" // Default value for splashNative
        )

        firebaseRemoteConfig.setDefaultsAsync(defaults)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch remote configs
                    firebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                // Remote configs fetched and activated
                                Ads.languageNative = firebaseRemoteConfig.getString("languageNative")

                                // Load and show the native or MREC ad
                                Ads.loadAndShowNativeOrMRECAd(
                                    this,
                                    remoteKey = Ads.languageNative,
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

        setupLanguageSelection()

        binding.next.setOnClickListener {
            showLanguageAct()
        }
    }

    private fun setupLanguageSelection() {
        binding.eng.setOnClickListener {
            setLocaleAndHighlight("", binding.eng)
        }

        binding.french.setOnClickListener {
            setLocaleAndHighlight("fr", binding.french)
        }

        binding.arabic.setOnClickListener {
            setLocaleAndHighlight("ar", binding.arabic)
        }

        binding.spanish.setOnClickListener {
            setLocaleAndHighlight("es", binding.spanish)
        }

        binding.german.setOnClickListener {
            setLocaleAndHighlight("de", binding.german)
        }

        binding.hindi.setOnClickListener {
            setLocaleAndHighlight("hi", binding.hindi)
        }
    }

    private fun setLocaleAndHighlight(languageCode: String, layoutView: ConstraintLayout) {
        setLocale(languageCode)
        highlightSelectedLayout(layoutView)
    }

    private fun highlightSelectedLayout(layoutView: ConstraintLayout) {
        // Deselect the previously selected layout
        selectedLayout?.isSelected = false

        // Select the new layout
        layoutView.isSelected = true

        // Update the reference to the currently selected layout
        selectedLayout = layoutView
    }

    private fun showLanguageAct() {
        startActivity(
            Intent(
                this@LanguageAct, DashboardAct::class.java
            ).putExtra(
                "FromSplash", true
            )
        )
        finish()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}
