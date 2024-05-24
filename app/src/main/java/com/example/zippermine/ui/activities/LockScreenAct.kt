package com.example.zippermine.ui.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.fingerprint.FingerprintManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.zippermine.R
import com.example.zippermine.core.HeartAppDataGenerator
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.databinding.ActivityLockScreenBinding
import com.multidots.fingerprintauth.FingerPrintAuthCallback
import com.multidots.fingerprintauth.FingerPrintAuthHelper
import com.preference.PowerPreference
import java.util.*
import kotlin.concurrent.schedule

class LockScreenAct : AppCompatActivity(), FingerPrintAuthCallback {


    private lateinit var binding: ActivityLockScreenBinding
    lateinit var params: WindowManager.LayoutParams
    private lateinit var winMan: WindowManager
    private lateinit var wrapperView: ConstraintLayout
    private var frameNumber = 0
    private var isDownFromStart = false
    private var mScreenHeight = 0
    private var mScreenWidth = 0
    private var mStartWidthRange = 0
    private var mEndWidthRange = 0
    private var wrongAttempts = 1
    private var mFingerPrintAuthHelper: FingerPrintAuthHelper? = null
    private var themeZip: IntArray = intArrayOf()
    private lateinit var vibrator: Vibrator


    companion object {
        lateinit var activity: Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLockerView()

        activity = this

        setTheme()
        setView()
        setListeners()
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTheme() {

        if (PowerPreference.getDefaultFile().getInt(HeartPrefConst.SetTheme) == 0) {
            themeZip = HeartAppDataGenerator.theme1
        }
        if (PowerPreference.getDefaultFile().getInt(HeartPrefConst.SetTheme) == 1) {
            themeZip = HeartAppDataGenerator.theme2
        }
        if (PowerPreference.getDefaultFile().getInt(HeartPrefConst.SetTheme) == 2) {
            themeZip = HeartAppDataGenerator.theme3
        }
        if (PowerPreference.getDefaultFile().getInt(HeartPrefConst.SetTheme) == 3) {
            themeZip = HeartAppDataGenerator.theme4
        }


        binding.zipperScreen.setBackgroundResource(
            themeZip[0]
        )
    }


    private fun setLockerView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
        } else {
            params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
            )
        }

        winMan = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        window.attributes = params
        wrapperView = ConstraintLayout(baseContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (!isFinishing && !isDestroyed) {
                    winMan.addView(wrapperView, params)
                }
            }
        }
        val view = View.inflate(this, R.layout.activity_lock_screen, wrapperView)
        binding = ActivityLockScreenBinding.bind(view)
    }

    private fun setView() {


        if (PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsPasswordSet, false)) {
            binding.passwordLayout.root.visibility = View.VISIBLE
            setPinListeners()
        } else {
            binding.passwordLayout.root.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.zipperScreen.setOnTouchListener { _, event ->
            var i = 0
            mScreenHeight = binding.zipperScreen.height
            mScreenWidth = binding.zipperScreen.width
            mStartWidthRange = 2 * (mScreenWidth / 5)
            mEndWidthRange = 3 * (mScreenWidth / 5)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDownFromStart = (event.y < mScreenHeight / 4
                            && event.x > mStartWidthRange
                            && event.x < mEndWidthRange)

                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDownFromStart) {
                        if (event.x > mStartWidthRange
                            && event.x < mEndWidthRange
                            && isDownFromStart
                        ) {
                            i = (event.y / (mScreenHeight / 9)).toInt()
                            setImage(i)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (frameNumber >= 8) {
                        frameNumber = 0
                        isDownFromStart = true
                        unZip()
                    } else {
                        frameNumber = 0
                        setImage(0)
                    }
                }
                else -> {

                }
            }
            true
        }
    }

    private fun setImage(paramInt: Int) {
        frameNumber = when (paramInt) {
            0 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[0])
                1
            }
            1 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[1])
                2
            }
            2 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[2])
                3
            }
            3 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[3])
                4
            }
            4 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[4])
                5
            }
            5 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[5])
                6
            }
            6 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[6])
                7
            }
            7 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[7])
                8
            }
            8 -> {
                binding.zipperScreen.setBackgroundResource(themeZip[8])
                9
            }
            else -> return
        }
    }

    private fun setPinListeners() {
        binding.passwordLayout.key0.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("0")
        }

        binding.passwordLayout.key1.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("1")
        }

        binding.passwordLayout.key2.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("2")
        }

        binding.passwordLayout.key3.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("3")
        }

        binding.passwordLayout.key4.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("4")
        }

        binding.passwordLayout.key5.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("5")
        }

        binding.passwordLayout.key6.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("6")
        }

        binding.passwordLayout.key7.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("7")
        }

        binding.passwordLayout.key8.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("8")
        }

        binding.passwordLayout.key9.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            binding.passwordLayout.circleField.append("9")
        }

        binding.passwordLayout.keyBack.setOnClickListener {
            vibrate(100)
            playBtnClickSound()
            if (binding.passwordLayout.circleField.length() != 0) {
                binding.passwordLayout.circleField.setText(
                    binding.passwordLayout.circleField.text?.substring(
                        0,
                        binding.passwordLayout.circleField.length() - 1
                    )
                )
            }
        }

        binding.passwordLayout.circleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 4) {

                    Timer().schedule(100) {
                        runOnUiThread {

                            if (PowerPreference.getDefaultFile()
                                    .getString(HeartPrefConst.PinCode) == s.toString()
                            ) {
                                finish()
                            } else {

                                if (wrongAttempts == 3) {
                                    wrongAttempts = 1

                                    if (PowerPreference.getDefaultFile()
                                            .getBoolean(HeartPrefConst.IsSecurityQuestionSet, false)
                                    ) {
                                        binding.passwordLayout.forgetPasswordView.visibility =
                                            View.VISIBLE
                                        binding.passwordLayout.forgetPasswordQuestionDialog.text =
                                            PowerPreference.getDefaultFile()
                                                .getString(HeartPrefConst.SecurityQuestion)
                                    }

                                } else {
                                    wrongAttempts++
                                    wrongPasswordAnimation(binding.passwordLayout.circleField)
                                    binding.passwordLayout.circleField.text?.clear()
                                }


                            }

                        }
                    }


                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.passwordLayout.doneButtonDialog.setOnClickListener {

            if (binding.passwordLayout.answerEditTextDialog.text.trim() == PowerPreference.getDefaultFile()
                    .getString(HeartPrefConst.SecurityQuestionAnswer)
            ) {

                PowerPreference.getDefaultFile().setBoolean(HeartPrefConst.IsPasswordSet, false)
                startActivity(Intent(this, SplashAct::class.java))
                finish()
            }
        }

        binding.passwordLayout.cancelDialogForgetPassword.setOnClickListener {
            binding.passwordLayout.forgetPasswordView.visibility = View.GONE
            hideKeyboard(this)
        }

    }

    private fun wrongPasswordAnimation(view: View) {
        ObjectAnimator.ofFloat(
            view,
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
        ).setDuration(1000).start()
    }


    private fun unZip() {
        if (!PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsPasswordSet, false)) {
            finish()
        } else {
            binding.zipperScreen.visibility = View.GONE
        }
    }

    private fun hideKeyboard(activity: Activity) {

        val manager: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            val inputManager: InputMethodManager =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            view = View(activity)
            Log.e("TAG", "hideKeyboard: null")

        } else {
            manager.hideSoftInputFromWindow(view.windowToken, 0)
            Log.e("TAG", "hideKeyboard: not null")
        }

    }

    private fun vibrate(long: Long) {
        if (PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsVibrationSet, false)) {
            vibrator.vibrate(long)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PowerPreference.getDefaultFile()
                    .getBoolean(HeartPrefConst.IsFingerPrintSet)
            ) {
                mFingerPrintAuthHelper!!.startAuth()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNoFingerPrintHardwareFound() {}
    override fun onNoFingerPrintRegistered() {}
    override fun onBelowMarshmallow() {}
    override fun onAuthFailed(errorCode: Int, errorMessage: String?) {
        binding.fingerprintImg.visibility = View.VISIBLE
        wrongPasswordAnimation(binding.fingerprintImg)
        object : CountDownTimer(1100, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                binding.fingerprintImg.visibility = View.GONE
            }
        }.start()
    }

    override fun onAuthSuccess(cryptoObject: FingerprintManager.CryptoObject?) {
        if (PowerPreference.getDefaultFile().getBoolean(HeartPrefConst.IsFingerPrintSet)) {
            finish()
        }
    }


    override fun finish() {
        vibrate(200)
        try {
            this.winMan.removeView(this.wrapperView)
            this.wrapperView.removeAllViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val mp = MediaPlayer()
            mp.setAudioStreamType(AudioManager.STREAM_RING)
            val afd = assets.openFd("iphone_sound.mp3")
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.prepare()
            mp.start()
            mp.setOnCompletionListener { mp -> mp.release() }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.finish()
    }

    private fun playBtnClickSound() {
        try {
            val mp = MediaPlayer()
            mp.setAudioStreamType(AudioManager.STREAM_RING)
            val afd = assets.openFd("typing.mp3")
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mp.prepare()
            mp.start()
            mp.setOnCompletionListener { mp -> mp.release() }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}