package com.auth.face.faceauth.base

import android.location.Location

interface LocationReadyListener {
    fun onReady(location: Location)
}
