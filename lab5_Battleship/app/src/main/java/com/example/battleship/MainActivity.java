package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    Player player;
    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_GRID = "grid";

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = new Player();
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String strGrid = mSettings.getString(APP_PREFERENCES_GRID, "");

        Type type = new TypeToken<Grid>(){}.getType();

        Gson gson = new Gson();
        if (!strGrid.isEmpty()) {
            player.setGrid((Grid)gson.fromJson(strGrid, type));
        }
    }

    public void createGridButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, CreateGridActivity.class);
        intent.putExtra("grid", player.getGrid());
        startActivity(intent);
    }

}
