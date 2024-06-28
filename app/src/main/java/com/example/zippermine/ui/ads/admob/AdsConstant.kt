package com.example.zippermine.ui.ads.admob

import com.example.zippermine.BuildConfig

object AdsConstant {


    const val PURCHASE = "Purchase"

    const val admob = "am"
    const val admobHctr = "am_hctr"
    const val admobLctr = "am_lctr"


    var admob_interstitial_id = "ca-app-pub-3940256099942544/1033173712/"
    var admob_native_id = "ca-app-pub-3940256099942544/2247696110/"
    var admob_banner_ad_id = "ca-app-pub-3940256099942544/6300978111/"
    var admob_app_open = if (BuildConfig.DEBUG) { "ca-app-pub-3940256099942544/3419835294" } else { "abc" }



    //interstitial placements
    var splash_interstitial = "am"
    var security_question_activity_done_interstitial = "am"
    var dashboard_enable_zip_lock_front_interstitial = "am"
    var enable_zip_lock_back_interstitial = "am"
    var security_question_activity_back_interstitial = "am"
    var dashboard_set_password_interstitial = "am"
    var set_password_activity_back_interstitial = "am"
    var set_password_confirm_interstitial = "am"
    var theme_item_click_interstitial = "am"
    var dashboard_select_theme_interstitial = "am"
    var dashboard_select_fingerprint_interstitial = "am"
    var theme_activity_back_interstitial = "am"
    var fingerprintScreen_back_interstitial = "am"
    var change_security_question_dashboard_interstitial = "am"
    var billing_screen_back_interstitial = "am"
    var billing_screen_skip_interstitial = "am"
    var billing_screen_continue_with_ads_interstitial = "am"
    var preview_lock_interstitial = "am"

    var inner_items_click_counter_for_ads = "3"

    //native Ads
    var splash_native = "am_hctr"
    var permission_native = "am_hctr"
    var dashboard_native = "am_hctr"
    var enable_zip_lock_screen_native = "am_hctr"
    var theme_small_native = "am"
    var security_question_native = "am_hctr"
    var set_password_small_native = "am"
    var fingerprint_screen_native = "am_hctr"

    //banner
    var banner_dashboard = "ab"
}