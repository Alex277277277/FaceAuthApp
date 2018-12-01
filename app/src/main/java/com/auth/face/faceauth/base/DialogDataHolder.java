package com.auth.face.faceauth.base;

public class DialogDataHolder {
    public boolean active = true;
    public String title;
    public String message;
    public String positiveButtonLabel;
    public String negativeButtonLabel;
    public String neutralButtonLabel;

    public DialogDataHolder(String title, String message) {
        this(message);
        this.title = title;
    }

    public DialogDataHolder(String message) {
        this.message = message;
    }

}
