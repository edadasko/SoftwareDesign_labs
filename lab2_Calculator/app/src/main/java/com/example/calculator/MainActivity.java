package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    boolean isInScientificMode = false;
    Fragment scientificButtonsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            isInScientificMode = false;
            hideScientificButtons();
        }
        else
        {
            isInScientificMode = true;
        }

    }

    public void changeMode(View view) {
        if (isInScientificMode)
            hideScientificButtons();
        else
            showScientificButtons();
    }

    private void hideScientificButtons() {
        isInScientificMode = false;
        scientificButtonsFragment = getSupportFragmentManager().findFragmentById(R.id.scientificButtonsFragment);
        if (scientificButtonsFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(scientificButtonsFragment)
                    .commit();
            findViewById(R.id.basicButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    5));
        }
    }

    private void showScientificButtons() {
        isInScientificMode = true;
        scientificButtonsFragment = getSupportFragmentManager().findFragmentById(R.id.scientificButtonsFragment);
        if (scientificButtonsFragment != null) {
                findViewById(R.id.scientificButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        0,
                        2));
                findViewById(R.id.basicButtonsFragment).setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        0,
                        3));
            }

            getSupportFragmentManager().beginTransaction()
                    .show(scientificButtonsFragment)
                    .commit();
        
    }

}
