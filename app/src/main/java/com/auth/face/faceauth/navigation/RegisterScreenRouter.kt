package com.auth.face.faceauth.navigation

import android.app.Activity
import android.content.Intent
import com.auth.face.faceauth.login.RegisterActivity

class RegisterScreenRouter : Router {
    override fun route(activity: Activity) {
        val intent = Intent(activity, RegisterActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}