package ca.on.conestogac.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import ca.on.conestogac.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        Runnable runnable = () -> {
            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
            finish();
        };
        handler.postDelayed(runnable, 5000);
    }
}
