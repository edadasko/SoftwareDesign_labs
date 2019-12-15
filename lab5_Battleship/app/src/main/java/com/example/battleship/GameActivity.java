package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.battleship.model.GameInfo;
import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;
import com.example.battleship.model.PlayerMoveStatus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GameActivity extends AppCompatActivity {
    boolean isCreator;

    public String gameId;

    GameGridView player1GridView;
    GameGridView player2GridView;

    FirebaseDatabase database;
    DatabaseReference gameRef;

    boolean isCreatorsMove = true;
    boolean isOpponentsConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        player1GridView = findViewById(R.id.player1_grid);
        player2GridView = findViewById(R.id.player2_grid);

        gameId = getIntent().getStringExtra("gameId");
        isCreator = getIntent().getBooleanExtra("creator", false);

        database =  FirebaseDatabase.getInstance();
        gameRef = database.getReference("games").child(gameId);
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameInfo value = dataSnapshot.getValue(GameInfo.class);
                if (value == null)
                    return;

                Gson gson = new Gson();
                Type type = new TypeToken<Grid>(){}.getType();
                if (value.player1Grid != null && !value.player1Grid.isEmpty()) {
                    if(isCreator)
                        player1GridView.updateGrid(gson.fromJson(value.player1Grid, type));
                    else {
                        player2GridView.updateGrid(gson.fromJson(value.player1Grid, type));
                    }
                }
                if (value.player2Grid != null && !value.player2Grid.isEmpty()) {
                    isOpponentsConnected = true;
                    if(isCreator)
                        player2GridView.updateGrid(gson.fromJson(value.player2Grid, type));
                    else {
                        player1GridView.updateGrid(gson.fromJson(value.player2Grid, type));
                    }
                }

                if (isOpponentsConnected && (value.player1Grid.isEmpty() || value.player2Grid.isEmpty())) {
                    Toast.makeText(getApplicationContext(),
                            "Connection error!",
                            Toast.LENGTH_SHORT).show();
                    isOpponentsConnected = false;
                    gameRef.removeEventListener(this);
                    finish();
                }

                if (isOpponentsConnected) {
                    isCreatorsMove = !isCreatorsMove;

                    if (isCreator) {
                        if (isCreatorsMove) {
                            player2GridView.setMode(GridDrawMode.Opponent);
                            Toast.makeText(getApplicationContext(),
                                    "Your move!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            player2GridView.setMode(GridDrawMode.Inactive);
                            Toast.makeText(getApplicationContext(),
                                    "Opponents move!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!isCreatorsMove) {
                            Toast.makeText(getApplicationContext(),
                                    "Your move!",
                                    Toast.LENGTH_SHORT).show();
                            player2GridView.setMode(GridDrawMode.Opponent);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Opponents move!",
                                    Toast.LENGTH_SHORT).show();
                            player2GridView.setMode(GridDrawMode.Inactive);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        player1GridView.initGrid(GridDrawMode.Player);
        player2GridView.initGrid(GridDrawMode.Opponent);

        if (!isCreator)
            player2GridView.setMode(GridDrawMode.Inactive);
    }

    public void updateGrids(PlayerMoveStatus status) {
        Gson gson = new Gson();
        String jsonGrid1 = gson.toJson(player1GridView.getGrid());
        gameRef.child(isCreator ? "player1Grid" : "player2Grid").setValue(jsonGrid1);
        String jsonGrid2 = gson.toJson(player2GridView.getGrid());
        gameRef.child(isCreator ? "player2Grid" : "player1Grid").setValue(jsonGrid2);
    }

    @Override
    public void onBackPressed() {
        gameRef.child(isCreator ? "player1Grid" : "player2Grid").setValue("");

        super.onBackPressed();
    }
}
