package dev.yonathaniel.tenakatauni;

import android.app.Application;

import androidx.multidex.MultiDex;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
