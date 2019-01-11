package com.auth.face.faceauth.login;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.auth.face.faceauth.ApiManager;
import com.auth.face.faceauth.FaceAuthApp;
import com.auth.face.faceauth.R;
import com.auth.face.faceauth.RegisterResult;
import com.auth.face.faceauth.base.BaseViewModel;
import com.auth.face.faceauth.logger.LoggerInstance;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegisterViewModel extends BaseViewModel {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + RegisterViewModel.class.getSimpleName();

    private final MutableLiveData<String> uniqueId = new MutableLiveData<>();

    private ApiManager apiManager;

    public RegisterViewModel(Application application) {
        super(application);
        apiManager = new ApiManager();
    }

    public void register(String userName, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(mContext, R.string.err_empty_username, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(mContext, R.string.err_empty_email, Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, R.string.err_empty_password, Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(mContext, R.string.err_passwords_not_match, Toast.LENGTH_LONG).show();
            return;
        }

        showLoading(R.string.registering);
        subscribe(Single
                .fromCallable(() -> apiManager.register(userName, email, password))
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRegisterSuccess, this::onRegisterFailed)
        );
    }

    private void onRegisterSuccess(RegisterResult result) {
        hideLoading();

        if (!TextUtils.isEmpty(result.getError())) {
            Toast.makeText(mContext, result.getError(), Toast.LENGTH_LONG).show();
            return;
        }

        String id = result.getUniqueId();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(mContext, R.string.err_register_failed, Toast.LENGTH_LONG).show();
            return;
        }

        uniqueId.setValue(id);
    }

    private void onRegisterFailed(Throwable t) {
        LoggerInstance.get().error(TAG, "onLoginFailed ", t);
        Toast.makeText(mContext, R.string.err_connection , Toast.LENGTH_LONG).show();
    }

    public MutableLiveData<String> getUniqueId() {
        return uniqueId;
    }

}
