package com.example.zippermine.ui.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppConstants
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.data.interfaces.InterstitialCallBack
import com.example.zippermine.databinding.ActivitySecurityQuestionBinding
import com.example.zippermine.ui.ads.Ads
import com.preference.PowerPreference

class SecurityQuestionSet : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivitySecurityQuestionBinding
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
                binding.answerEditTextAnswer.setHintTextColor(ContextCompat.getColor(this, R.color.red))
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
