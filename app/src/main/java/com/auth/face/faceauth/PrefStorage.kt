package com.auth.face.faceauth

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class PrefStorage(context: Context) {

    private val sharedPreferences: SharedPreferences

    var photo: String
        get() = sharedPreferences.getString(KEY_PHOTO, "")
        set(photo) = applyString(KEY_PHOTO, photo)

    var username: String
        get() = sharedPreferences.getString(KEY_USERNAME, "")
        set(Username) = applyString(KEY_USERNAME, Username)

    var userId: String
        get() = sharedPreferences.getString(KEY_USER_ID, "")
        set(userId) = applyString(KEY_USER_ID, userId)

    var dob: String
        get() = sharedPreferences.getString(KEY_DOB, "")
        set(dob) = applyString(KEY_DOB, dob)

    var promoImage: String
        get() = sharedPreferences.getString(KEY_PROMO_IMAGE, "")
        set(photo) = applyString(KEY_PROMO_IMAGE, photo)

    var promoId: String
        get() = sharedPreferences.getString(KEY_PROMO_ID, "")
        set(promoId) = applyString(KEY_PROMO_ID, promoId)

    var qrCode: String
        get() = sharedPreferences.getString(KEY_QR_CODE, "")
        set(photo) = applyString(KEY_QR_CODE, photo)

    init {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun applyString(key: String, value: String) {
        sharedPreferences
                .edit()
                .putString(key, value)
                .apply()
    }

    companion object {
        private val KEY_PHOTO = "key_photo"
        private val KEY_USERNAME = "key_username"
        private val KEY_USER_ID = "key_user_id"
        private val KEY_DOB = "key_dob"
        private val KEY_PROMO_IMAGE = "key_promo_image"
        private val KEY_PROMO_ID = "key_promo_id"
        private val KEY_QR_CODE = "key_qr_code"
    }

}
