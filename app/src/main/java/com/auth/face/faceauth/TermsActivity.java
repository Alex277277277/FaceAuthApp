package com.auth.face.faceauth;

import android.os.Bundle;
import android.webkit.WebView;

import com.auth.face.faceauth.navigation.RegisterScreenRouter;

import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_terms);

        WebView web = findViewById(R.id.web);
        web.loadData(readHtmlFromAssets(), "text/html", "UTF-8");

        findViewById(R.id.btAccept).setOnClickListener(v -> new RegisterScreenRouter().route(this));
        findViewById(R.id.btDecline).setOnClickListener(v -> finish());
    }

    private String readHtmlFromAssets() {
        try {
            InputStream is = getAssets().open("terms.html");
            byte [] byteArray = new byte[65535];
            is.read(byteArray);
            return new String(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
