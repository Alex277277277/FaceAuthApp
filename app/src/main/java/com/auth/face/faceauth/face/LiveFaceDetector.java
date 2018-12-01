package com.auth.face.faceauth.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;

import java.io.ByteArrayOutputStream;

public class LiveFaceDetector extends Detector<Face> {

    private Detector<Face> delegate;
    private Bitmap frameImage;

    public LiveFaceDetector(Detector<Face> delegate) {
        this.delegate = delegate;
    }

    @Override
    public SparseArray<Face> detect(Frame frame) {
        YuvImage var2 = new YuvImage(frame.getGrayscaleImageData().array(), 17, frame.getMetadata().getWidth(), frame.getMetadata().getHeight(), (int[])null);
        ByteArrayOutputStream var3 = new ByteArrayOutputStream();
        var2.compressToJpeg(new Rect(0, 0, frame.getMetadata().getWidth(), frame.getMetadata().getHeight()), 100, var3);
        byte[] var4 = var3.toByteArray();
        this.frameImage = BitmapFactory.decodeByteArray(var4, 0, var4.length);
        this.frameImage = rotateBitmap(this.frameImage, 270.0F);
        return delegate.detect(frame);
    }

    @Override
    public boolean isOperational() {
        return delegate.isOperational();
    }

    public boolean setFocus(int var1) {
        return delegate.setFocus(var1);
    }

    private static Bitmap rotateBitmap(Bitmap var0, float rotation) {
        Matrix var2 = new Matrix();
        var2.postRotate(rotation);
        return Bitmap.createBitmap(var0, 0, 0, var0.getWidth(), var0.getHeight(), var2, true);
    }

    public Bitmap getFrame() {
        return this.frameImage;
    }

}
