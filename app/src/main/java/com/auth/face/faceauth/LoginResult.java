package com.auth.face.faceauth;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResult implements Serializable {

    @SerializedName("image")
    private String base64Photo;

    @SerializedName("name")
    private String username;

    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("dob")
    private String dob;

    @SerializedName("verifier")
    private boolean verifier;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isVerifier() {
        return verifier;
    }

    public void setVerifier(boolean verifier) {
        this.verifier = verifier;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
