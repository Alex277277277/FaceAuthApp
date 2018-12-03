package com.auth.face.faceauth.face;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.auth.face.faceauth.FaceAuthApp;
import com.auth.face.faceauth.PrefStorage;
import com.auth.face.faceauth.R;
import com.auth.face.faceauth.base.BaseViewModel;
import com.auth.face.faceauth.base.Utils;
import com.auth.face.faceauth.logger.LoggerInstance;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class FaceAuthViewModel extends BaseViewModel {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + FaceAuthViewModel.class.getSimpleName();

    private static final long POLLING_INTERVAL = 1000;
    private static final long INTERVAL_MATCH = 10*1000;
    private static final long INTERVAL_NOT_MATCH = 5*1000;

    private boolean initialized;

    private FaceServiceRestClient faceServiceClient;
    private UUID photoFaceId;

    private Scheduler processingScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
    private PublishSubject<Bitmap> faceImageNotifier = PublishSubject.create();

    private final MutableLiveData<Bitmap> userPhoto = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> dob = new MutableLiveData<>();

    private final MutableLiveData<FaceState> faceState = new MutableLiveData<>();
    private final MutableLiveData<Integer> infoTextResId = new MutableLiveData<>();

    public FaceAuthViewModel(Application application) {
        super(application);
    }

    public MutableLiveData<Bitmap> getUserPhoto() {
        return userPhoto;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<String> getDob() {
        return dob;
    }

    public MutableLiveData<Integer> getInfoTextResId() {
        return infoTextResId;
    }

    public PublishSubject<Bitmap> getFaceImageNotifier() {
        return faceImageNotifier;
    }

    public MutableLiveData<FaceState> getFaceState() {
        return faceState;
    }

    public void initialize() {
        if (!initialized) {

            faceState.setValue(FaceState.WAITING);

            faceServiceClient = new FaceServiceRestClient(mContext.getString(R.string.endpoint), mContext.getString(R.string.subscription_key));

            loadUserData();

            detectFaceOnPhoto();

            subscribeToCameraFrames();

            initialized = true;
        }
    }

    private void loadUserData() {
        PrefStorage prefs = FaceAuthApp.Companion.getApp().getPrefs();

        String base64Photo = prefs.getPhoto();
        byte[] photoData = Utils.fromBase64(base64Photo);
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
        userPhoto.setValue(bitmap);

        username.setValue(prefs.getUsername());
        dob.setValue(prefs.getDob());
    }

    private void detectFaceOnPhoto() {
        showLoading(R.string.photo_face_detection);
        subscribe(
                Single
                        .fromCallable(() -> doFaceDetection(userPhoto.getValue()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onFaceDetected, this::onFaceDetectionFailed)
        );
    }

    private void onFaceDetected(UUID id) {
        LoggerInstance.get().debug(TAG, "onFaceDetected");
        hideLoading();
        photoFaceId = id;
    }

    private void onFaceDetectionFailed(Throwable throwable) {
        LoggerInstance.get().debug(TAG, "onFaceDetectionFailed " + throwable);
        hideLoading();
        if (throwable instanceof FaceNotDetectedException) {
            infoTextResId.setValue(R.string.err_face_not_detected);
        } else {
            infoTextResId.setValue(R.string.err_detection);
        }
    }

    private UUID doFaceDetection(Bitmap bitmap) throws ClientException, IOException {
        LoggerInstance.get().debug(TAG, "doFaceDetection -> converting bitmap");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        LoggerInstance.get().debug(TAG, "doFaceDetection -> DETECTING...");
        Face[] faces = faceServiceClient.detect(inputStream, true, false, null);

        LoggerInstance.get().debug(TAG, "doFaceDetection -> completed, faces found: " + faces.length);
        if (faces.length == 0) {
            throw new FaceNotDetectedException("Face not detected");
        }
        return faces[0].faceId;
    }

    private VerifyResult doFaceVerification(UUID faceId1, UUID faceId2) throws ClientException, IOException {
        LoggerInstance.get().debug(TAG, "doFaceVerification -> VERIFYING...");
        VerifyResult verifyResult = faceServiceClient.verify(faceId1, faceId2);
        LoggerInstance.get().debug(TAG, "doFaceVerification -> verification completed, confidence = " + verifyResult.confidence + " is identical = " + verifyResult.isIdentical);
        return verifyResult;
    }

    private void subscribeToCameraFrames() {
        subscribe(faceImageNotifier
                .sample(POLLING_INTERVAL, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(v -> faceState.getValue() == FaceState.WAITING)
                .filter(v -> photoFaceId != null)
                .doOnNext(v -> faceState.setValue(FaceState.VERIFYING))
                .observeOn(processingScheduler)
                .map(this::doFaceDetection)
                .map(faceId -> doFaceVerification(photoFaceId, faceId))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::onFaceVerificationFailed)
                .retry()
                .subscribe(this::onFaceVerificationSuccess));
    }

    private void onFaceVerificationSuccess(VerifyResult verifyResult) {
        if (verifyResult.isIdentical) {
            LoggerInstance.get().debug(TAG, "onFaceVerificationSuccess -> FACES MATCH");
            faceState.setValue(FaceState.MATCH);
            postWaitingStateDelayed(INTERVAL_MATCH);
        } else {
            LoggerInstance.get().debug(TAG, "onFaceVerificationSuccess -> FACES DON'T MATCH");
            faceState.setValue(FaceState.FAILURE);
            postWaitingStateDelayed(INTERVAL_NOT_MATCH);
        }
    }

    private void onFaceVerificationFailed(Throwable throwable) {
        LoggerInstance.get().debug(TAG, "onFaceVerificationFailed " + throwable);
        faceState.setValue(FaceState.FAILURE);
        postWaitingStateDelayed(INTERVAL_NOT_MATCH);
    }

    private void postWaitingStateDelayed(long delay) {
        new Handler().postDelayed(() -> faceState.setValue(FaceState.WAITING), delay);
    }

}
