package com.auth.face.faceauth;

import android.text.TextUtils;
import android.util.Log;

import com.auth.face.faceauth.base.AppException;
import com.auth.face.faceauth.logger.LoggerInstance;
import com.auth.face.faceauth.promo.PromotionResult;
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

    private static final String LOGIN_URL = "http://iidapidev.azurewebsites.net/api/user?uId=%s";
    private static final String REGISTER_URL = "http://iidapidev.azurewebsites.net/api/generator?name=%s&pass=%s&email=%s";

    private static final String PROMOTION_URL = "http://iidapidev.azurewebsites.net/api/promotion?xcord=%s&ycord=%s";
    private static final String QR_CODE_URL = "http://iidapidev.azurewebsites.net/api/qrcode/%s";
    //private static final String QR_CODE_URL = "http://testportalapp3.azurewebsites.net/api/qrcode/?id=%s";

    //private static final String LOGIN_URL = "http://testportalapp3.azurewebsites.net/api/user?uId=%s";
    //private static final String REGISTER_URL = "https://testportalapp3.azurewebsites.net/api/generator?name=%s&pass=%s&email=%s";

    //private static final String PROMOTION_URL = "http://testportalapp3.azurewebsites.net/api/promotion?xcord=%s&ycord=%s";

    public RegisterResult register(String userName, String email, String password) {
        RegisterResult registerResult = new RegisterResult();
        String passwordForBasicAuth = "yesMan";    // hardcoded
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();

            String userNameEncoded = URLEncoder.encode(userName, "UTF-8");
            String passwordEncoded = URLEncoder.encode(password, "UTF-8");
            String emailEncoded = URLEncoder.encode(email, "UTF-8");
            String url = String.format(REGISTER_URL, userNameEncoded, passwordEncoded, emailEncoded);
            String httpResponse = httpCommunicator.httpRequest(url, userName, passwordForBasicAuth, true);
            registerResult.setUniqueId(httpResponse.substring(1, httpResponse.length() - 1));
        }  catch (AppException e) {
            LoggerInstance.get().error(TAG, " Registration failed: ", e);
            registerResult.setError(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
        }
        return registerResult;
    }

    public LoginResult login(String userName, String password) {
        LoginResult loginResult = new LoginResult();
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();

            String userNameEncoded = URLEncoder.encode(userName, "UTF-8");
            String url = String.format(LOGIN_URL, userNameEncoded);
            String httpResponse = httpCommunicator.httpRequest(url, userName, password);
            return parseJson(httpResponse);
        } catch (AppException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
            loginResult.setError(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
        }
        return loginResult;
    }

    public ProfileResult profile(String url, String userName, String password) {
        ProfileResult profileResult = new ProfileResult();
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();

            String httpResponse = httpCommunicator.httpRequest(url, userName, password);
            Log.v("FaceAuth", "Profile = " + httpResponse);
            return parseProfileJson(httpResponse);
        } catch (AppException e) {
            LoggerInstance.get().error(TAG, " Profile failed: ", e);
            profileResult.setError(e.getMessage());
        }
        return profileResult;
    }

    public PromotionResult getPromotion(double lat, double lng) {
        PromotionResult promotionResult = new PromotionResult();
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();

            String url = String.format(PROMOTION_URL, String.valueOf(lat), String.valueOf(lng));
            String httpResponse = httpCommunicator.httpRequest(url, null, null);
            return parsePromotionJson(httpResponse);
        } catch (AppException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
            promotionResult.setError(e.getMessage());
        }
        return promotionResult;
    }

    public QrCodeResult getQrCode(String id) {
        QrCodeResult qrCodeResult = new QrCodeResult();
        try {
            HttpCommunicator httpCommunicator = new HttpCommunicator();

            String idEncoded = URLEncoder.encode(id, "UTF-8");
            String passwordForBasicAuth = "yesMan";    // hardcoded
            String userForBasicAuth = idEncoded;    // hardcoded, any string is ok
            String url = String.format(QR_CODE_URL, idEncoded);
            String httpResponse = httpCommunicator.httpRequest(url, userForBasicAuth, passwordForBasicAuth);
            return parseQrCodeJson(httpResponse);
        } catch (AppException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
            qrCodeResult.setError(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            LoggerInstance.get().error(TAG, " Login failed: ", e);
        }
        return qrCodeResult;
    }

    private LoginResult parseJson(String json) {
        try {
            LoginResult result = new Gson().fromJson(json, LoginResult.class);

            if (TextUtils.isEmpty(result.getUsername())) {
                throw new AppException("Unable to parse user name from the server response");
            }
            if (TextUtils.isEmpty(result.getId())) {
                throw new AppException("Unable to parse id from the server response");
            }
            if (TextUtils.isEmpty(result.getUserId())) {
                throw new AppException("Unable to parse user id from the server response");
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

    private ProfileResult parseProfileJson(String json) {
        try {
            ProfileResult result = new Gson().fromJson(json, ProfileResult.class);
            String marker = "base64,";
            String webBase64Str = result.getBase64Photo();
            if (!TextUtils.isEmpty(webBase64Str)) {
                int dataPosition = webBase64Str.indexOf(marker) + marker.length();
                String base64Image = webBase64Str.substring(dataPosition);
                result.setBase64Photo(base64Image);
            }
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

    private PromotionResult parsePromotionJson(String json) {
        try {
            JsonArray topArrayJsonObject = getTopArrayJsonObject("image", json);
            List<PromotionResult> resultList =
                    new Gson().fromJson(
                            topArrayJsonObject,
                            new TypeToken<List<PromotionResult>>() {
                            }.getType()
                    );
            PromotionResult result = resultList.get(0);

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

    private QrCodeResult parseQrCodeJson(String json) {
        return new Gson().fromJson(json, QrCodeResult.class);
    }

}
