package com.auth.face.faceauth.navigation

import android.app.Activity
import android.content.Intent
import com.auth.face.faceauth.promo.PromotionActivity

class PromoScreenRouter : Router {
    override fun route(activity: Activity) {
        val intent = Intent(activity, PromotionActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}