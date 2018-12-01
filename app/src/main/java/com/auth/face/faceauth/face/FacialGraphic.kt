package com.auth.face.faceauth.face

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.google.android.gms.vision.face.Face

internal class FacialGraphic(overlay: FacialGraphicOverlay) : FacialGraphicOverlay.Graphic(overlay) {
    private val mFaceRectPaint: Paint

    @Volatile var face: Face? = null

    init {
        mFaceRectPaint = Paint()
        mFaceRectPaint.color = Color.WHITE
        mFaceRectPaint.style = Paint.Style.STROKE
        mFaceRectPaint.strokeWidth = 10f
        mFaceRectPaint.setPathEffect(DashPathEffect(floatArrayOf(10f, 20f), 0.0f))
    }

    fun updateLiveFaceDetails(face : Face?) {
        this.face = face
        postInvalidate()
    }

    override fun draw(canvas: Canvas) {
        drawFaceOval(canvas)
    }

    private fun drawFaceOval(canvas: Canvas) {
        if (face != null) {
            val centerX = translateX(face!!.position.x + face!!.width / 2f)
            val centerY = translateY(face!!.position.y + face!!.height / 2f)
            val offsetX = scaleX(face!!.width / 2f)
            val offsetY = scaleY(face!!.height / 2f)

            val left = centerX - offsetX
            val right = centerX + offsetX
            val top = centerY - offsetY
            val bottom = centerY + offsetY
            canvas.drawOval(left, top, right, bottom, mFaceRectPaint)
        }
    }

}
