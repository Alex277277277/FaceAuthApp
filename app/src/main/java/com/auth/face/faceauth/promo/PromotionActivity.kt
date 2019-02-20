package com.auth.face.faceauth.promo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

import com.auth.face.faceauth.FaceAuthApp
import com.auth.face.faceauth.R
import com.auth.face.faceauth.base.Utils
import com.auth.face.faceauth.navigation.FaceScreenRouter

class PromotionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion)

        val prefs = FaceAuthApp.app.getPrefs()
        val image = prefs.promoImage
        if (TextUtils.isEmpty(image)) {
            navigateToFaceAuthScreen()
            return
        }

        findViewById<View>(R.id.btClose).setOnClickListener({ v -> navigateToFaceAuthScreen() })
        displayPromoImage(image)
    }

    private fun displayPromoImage(image: String) {
        val photoData = Utils.fromBase64(image)
        val bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.size)
        val ivPromo = findViewById<ImageView>(R.id.ivPromo)
        ivPromo.setImageBitmap(bitmap)
    }

    private fun navigateToFaceAuthScreen() {
        FaceScreenRouter().route(this)
    }

    override fun onBackPressed() {
        navigateToFaceAuthScreen()
    }
}
