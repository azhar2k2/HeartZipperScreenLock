package com.example.zippermine.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zippermine.databinding.SetSecurityQuestionBinding
import com.example.zippermine.ui.activities.SecurityQuestionSet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SetSecuritySetting : BottomSheetDialogFragment() {

    private var _binding: SetSecurityQuestionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        _binding = SetSecurityQuestionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yesBtn.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    SecurityQuestionSet::class.java
                )
            )
            requireActivity().finish()
        }

        binding.noBtn.setOnClickListener {
            requireActivity().finish()
        }


    }

    override fun dismiss() {
        super.dismiss()
        _binding = null
    }

}