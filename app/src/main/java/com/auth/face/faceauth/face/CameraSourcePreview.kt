package com.auth.face.faceauth.face

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
//import com.glyphid.MainApplication
//import com.glyphid.logger.LoggerInstance
import com.google.android.gms.vision.CameraSource
import java.io.IOException

class CameraSourcePreview(private val mContext: Context, attrs: AttributeSet) : ViewGroup(mContext, attrs) {

    private val mSurfaceView: SurfaceView
    private var mCameraSource: CameraSource? = null
    private var mOverlay: FacialGraphicOverlay? = null
    private var mStartRequested: Boolean = false
    private var mSurfaceAvailable: Boolean = false

    init {
        mSurfaceView = SurfaceView(mContext)
        mSurfaceView.holder.addCallback(SurfaceCallback())
        addView(mSurfaceView)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource?) {
        if (cameraSource == null) {
            stop()
        }

        mCameraSource = cameraSource
        if (mCameraSource != null) {
            mStartRequested = true
            startIfReady()
        }
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource, overlay: FacialGraphicOverlay) {
        mOverlay = overlay
        start(cameraSource)
    }

    fun stop() {
        if (mCameraSource != null) {
            mCameraSource!!.stop()
        }
    }

    @Throws(IOException::class)
    private fun startIfReady() {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource!!.start(mSurfaceView.holder)
            if (mOverlay != null) {
                val size = mCameraSource!!.previewSize
                val min = Math.min(size.width, size.height)
                val max = Math.max(size.width, size.height)
                mOverlay!!.setCameraInfo(min, max, mCameraSource!!.cameraFacing)
                mOverlay!!.clear()
            }
            mStartRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            mSurfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                //LoggerInstance.get().error(TAG, "Could not start camera source.", e)
            }

        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            mSurfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            // do nothing
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var previewWidth = 240
        var previewHeight = 320
        if (mCameraSource != null) {
            val size = mCameraSource!!.previewSize
            if (size != null) {
                previewWidth = size.height
                previewHeight = size.width
            }
        }

        val viewWidth = right - left
        val viewHeight = bottom - top

        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }

        for (i in 0 until childCount) {
            getChildAt(i).layout(-1 * childXOffset,
                    -1 * childYOffset,
                    childWidth - childXOffset,
                    childHeight - childYOffset)
        }

        try {
            startIfReady()
        } catch (e: IOException) {
            //LoggerInstance.get().error(TAG, "Could not start camera source.", e)
        }

    }

    companion object {
        //private val TAG = MainApplication.TAG + CameraSourcePreview::class.java.simpleName
    }

}
