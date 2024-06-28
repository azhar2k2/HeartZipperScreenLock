package com.example.zippermine.ui.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppConstants
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.databinding.ActivitySecurityQuestionBinding
import com.example.zippermine.ui.ads.AdmobBannerAds
import com.example.zippermine.ui.ads.Ads
//import com.example.zippermine.ui.ads.admob.AdmobNativeAds
import com.example.zippermine.ui.ads.admob.AdsConstant
import com.example.zippermine.ui.ads.admob.loadNdShowINterAd
import com.example.zippermine.ui.dialog.ALodingDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.preference.PowerPreference

class SecurityQuestionSet : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivitySecurityQuestionBinding
    private lateinit var aLoadingDialog: ALodingDialog
    lateinit var spinner: Spinner
    lateinit var question: String
    var securityQuestions = arrayOf(
        "Where were you born?",
        "What was your childhood nick name?",
        "What was the name of your primary school?",
        "What was the name of your high school?",
        "In which city did you meet your spouse?",
        "What was the name of your first grade teacher?",
        "What is your best friend name?",
        "What is your favorite book name?",
        "What school did you attend for sixth grade?",
        "What is the name of your youngest child?",
        "In what city were you born?",
        "What is your favorite movie?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values (fallback when values are not available)
        val defaults: Map<String, Any> = mapOf(
            "welcome_message" to "Welcome to our app!",
            "securityQuestionNative" to "" // Default value for splashNative
        )

        firebaseRemoteConfig.setDefaultsAsync(defaults)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch remote configs
                    firebaseRemoteConfig.fetchAndActivate()
                        .addOnCompleteListener { fetchTask ->
                            if (fetchTask.isSuccessful) {
                                // Remote configs fetched and activated
                                Ads.securityQuestionNative = firebaseRemoteConfig.getString("securityQuestionNative")

                                // Load and show the native or MREC ad
                                Ads.loadAndShowNativeOrMRECAd(
                                    this,
                                    remoteKey = Ads.securityQuestionNative,
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

        spinner = findViewById(R.id.spinner)
        spinner.onItemSelectedListener = this
        HeartAppConstants.securityActivity = true

        if (!PowerPreference.getDefaultFile().getString(HeartPrefConst.SecurityQuestionAnswer)
                .isNullOrEmpty()
        ) {
            binding.topText.visibility = View.GONE
            spinner.visibility = View.GONE
            binding.doneButton.visibility = View.GONE
            binding.answerEditText.visibility = View.GONE

            binding.questionTextView.visibility = View.VISIBLE
            binding.doneButtonAnswer.visibility = View.VISIBLE
            binding.answerEditTextAnswer.visibility = View.VISIBLE

            binding.questionTextView.text =
                PowerPreference.getDefaultFile().getString(HeartPrefConst.SecurityQuestion)
            binding.titleBarTV.text = "Enter Answer Of your Old Question"
            binding.topText.text = "Your security question"

        }

        binding.doneButtonAnswer.setOnClickListener {
            if (!binding.answerEditTextAnswer.text.isNullOrEmpty() && binding.answerEditTextAnswer.text.toString() == PowerPreference.getDefaultFile()
                    .getString(HeartPrefConst.SecurityQuestionAnswer).toString()
            ) {
                binding.topText.visibility = View.VISIBLE
                spinner.visibility = View.VISIBLE
                binding.doneButton.visibility = View.VISIBLE
                binding.answerEditText.visibility = View.VISIBLE

                binding.questionTextView.visibility = View.GONE
                binding.doneButtonAnswer.visibility = View.GONE
                binding.answerEditTextAnswer.visibility = View.GONE

                binding.titleBarTV.text = "Security Question"
                binding.topText.text = "Your security question"
            } else {
                binding.answerEditTextAnswer.text.clear()
                binding.answerEditTextAnswer.hint = "Wrong Answer"
                binding.answerEditTextAnswer.setHintTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.red
                    )
                )
            }
        }

        val spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, securityQuestions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        binding.doneButton.setOnClickListener {
            if (binding.answerEditText.text.isNullOrEmpty()) {
                binding.editTextValidationText.visibility = View.VISIBLE
                ObjectAnimator
                    .ofFloat(
                        binding.answerEditText,
                        "translationX",
                        0f,
                        25f,
                        -25f,
                        25f,
                        -25f,
                        15f,
                        -15f,
                        6f,
                        -6f,
                        0f
                    )
                    .setDuration(1000)
                    .start()

                object : CountDownTimer(1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        binding.editTextValidationText.visibility = View.GONE
                    }
                }.start()
            } else {
                PowerPreference.getDefaultFile()
                    .putBoolean(HeartPrefConst.IsSecurityQuestionSet, true)
                PowerPreference.getDefaultFile()
                    .putString(HeartPrefConst.SecurityQuestionAnswer, question)
                PowerPreference.getDefaultFile().putString(
                    HeartPrefConst.SecurityQuestionAnswer,
                    binding.answerEditText.text.toString()
                )
                finish()
            }
        }
    }


//        private fun loadNativeAds() {
//            if (AdsConstant.security_question_native.contains("am")) {
//                binding.shimmerFing.visibility = View.VISIBLE
//                object : CountDownTimer(2000, 1000) {
//                    override fun onTick(p0: Long) {}
//                    override fun onFinish() {
//                        binding.shimmerFing.visibility = View.GONE
//                        AdmobNativeAds.showPreFetch(
//                            this@SecurityQuestionSet,
//                            AdsConstant.security_question_native,
//                            binding.nativeLayout
//                        )
//                    }
//                }.start()
//            }
//        }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        question = securityQuestions[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun backtoDashScreen(){
        startActivity(
            Intent(
                this@SecurityQuestionSet,
                DashboardAct::class.java
            )
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backtoDashScreen()
        finish()
    }
}
