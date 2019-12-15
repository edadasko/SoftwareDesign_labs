package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;

public class GameActivity extends AppCompatActivity {
    Player player1;
    Player player2;

    GameGridView player1GridView;
    GameGridView player2GridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        player1 = (Player) getIntent().getSerializableExtra("player1");
        setContentView(R.layout.activity_game);

        player1GridView = findViewById(R.id.player1_grid);
        player2GridView = findViewById(R.id.player2_grid);
        player1GridView.initGrid(player1.getGrid());
        player2GridView.initGrid();
    }
}
