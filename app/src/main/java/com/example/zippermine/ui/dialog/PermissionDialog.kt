package com.example.zippermine.ui.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.zippermine.databinding.PermissionDialogBinding
import com.example.zippermine.ui.activities.EnableLockAct
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.*


class PermissionDialog(
    private val activity: Activity
) : Dialog(activity) {

    private var _binding: PermissionDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setView()
        initView()
    }


    private fun setView() {

        val inflater = context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater

        _binding = PermissionDialogBinding.inflate(inflater)
        this.setContentView(binding.root)

        this.let {
            it.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.window?.setBackgroundDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                )
            )
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
        }


        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.readPhoneStateCard.visibility = View.GONE
            binding.divider.visibility = View.GONE
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(activity)) {
                binding.screenOverlayCard.visibility = View.GONE
                binding.divider.visibility = View.GONE
            }
        } else {
            binding.screenOverlayCard.visibility = View.GONE
            binding.divider.visibility = View.GONE
        }
    }


    private fun initView() {

        binding.readPhoneStateBtn.setOnClickListener {
            TedPermission.create()
                .setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        binding.readPhoneStateCard.visibility = View.GONE
                        binding.divider.visibility = View.GONE
                    }

                    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    }

                }).setDeniedMessage("Please allow this permission to avoid inconvenience")
                .setPermissions(Manifest.permission.READ_PHONE_STATE)
                .check()
        }

        binding.screenOverlayBtn.setOnClickListener {
            screenOverlayPermission()
        }

    }


    private fun screenOverlayPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${activity.packageName}")
            activity.startActivityForResult(intent, 101)

            /*val intentHint = Intent(activity, ScreenOverlayGuide::class.java)
            intentHint.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intentHint)*/

            Timer(true).scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Log.e("TAG", "run: ")
                    if (Settings.canDrawOverlays(activity)) {
                        cancel()
                        Log.e("TAG", "run inner: ")
                        val i = Intent(activity, EnableLockAct::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        activity.startActivity(i)
                    }
                }
            }, 0, 1000)
        }
    }


    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        _binding = null
    }


}