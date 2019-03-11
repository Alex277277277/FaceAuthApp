package com.auth.face.faceauth.qr;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.auth.face.faceauth.ApiManager;
import com.auth.face.faceauth.FaceAuthApp;
import com.auth.face.faceauth.PrefStorage;
import com.auth.face.faceauth.ProfileResult;
import com.auth.face.faceauth.R;
import com.auth.face.faceauth.base.BaseViewModel;
import com.auth.face.faceauth.logger.LoggerInstance;
import com.google.android.gms.vision.barcode.Barcode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class QrViewModel extends BaseViewModel {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + QrViewModel.class.getSimpleName();

    private PrefStorage prefs = FaceAuthApp.Companion.getApp().getPrefs();
    private ApiManager apiManager;
    private boolean isScanActive = true;

    private final MutableLiveData<ProfileResult> profileResult = new MutableLiveData<>();

    public QrViewModel(Application application) {
        super(application);
        apiManager = new ApiManager();
    }

    LiveData<ProfileResult> getProfileResult() {
        return profileResult;
    }

    void onBarcodeRetrieved(Barcode barcode) {
        if (!isScanActive) {
            return;
        }

        isScanActive = false;
        profile(barcode.displayValue);
    }

    void reactivateScaner() {
        isScanActive = true;
    }

    private void profile(String url) {
        Log.v("FaceAuth", "profile -> url = " + url);
        showLoading(R.string.loading_profile);
        subscribe(Single
                .fromCallable(() -> apiManager.profile(url, prefs.getUsername(), "yesMan"))
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onProfileSuccess, this::onProfileFailed)
        );
    }

    private void onProfileSuccess(ProfileResult result) {
        hideLoading();

        if (!TextUtils.isEmpty(result.getError())) {
            Toast.makeText(mContext, result.getError(), Toast.LENGTH_LONG).show();
            isScanActive = true;
            return;
        }

        profileResult.setValue(result);
    }

    private void onProfileFailed(Throwable t) {
        LoggerInstance.get().error(TAG, "onProfileFailed ", t);
        Toast.makeText(mContext, R.string.err_connection , Toast.LENGTH_LONG).show();
        isScanActive = true;
    }

}
