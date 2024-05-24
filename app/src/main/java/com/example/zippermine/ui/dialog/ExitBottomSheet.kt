package com.example.zippermine.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zippermine.databinding.FragmentBottomSheetExitBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ExitBottomSheet : BottomSheetDialogFragment() {


    private var _binding: FragmentBottomSheetExitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        _binding = FragmentBottomSheetExitBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.exitBtn.setOnClickListener {
            activity?.finishAffinity()
        }

    }

}