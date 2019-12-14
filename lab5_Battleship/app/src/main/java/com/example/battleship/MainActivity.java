package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.battleship.model.Player;

public class MainActivity extends AppCompatActivity {

    GameGridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = findViewById(R.id.gridView);
        gridView.setPlayers(new Player(), new Player(), GridDrawMode.Opponent);
    }
}
