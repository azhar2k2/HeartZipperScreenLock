package com.example.zippermine.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.zippermine.core.HeartAppDataGenerator
import com.example.zippermine.databinding.ActivityPreviewBinding


class PreviewAct : AppCompatActivity() {

    private lateinit var binding: ActivityPreviewBinding
    private var frameNumber = 0
    private var isDownFromStart = false
    private var mScreenHeight = 0
    private var mScreenWidth = 0
    private var mStartWidthRange = 0
    private var mEndWidthRange = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        InterstitialAd.showInterstitialAdmob(
//            this,
//            this,
//            AdsConstant.preview_lock_interstitial
//        )

        binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[0])
        setListeners()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.zipperScreen.setOnTouchListener { v, event ->
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
                MotionEvent.ACTION_MOVE -> if (isDownFromStart) {
                    if (event.x > mStartWidthRange
                        && event.x < mEndWidthRange
                        && isDownFromStart
                    ) {
                        i = (event.y / (mScreenHeight / 9)).toInt()
                        setImage(i)
                    }
                }
                MotionEvent.ACTION_UP -> if (frameNumber >= 8) {
                    frameNumber = 0
                    isDownFromStart = true
                    onBackPressed()
                } else {
                    frameNumber = 0
                    setImage(0)
                }
                else -> {}
            }
            true
        }
    }

    private fun setImage(paramInt: Int) {
        frameNumber = when (paramInt) {
            0 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[0])
                1
            }
            1 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[1])
                2
            }
            2 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[2])
                3
            }
            3 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[3])
                4
            }
            4 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[4])
                5
            }
            5 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[5])
                6
            }
            6 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[6])
                7
            }
            7 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[7])
                8
            }
            8 -> {
                binding.zipperScreen.setBackgroundResource(HeartAppDataGenerator.theme1[8])
                9
            }
            else -> return
        }
    }
}