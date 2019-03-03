package com.auth.face.faceauth.navigation

import android.app.Activity
import android.content.Intent
import com.auth.face.faceauth.qr.QrActivity

class QrScreenRouter : Router {
    override fun route(activity: Activity) {
        val intent = Intent(activity, QrActivity::class.java)
        activity.startActivity(intent)
    }
}