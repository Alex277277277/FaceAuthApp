package com.auth.face.faceauth.login;

import android.Manifest;
import android.app.Application;
import android.location.Location;
import android.text.TextUtils;
import android.widget.Toast;

import com.auth.face.faceauth.ApiManager;
import com.auth.face.faceauth.FaceAuthApp;
import com.auth.face.faceauth.LocationController;
import com.auth.face.faceauth.LoginResult;
import com.auth.face.faceauth.PrefStorage;
import com.auth.face.faceauth.QrCodeResult;
import com.auth.face.faceauth.base.LocationReadyListener;
import com.auth.face.faceauth.base.Utils;
import com.auth.face.faceauth.navigation.FaceScreenRouter;
import com.auth.face.faceauth.navigation.PromoScreenRouter;
import com.auth.face.faceauth.navigation.Router;
import com.auth.face.faceauth.promo.PromotionResult;
import com.auth.face.faceauth.R;
import com.auth.face.faceauth.base.BaseViewModel;
import com.auth.face.faceauth.logger.LoggerInstance;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + LoginViewModel.class.getSimpleName();

    private final MutableLiveData<Router> router = new MutableLiveData<>();

    private ApiManager apiManager;
    private double lat;
    private double lng;

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
                .fromCallable(() -> apiManager.login(userName, password))
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoginSuccess, this::onLoginFailed)
        );
    }

    private void onLoginSuccess(LoginResult result) {
        hideLoading();

        if (!TextUtils.isEmpty(result.getError())) {
            Toast.makeText(mContext, result.getError(), Toast.LENGTH_LONG).show();
            return;
        }

        String photoBase64 = result.getBase64Photo();
        if (TextUtils.isEmpty(photoBase64)) {
            Toast.makeText(mContext, R.string.err_login_failed, Toast.LENGTH_LONG).show();
            return;
        }

        String username = result.getUsername();
        if (username == null) {
            username = "";
        }

        String userId = result.getUserId();
        if (userId == null) {
            userId = "";
        }

        String dob = result.getDob();
        if (dob == null) {
            dob = "";
        }


        PrefStorage prefs = FaceAuthApp.Companion.getApp().getPrefs();
        prefs.setPhoto(photoBase64);
        prefs.setUsername(username);
        prefs.setUserId(userId);
        prefs.setDob(dob);

        getPromotion();
    }

    private void onLoginFailed(Throwable t) {
        LoggerInstance.get().error(TAG, "onLoginFailed ", t);
        Toast.makeText(mContext, R.string.err_connection , Toast.LENGTH_LONG).show();
    }

    private void getPromotion() {
        showLoading(R.string.signing_in);
        subscribe(Single
                .fromCallable(() -> apiManager.getPromotion(lat, lng))
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPromotionSuccess, this::onPromotionFailed)
        );
    }

    private void onPromotionSuccess(PromotionResult promotionResult) {
        hideLoading();

        PrefStorage prefs = FaceAuthApp.Companion.getApp().getPrefs();
        String photoBase64 = promotionResult.getBase64Photo();
        String id = promotionResult.getId();
        if (!TextUtils.isEmpty(photoBase64) && !TextUtils.isEmpty(id)) {
            prefs.setPromoImage(photoBase64);
            prefs.setPromoId(id);
            router.setValue(new PromoScreenRouter());
            return;
        }

        prefs.setPromoImage("");
        prefs.setPromoId("");
        router.setValue(new FaceScreenRouter());
    }

    private void onPromotionFailed(Throwable t) {
        hideLoading();
        PrefStorage prefs = FaceAuthApp.Companion.getApp().getPrefs();
        prefs.setPromoImage("");
        prefs.setPromoId("");
        router.setValue(new FaceScreenRouter());
    }

    public MutableLiveData<Router> getRouter() {
        return router;
    }

    public void requestCurrentLocation() {
        String[] locationPermission = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
        if (!Utils.checkSelfPermissions(locationPermission, mContext)) {
            Toast.makeText(mContext, R.string.err_location_permissions, Toast.LENGTH_LONG).show();
            return;
        }

        if (!LocationController.isLocationServiceEnabled(mContext)) {
            Toast.makeText(mContext, R.string.err_location_disabled, Toast.LENGTH_LONG).show();
            return;
        }

        LocationController locationController = new LocationController(mContext, new LocationReadyListener() {
            @Override
            public void onReady(@NotNull Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        });

        locationController.requestCurrentLocation();
    }

}
