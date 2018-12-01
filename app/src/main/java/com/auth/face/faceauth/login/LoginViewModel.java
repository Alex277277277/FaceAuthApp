package com.auth.face.faceauth.login;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.widget.Toast;

import com.auth.face.faceauth.ApiManager;
import com.auth.face.faceauth.FaceAuthApp;
import com.auth.face.faceauth.LoginResult;
import com.auth.face.faceauth.PrefStorage;
import com.auth.face.faceauth.R;
import com.auth.face.faceauth.base.BaseViewModel;
import com.auth.face.faceauth.logger.LoggerInstance;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + LoginViewModel.class.getSimpleName();

    private final MutableLiveData<Void> router = new MutableLiveData<>();

    private ApiManager apiManager;

    public LoginViewModel(Application application) {
        super(application);
        apiManager = new ApiManager();
    }

    public void login(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(mContext, R.string.err_empty_username, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, R.string.err_empty_password, Toast.LENGTH_LONG).show();
            return;
        }

        showLoading(R.string.signing_in);
        subscribe(Single
                .fromCallable(() -> apiManager.login(mContext, userName, password))
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoginSuccess, this::onLoginFailed)
        );
    }

    private void onLoginSuccess(LoginResult result) {
        hideLoading();

        String photoBase64 = result.getBase64Photo();
        if (TextUtils.isEmpty(photoBase64)) {
            Toast.makeText(mContext, R.string.err_login_failed, Toast.LENGTH_LONG).show();
            return;
        }

        String username = result.getUsername();
        if (username == null) {
            username = "";
        }

        String dob = result.getDob();
        if (dob == null) {
            dob = "";
        }


        PrefStorage prefs = FaceAuthApp.Companion.getApp().getPrefs();
        prefs.setPhoto(photoBase64);
        prefs.setUsername(username);
        prefs.setDob(dob);

        router.setValue(null);
    }

    private void onLoginFailed(Throwable t) {
        LoggerInstance.get().error(TAG, "onLoginFailed ", t);
        Toast.makeText(mContext, R.string.err_connection , Toast.LENGTH_LONG).show();
    }

    public MutableLiveData<Void> getRouter() {
        return router;
    }

}
