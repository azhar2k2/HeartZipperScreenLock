package com.example.zippermine.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.zippermine.core.HeartPrefConst
import com.example.zippermine.databinding.ActivitySetPasswordBinding
import com.example.zippermine.ui.dialog.SetSecuritySetting
import com.preference.PowerPreference
import java.util.*
import kotlin.concurrent.schedule

class SetPasswordAct : AppCompatActivity() {

    private lateinit var binding: ActivitySetPasswordBinding
    private var firstAttempt = ""
    private var isFirstAttempt = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {

        binding.key0.setOnClickListener {
            binding.circleField.append("0")
        }

        binding.key1.setOnClickListener {
            binding.circleField.append("1")
        }

        binding.key2.setOnClickListener {
            binding.circleField.append("2")
        }

        binding.key3.setOnClickListener {
            binding.circleField.append("3")
        }

        binding.key4.setOnClickListener {
            binding.circleField.append("4")
        }

        binding.key5.setOnClickListener {
            binding.circleField.append("5")
        }

        binding.key6.setOnClickListener {
            binding.circleField.append("6")
        }

        binding.key7.setOnClickListener {
            binding.circleField.append("7")
        }

        binding.key8.setOnClickListener {
            binding.circleField.append("8")
        }

        binding.key9.setOnClickListener {
            binding.circleField.append("9")
        }

        binding.keyBack.setOnClickListener {
            if (binding.circleField.length() != 0) {
                binding.circleField.setText(
                    binding.circleField.text?.substring(0, binding.circleField.length() - 1)
                )
            }
        }

        binding.circleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 4) {
                    Timer().schedule(100) {
                        runOnUiThread {
                            if (isFirstAttempt) {
                                binding.text.text = "Re-enter your pin"
                                isFirstAttempt = false
                                firstAttempt = s.toString()
                                binding.circleField.text?.clear()
                            } else {
                                if (s.toString() == firstAttempt) {
                                    PowerPreference.getDefaultFile().setBoolean(
                                        HeartPrefConst.IsPasswordSet,
                                        true
                                    )

                                    PowerPreference.getDefaultFile().setString(
                                        HeartPrefConst.PinCode,
                                        s.toString()
                                    )

                                    if (PowerPreference.getDefaultFile()
                                            .getBoolean(HeartPrefConst.ShowQuestionDialog, true)
                                    ) {
                                        PowerPreference.getDefaultFile()
                                            .setBoolean(HeartPrefConst.ShowQuestionDialog, false)
                                        val bottomSheet = SetSecuritySetting()
                                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                                    } else {
                                        finish()
                                    }
                                } else {
                                    binding.text.text = "Enter your pin"
                                    binding.circleField.text?.clear()
                                    isFirstAttempt = true
                                    firstAttempt = ""
                                }
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
