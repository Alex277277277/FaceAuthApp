package com.auth.face.faceauth;

import com.auth.face.faceauth.base.AppException;
import com.auth.face.faceauth.logger.LoggerInstance;

public class ApiManager {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + ApiManager.class.getSimpleName();

    private static final String LOGIN_URL = "http://testportalapp2.azurewebsites.net/api/user?uId=2122";
    private static final String TAG_MARKER = "\\\"";
    private static final String TAG_END_MARKER = "\\\":";

    public LoginResult login(String userName, String password) {
        LoginResult loginResult = new LoginResult();
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();
            String httpResponse = httpCommunicator.loginRequest(LOGIN_URL, userName, password);
            return parseLoginResponse(httpResponse);
        } catch (AppException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
            loginResult.setError(e.getMessage());
        }
        return loginResult;
    }

    private LoginResult parseLoginResponse(String loginResponse) {
        LoginResult loginResult = new LoginResult();
        loginResult.setBase64Photo(parseImageTag(loginResponse, "image"));
        loginResult.setUsername(parseTag(loginResponse, "name"));
        loginResult.setDob(parseTag(loginResponse, "dob"));
        return loginResult;
    }

    private String parseTag(String data, String tagName) {
        LoggerInstance.get().debug(TAG, " Parsing tag: " + tagName);

        int tagNamePositionEnd = data.indexOf(tagName + TAG_END_MARKER) + tagName.length() + TAG_END_MARKER.length();
        if (tagNamePositionEnd == -1) {
            throw new AppException(tagName + " : tag not found");
        }

        int tagDataPositionStart = data.indexOf(TAG_MARKER, tagNamePositionEnd) + TAG_MARKER.length();
        if (tagDataPositionStart == -1) {
            throw new AppException(tagName + " : tag data not found");
        }

        int tagDataPositionEnd = data.indexOf(TAG_MARKER, tagDataPositionStart);
        if (tagDataPositionEnd == -1) {
            throw new AppException(tagName + " : tag data end not found");
        }

        String tagValue = data.substring(tagDataPositionStart, tagDataPositionEnd);
        LoggerInstance.get().debug(TAG, " Tag parsed, value : " + tagValue);
        return tagValue;
    }

    private String parseImageTag(String data, String imageTagName) {
        LoggerInstance.get().debug(TAG, " Parsing image tag: " + imageTagName);

        int tagNamePositionEnd = data.indexOf(imageTagName + TAG_END_MARKER) + imageTagName.length() + TAG_END_MARKER.length();
        if (tagNamePositionEnd == -1) {
            throw new AppException(imageTagName + " : tag not found");
        }

        String base64Marker = "base64,";
        int tagDataPositionStart = data.indexOf(base64Marker, tagNamePositionEnd) + base64Marker.length();
        if (tagDataPositionStart == -1) {
            throw new AppException(imageTagName + " : tag data not found");
        }

        int tagDataPositionEnd = data.indexOf(TAG_MARKER, tagDataPositionStart);
        if (tagDataPositionEnd == -1) {
            throw new AppException(imageTagName + " : tag data end not found");
        }

        String tagValue = data.substring(tagDataPositionStart, tagDataPositionEnd).trim();
        LoggerInstance.get().debug(TAG, " Image tag parsed");
        return tagValue;
    }

}
