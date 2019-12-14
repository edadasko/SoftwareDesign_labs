package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;
import com.google.gson.Gson;

public class CreateGridActivity extends AppCompatActivity {

    private GameGridView gridView;
    private Grid grid;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        grid = (Grid) getIntent().getSerializableExtra("grid");
        setContentView(R.layout.activity_create_grid);
        gridView = findViewById(R.id.gridView);
        gridView.initGrid(grid);
        mSettings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveGrid(View view) {
        GridValidator validator = new GridValidator(grid);
        if(!validator.Validate()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Grid is invalid",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            SharedPreferences.Editor editor = mSettings.edit();
            Gson gson = new Gson();
            String jsonCache = gson.toJson(grid);
            editor.putString(MainActivity.APP_PREFERENCES_GRID, jsonCache);
            editor.apply();
        }
        else {
            SharedPreferences.Editor editor = mSettings.edit();
            Gson gson = new Gson();
            String jsonCache = gson.toJson(grid);
            editor.putString(MainActivity.APP_PREFERENCES_GRID, jsonCache);
            editor.apply();
            this.onBackPressed();
        }
    }
}
