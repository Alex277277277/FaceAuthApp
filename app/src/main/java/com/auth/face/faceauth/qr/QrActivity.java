package com.auth.face.faceauth.qr;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth.face.faceauth.ProfileResult;
import com.auth.face.faceauth.R;
import com.auth.face.faceauth.base.Utils;
import com.auth.face.faceauth.qr.camera.CameraSource;
import com.auth.face.faceauth.qr.camera.CameraSourcePreview;
import com.auth.face.faceauth.qr.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class QrActivity extends AppCompatActivity {

    private static final String TAG = "Barcode-reader";

    private static final int RC_HANDLE_GMS = 9001;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private View mProfile;

    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvDob;
    private TextView tvUserId;
    private TextView tvId;

    private QrViewModel viewModel;

    private BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        mProfile = findViewById(R.id.profile);
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);
        mGraphicOverlay.setShowText(false);
        mGraphicOverlay.setRectColors(null);
        mGraphicOverlay.setDrawRect(false);

        ivPhoto = findViewById(R.id.ivPhoto);
        tvName = findViewById(R.id.tvName);
        tvDob = findViewById(R.id.tvDob);
        tvUserId = findViewById(R.id.tvUserId);
        tvId = findViewById(R.id.tvId);

        initializeViewModel();

        createCameraSource();
    }

    private void initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(QrViewModel.class);
        viewModel.getProfileResult().observe(this, this::onProfileReady);
    }

    private void onProfileReady(ProfileResult profileResult) {
        mPreview.setVisibility(View.GONE);
        mProfile.setVisibility(View.VISIBLE);

        String base64Photo = profileResult.getBase64Photo();
        byte[] photoData = Utils.fromBase64(base64Photo);
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
        ivPhoto.setImageBitmap(bitmap);

        tvName.setText(profileResult.getUsername());
        tvDob.setText(profileResult.getDob());
        tvUserId.setText(profileResult.getUserId());
        tvId.setText(profileResult.getId());
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource() {
        Detector<Barcode> barcodeDetector = getCustomBarcodeDetector();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay) {
            @Override
            void onCodeDetected(Barcode barcode) {
                runOnUiThread(() -> viewModel.onBarcodeRetrieved(barcode));
            }
        };

        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(height, width)
                .setRequestedFps(15.0f)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .setFlashMode(null)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        new Thread(() -> {
            if (mPreview != null) {
                mPreview.stop();
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    private void startCameraSource() throws SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public Detector<Barcode> getCustomBarcodeDetector() {
        if (barcodeDetector == null)
            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.ALL_FORMATS)
                    .build();
        return barcodeDetector;
    }

}
