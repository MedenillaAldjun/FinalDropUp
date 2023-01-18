package com.group.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private View logo, logo_text, logo_tag, logo_bot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        logo_text = findViewById(R.id.logo_text);
        logo_bot = findViewById(R.id.logo_bot);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                   Intent intent = new Intent(SplashActivity.this, SendOTP.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this,
                            Pair.create(logo,"logo"),
                            Pair.create(logo_text, "logo_text"),
                            Pair.create(logo_bot, "logo_bot"));
                    startActivity(intent, options.toBundle());
                }else {
                    startActivity(new Intent(SplashActivity.this, Main2.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        }, 1500);

    }
}