package com.auth.face.faceauth;

import android.content.Context;
import android.util.Log;

import com.auth.face.faceauth.base.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ApiManager {

    public LoginResult login(Context context, String userName, String password) {
        LoginResult loginResult = new LoginResult();
        try {
            byte[] imageData = loadMockImage(context);
            if (imageData != null) {
                Log.v("FaceAuth", "Base64 ok, length = " + imageData.length);
                loginResult.setBase64Photo(Utils.toBase64(imageData));
                loginResult.setUsername("Alex");
                loginResult.setDob("11-11-2000");
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return loginResult;
    }

    private byte[] loadMockImage(Context context) {
        String filePath = context
                .getExternalFilesDir(null)
                .getAbsolutePath() + "/mock0.jpg";
        File file = new File(filePath);
        if (file.exists()) {
            Log.v("FaceAuth", "File exists");
        } else {
            Log.v("FaceAuth", "File NOT exists");
        }

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            Log.v("FaceAuth", "Converted");
            return ous.toByteArray();

        } catch (Exception e) {

        }
        finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {

            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {

            }
        }
        return null;
    }


}
