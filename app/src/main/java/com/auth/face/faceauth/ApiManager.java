package com.auth.face.faceauth;

import android.text.TextUtils;

import com.auth.face.faceauth.base.AppException;
import com.auth.face.faceauth.logger.LoggerInstance;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class ApiManager {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + ApiManager.class.getSimpleName();

    private static final String LOGIN_URL = "http://testportalapp3.azurewebsites.net/api/user?uId=%s";

    public LoginResult login(String userName, String password) {
        LoginResult loginResult = new LoginResult();
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();

            String userNameEncoded = URLEncoder.encode(userName, "UTF-8");
            String url = String.format(LOGIN_URL, userNameEncoded);
            String httpResponse = httpCommunicator.loginRequest(url, userName, password);
            return parseJson(httpResponse);
        } catch (AppException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
            loginResult.setError(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
        }
        return loginResult;
    }

    private LoginResult parseJson(String json) {
        try {
            JsonArray topArrayJsonObject = getTopArrayJsonObject("image", json);
            List<LoginResult> resultList =
                    new Gson().fromJson(
                            topArrayJsonObject,
                            new TypeToken<List<LoginResult>>() {
                            }.getType()
                    );
            LoginResult result = resultList.get(0);

            if (TextUtils.isEmpty(result.getUsername())) {
                throw new AppException("Unable to parse user name from the server response");
            }
            if (TextUtils.isEmpty(result.getDob())) {
                throw new AppException("Unable to parse DOB from the server response");
            }
            if (TextUtils.isEmpty(result.getBase64Photo())) {
                throw new AppException("Unable to parse base64 photo from the server response");
            }

            String marker = "base64,";
            String webBase64Str = result.getBase64Photo();
            int dataPosition = webBase64Str.indexOf(marker) + marker.length();
            String base64Image = webBase64Str.substring(dataPosition);
            result.setBase64Photo(base64Image);
            return result;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Unable to parse server response");
        }
    }

    private JsonArray getTopArrayJsonObject(String topName, String jsonStr) {
        Gson gson = new Gson();
        JsonElement jsonTopLevelEl = gson.fromJson(jsonStr, JsonElement.class);
        JsonObject jsonTopLevelObject = jsonTopLevelEl.getAsJsonObject();
        return jsonTopLevelObject.getAsJsonArray(topName);
    }

}
