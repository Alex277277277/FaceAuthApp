package com.auth.face.faceauth;

import com.auth.face.faceauth.base.AppException;
import com.auth.face.faceauth.logger.LoggerInstance;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCommunicator {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + HttpCommunicator.class.getSimpleName();
    private static final int HTTP_CONNECTION_TIMEOUT = 30*1000; // 30 sec
    private static final int HTTP_READ_TIMEOUT = 30*1000; // 30 sec

    private OkHttpClient httpClient;

    public HttpCommunicator() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public String httpRequest(String url, String username, String password) {
        return httpRequest(url, username, password, false);
    }

    public String httpRequest(String url, String username, String password, boolean isPost) {
        LoggerInstance.get().info(TAG, "Http request... " + url);
        Request.Builder requestBuilder = new Request.Builder().url(url);
        if (username != null && password != null) {
            requestBuilder.addHeader("Authorization", Credentials.basic(username,password));
        }
        if (isPost) {
            requestBuilder.post(new FormBody.Builder().build());
        }

        Response response = null;
        try {
            response = httpClient.newCall(requestBuilder.build()).execute();
            if (!response.isSuccessful()) {
                LoggerInstance.get().error(TAG, "Http server request failed with status code = " + response.code() + " and status message = " + response.message());
                throw new AppException("Login error : " + response.message());
            }

            if (response.body() == null) {
                LoggerInstance.get().error(TAG, "Http server response is empty");
                throw new AppException("Login error : empty response");
            }

            String responseData = response.body().string();
            LoggerInstance.get().info(TAG, "Http response = " + responseData);
            return responseData;
        } catch (IOException e) {
            LoggerInstance.get().error(TAG, "Unable to connect to the server" + e);
            throw new AppException("Unable to connect to the server");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
