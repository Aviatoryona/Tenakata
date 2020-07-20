package dev.yonathaniel.tenakatauni;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        // TODO: 7/19/2020
        new Handler().postDelayed(() -> {
            finish();
            startActivity(new Intent(Splash.this, GetLocationActivity.class));
        }, 4000);
    }
}