package com.example.zippermine.core

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import com.example.zippermine.R

object HeartAppConstants {

    var securityActivity = false
    var securityQuestionCounter = 0


    fun checkInternetConnection(context: Context): Boolean {
        val connMgr =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connMgr.activeNetworkInfo
        if (activeNetworkInfo != null) {
            if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return true
            } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return true
            }
        }
        return false
    }

    fun moreApps(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Infinity+Tools+Choices+2020 ")
                )
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Link is Down", Toast.LENGTH_SHORT).show()
        }
    }

    fun ratingDialog(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.rate_us)

        val reviewBtnCancel = dialog.findViewById<Button>(R.id.review_cancel)
        val reviewBtnSumbit = dialog.findViewById<Button>(R.id.review_submit)
        val ratingBar: RatingBar = dialog.findViewById(R.id.rate_view)
        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, p1, _ ->
            if (p1 > 3f) {
                rateApp(context)
                dialog.dismiss()
            }
        }

        reviewBtnCancel.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }

        reviewBtnSumbit.setOnClickListener {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            rateApp(context)
        }

        dialog.show()
    }

//    fun showExitDialog(activity: Activity, context: Context) {
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.exit_dialog)
//        dialog.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setCancelable(true)
//
//        val ratingBar: RatingBar = dialog.findViewById(R.id.exit_rating)
//        ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, p1, _ ->
//            if (p1 > 3f) {
//                rateApp(context)
//                dialog.dismiss()
//            }
//        }
//
//        (dialog.findViewById<View>(R.id.yesBtn)).setOnClickListener {
//            dialog.dismiss()
//            activity.finishAffinity()
//        }
//
//
//        (dialog.findViewById<View>(R.id.noBtn)).setOnClickListener {
//            dialog.dismiss()
//
//        }
//
//        dialog.show()
//    }
//
    fun rateApp(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + context.packageName)
                )
            )
        }
    }

    fun showPrivacyPolicy(context: Context) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://sites.google.com/view/topvoiceconversation/heart-zipper-screen-lock                                                                                                                                                                                                                                                                                                                                                                                                                 ")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            anfe.printStackTrace()

        }
    }

    fun shareApp(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Download " + context.getString(R.string.app_name) + " now: https://play.google.com/store/apps/details?id=" + context.packageName
            )
            shareIntent.type = "text/plain"
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}