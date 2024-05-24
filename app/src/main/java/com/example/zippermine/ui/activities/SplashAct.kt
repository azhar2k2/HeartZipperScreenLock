package com.example.zippermine.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppConstants
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class SplashAct : AppCompatActivity() {

    private lateinit var consentInformation: ConsentInformation
    private var isRemoteConfigCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prog = findViewById<LottieAnimationView>(R.id.prog)
        val next = findViewById<Button>(R.id.next)

        next.setOnClickListener {
            nextScreen()
        }

        if (HeartAppConstants.checkInternetConnection(this)) {
            setupRemoteConfig()

            val params = ConsentRequestParameters.Builder().build()

            consentInformation = UserMessagingPlatform.getConsentInformation(this@SplashAct)
            consentInformation.requestConsentInfoUpdate(
                this@SplashAct,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this@SplashAct) { loadAndShowError ->
                        if (loadAndShowError != null) {
                            Log.d("TAG", loadAndShowError.message)
                        }

                        if (consentInformation.canRequestAds()) {
                            object : CountDownTimer(8000, 1000) {
                                @SuppressLint("SetTextI18n")
                                override fun onTick(p0: Long) {
                                    if (isRemoteConfigCompleted) {
                                        Log.d("test", "remote config completed")
                                    }
                                }

                                override fun onFinish() {
                                    prog.visibility = View.GONE
                                    next.visibility = View.VISIBLE
                                }
                            }.start()
                        } else {
                            prog.visibility = View.GONE
                            next.visibility = View.VISIBLE
                        }
                    }
                },
                {
                    Log.d("TAG", it.message)
                }
            )

        } else {
            prog.visibility = View.GONE
            next.visibility = View.VISIBLE
        }
    }

    private fun nextScreen() {
        startActivity(
            Intent(
                this@SplashAct,
                DashboardAct::class.java
            ).putExtra(
                "FromSplash",
                true
            )
        )
        finish()
    }

    private fun setupRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build()
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                remoteConfig.activate()

                // Handle other remote config keys as needed

                isRemoteConfigCompleted = true

            } else {
                isRemoteConfigCompleted = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
