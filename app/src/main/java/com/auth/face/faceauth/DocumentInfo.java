package com.auth.face.faceauth;

import com.google.gson.annotations.SerializedName;

public class DocumentInfo {

    @SerializedName("documentFront")
    private String documentFront;

    @SerializedName("documentBack")
    private String documentBack;

    @SerializedName("identityID")
    private String identityId;

    public String getDocumentFront() {
        return documentFront;
    }

    public void setDocumentFront(String documentFront) {
        this.documentFront = documentFront;
    }

    public String getDocumentBack() {
        return documentBack;
    }

    public void setDocumentBack(String documentBack) {
        this.documentBack = documentBack;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }
}
