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

    var dob: String
        get() = sharedPreferences.getString(KEY_DOB, "")
        set(dob) = applyString(KEY_DOB, dob)

    var promoImage: String
        get() = sharedPreferences.getString(KEY_PROMOTION_IMAGE, "")
        set(photo) = applyString(KEY_PROMOTION_IMAGE, photo)

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
        private val KEY_DOB = "key_dob"
        private val KEY_PROMOTION_IMAGE = "key_promo_image"
    }

}
