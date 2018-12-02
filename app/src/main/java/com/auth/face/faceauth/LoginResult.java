package com.auth.face.faceauth;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResult implements Serializable {

    @SerializedName("image")
    private String base64Photo;

    @SerializedName("name")
    private String username;

    @SerializedName("dob")
    private String dob;

    private String error;

    public String getBase64Photo() {
        return base64Photo;
    }

    public void setBase64Photo(String base64Photo) {
        this.base64Photo = base64Photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
