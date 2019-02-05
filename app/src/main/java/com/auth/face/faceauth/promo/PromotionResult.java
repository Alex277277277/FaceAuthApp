package com.auth.face.faceauth.promo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PromotionResult implements Serializable {

    @SerializedName("image")
    private String base64Photo;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    private String error;

    public String getBase64Photo() {
        return base64Photo;
    }

    public void setBase64Photo(String base64Photo) {
        this.base64Photo = base64Photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
