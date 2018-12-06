package com.auth.face.faceauth.face

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.auth.face.faceauth.R
import com.auth.face.faceauth.base.Utils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_face_auth.*
import java.io.IOException

class FaceAuthActivity : AppCompatActivity() {

    private lateinit var viewModel: FaceAuthViewModel

    private var mCameraSource: CameraSource? = null
    private var liveFaceDetector: LiveFaceDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_auth)

        initializeViewModel()

        createCameraSource()
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(FaceAuthViewModel::class.java)
        viewModel.userPhoto.observe(this, Observer { it -> ivPhoto.setImageBitmap(it) })
        viewModel.username.observe(this, Observer { it -> tvUserName.setText(it) })
        viewModel.dob.observe(this, Observer { it -> tvDob.setText(it) })
        viewModel.loadingResId.observe(this, Observer { it -> setLoading(it) })
        viewModel.infoTextResId.observe(this, Observer { it -> setInfoText(it) })
        viewModel.faceState.observe(this, Observer { it -> updateState(it) })
        viewModel.initialize()
    }

    private fun setLoading(loading: Int?) {
        loading?.let {
            if (it == 0) {
                llInfoPanel.visibility = View.GONE
            } else {
                llInfoPanel.visibility = View.VISIBLE
                pbProgress.visibility = View.VISIBLE
                tvInfoText.text = getString(loading)
            }
        }
    }

    private fun setInfoText(infoTextResId: Int?) {
        infoTextResId?.let {
            if (it == 0) {
                llInfoPanel.visibility = View.GONE
            } else {
                llInfoPanel.visibility = View.VISIBLE
                pbProgress.visibility = View.GONE
                tvInfoText.text = getString(infoTextResId)
            }
        }
    }

    private fun createCameraSource() {
        val context = applicationContext
        val detector = FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.5F)
                .build()

        liveFaceDetector = LiveFaceDetector(detector)
        liveFaceDetector!!.setProcessor(
                MultiProcessor.Builder<Face>(GraphicFaceTrackerFactory())
                        .build())

        mCameraSource = CameraSource.Builder(context, liveFaceDetector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build()
    }

    override fun onResume() {
        super.onResume()

        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        preview.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCameraSource != null) {
            mCameraSource?.release()
        }
    }

    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, 4000)
            dlg.show()
            return
        }

        if (mCameraSource != null) {
            try {
                preview.start(mCameraSource!!, faceOverlay)
            } catch (e: IOException) {
                Log.e("FaceAuth", "Unable to start camera source.", e)
                mCameraSource?.release()
                mCameraSource = null
            }
        }
    }

    private inner class GraphicFaceTrackerFactory : MultiProcessor.Factory<Face> {
        override fun create(face: Face): Tracker<Face> {
            return GraphicFaceTracker(faceOverlay)
        }
    }

    private inner class GraphicFaceTracker (val mOverlay: FacialGraphicOverlay) : Tracker<Face>() {
        private val mFaceGraphic: FacialGraphic

        init {
            mFaceGraphic = FacialGraphic(mOverlay)
        }

        override fun onNewItem(faceId: Int, item: Face?) {
            //
        }

        override fun onUpdate(detectionResults: Detector.Detections<Face>?, face: Face?) {
            mOverlay.add(mFaceGraphic)
            mFaceGraphic.updateLiveFaceDetails(face)
            val frame = liveFaceDetector?.frame
            if (frame != null) {
                viewModel.faceImageNotifier.onNext(frame)
            }
        }

        override fun onMissing(detectionResults: Detector.Detections<Face>?) {
            mFaceGraphic.updateLiveFaceDetails(null)
        }

        override fun onDone() {
            mFaceGraphic.updateLiveFaceDetails(null)
        }
    }

    private fun updateState(state: FaceState?) {
        Utils.blinkView(vStateRed, false)
        Utils.blinkView(vStateOrange, false)
        Utils.blinkView(vStateGreen, false)

        when (state) {
            FaceState.VERIFYING -> {
                Utils.blinkView(vStateOrange, true)
                Utils.blinkView(vStateRed, true)
            }
            FaceState.MATCH -> {
                Utils.blinkView(vStateGreen, true)
            }
            FaceState.FAILURE -> {
                Utils.blinkView(vStateRed, true)
            }
            FaceState.WAITING -> {
                Utils.blinkView(vStateRed, true)
            }

        }
    }

}
