package com.auth.face.faceauth;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QrCodeResult implements Serializable {

    @SerializedName("Qr_Code")
    private String base64Photo;

    private String error;

    public String getBase64Photo() {
        return base64Photo;
    }

    public void setBase64Photo(String base64Photo) {
        this.base64Photo = base64Photo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
