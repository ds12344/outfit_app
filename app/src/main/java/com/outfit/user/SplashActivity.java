package com.outfit.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent;
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }
}
