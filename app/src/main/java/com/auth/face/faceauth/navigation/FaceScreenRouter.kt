package com.auth.face.faceauth.navigation

import android.app.Activity
import android.content.Intent
import com.auth.face.faceauth.face.FaceAuthActivity

class FaceScreenRouter : Router {
    override fun route(activity: Activity) {
        val intent = Intent(activity, FaceAuthActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}
