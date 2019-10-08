package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.provider.Settings.Secure;

import android.content.pm.ActivityInfo;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String PERMISSION_STRING
        = android.Manifest.permission.READ_PHONE_STATE;
    private final int PERMISSION_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayVersionNum();
        displayID();
    }

    private void displayVersionNum() {
        TextView versionNum = findViewById(R.id.version_num);
        final String text = getResources().getString(R.string.version_num)
                            + " " + BuildConfig.VERSION_NAME;
        versionNum.setText(text);
    }

    private void displayID() {
        if (checkSelfPermission(PERMISSION_STRING) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {PERMISSION_STRING}, PERMISSION_REQUEST_CODE);
        }
        else {
            TextView id = findViewById(R.id.device_id);
            final String text = getResources().getString(R.string.device_id)
                                + " " + Secure.getString(getContentResolver(), Secure.ANDROID_ID);
            id.setText(text);
        }
    }

    public void requestIdPermissionWithExplanation() {
        if (shouldShowRequestPermissionRationale(PERMISSION_STRING)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.permission)
                    .setCancelable(false)
                    .setNegativeButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    displayID();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Toast.makeText(this, R.string.denied_permission, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayID();
            } else {
                requestIdPermissionWithExplanation();
            }
        }
    }
}