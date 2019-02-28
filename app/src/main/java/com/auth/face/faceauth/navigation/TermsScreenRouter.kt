package com.auth.face.faceauth.navigation

import android.app.Activity
import android.content.Intent
import com.auth.face.faceauth.TermsActivity

class TermsScreenRouter : Router {
    override fun route(activity: Activity) {
        val intent = Intent(activity, TermsActivity::class.java)
        activity.startActivity(intent)
    }
}