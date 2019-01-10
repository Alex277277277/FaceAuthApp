package com.auth.face.faceauth;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterResult implements Serializable {

    @SerializedName("id")
    private String uniqueId;

    private String error;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
