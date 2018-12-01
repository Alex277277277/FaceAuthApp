package com.auth.face.faceauth.base;

import android.util.Base64;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class Utils {

    public static String toBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] fromBase64(String base64String) {
        return Base64.decode(base64String, Base64.DEFAULT);
    }

    public static void blinkView(View view, boolean needBlinking) {
        if (!needBlinking) {
            view.clearAnimation();
            return;
        }
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }

}
