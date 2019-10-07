package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import android.content.pm.ActivityInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayVersionNum();
    }

    private void displayVersionNum() {
        TextView versionNum = findViewById(R.id.version_num);
        versionNum.append(" " + BuildConfig.VERSION_NAME);
    }
}
