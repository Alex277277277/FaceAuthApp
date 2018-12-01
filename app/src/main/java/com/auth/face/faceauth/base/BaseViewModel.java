package com.auth.face.faceauth.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.StringRes;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends AndroidViewModel {

    protected final Context mContext;
    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Integer> loadingResId = new MutableLiveData<>();
    private MutableLiveData<DialogDataHolder> dialog = new MutableLiveData<>();

    public BaseViewModel(Application application) {
        super(application);
        mContext = application;
    }

    public final MutableLiveData<Integer> getLoadingResId() {
        return loadingResId;
    }

    public final MutableLiveData<DialogDataHolder> getDialog() {
        return dialog;
    }

    public final LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }

    protected final void showLoading(@StringRes int messageResId) {
        loadingResId.setValue(messageResId);
    }

    protected final void hideLoading() {
        loadingResId.setValue(0);
    }

    protected final boolean isLoading() {
        return loadingResId.getValue() != null && loadingResId.getValue() != 0;
    }

    protected final void setErrorMessageString(@StringRes int messageResId) {
        setErrorMessage(mContext.getString(messageResId));
    }

    protected final void setErrorMessage(String message) {
        errorMessage.setValue(message);
    }

    protected final void showDialog(DialogDataHolder dialogDataHolder) {
        dialog.setValue(dialogDataHolder);
    }

    // Don't change to protected!!! Otherwise method reference from subclasses causing crash
    public void onFailedResult(Throwable t) {
        hideLoading();
        if (t != null && t instanceof AppException && t.getMessage() != null) {
            showDialog(new DialogDataHolder(t.getMessage()));
        }
    }

    protected final void subscribe(Disposable disposable) {
        mDisposable.add(disposable);
    }
}
