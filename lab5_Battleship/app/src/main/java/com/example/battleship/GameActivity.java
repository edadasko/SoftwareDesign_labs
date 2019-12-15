package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.battleship.model.GameInfo;
import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GameActivity extends AppCompatActivity {
    Player player;
    boolean isCreator;
    GameGridView player1GridView;
    GameGridView player2GridView;

    FirebaseDatabase database;
    DatabaseReference gameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        player1GridView = findViewById(R.id.player1_grid);
        player2GridView = findViewById(R.id.player2_grid);

        database =  FirebaseDatabase.getInstance();
        gameRef = database.getReference("games").child("firstGame");
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GameInfo value = dataSnapshot.getValue(GameInfo.class);
                if (value == null)
                    return;

                Gson gson = new Gson();
                Type type = new TypeToken<Grid>(){}.getType();
                if (value.player1Grid != null && !value.player1Grid.isEmpty()) {
                    if(isCreator)
                        player1GridView.updateGrid(gson.fromJson(value.player1Grid, type));
                    else
                        player2GridView.updateGrid(gson.fromJson(value.player1Grid, type));
                }
                if (value.player2Grid != null && !value.player2Grid.isEmpty()) {
                    if(isCreator)
                        player2GridView.updateGrid(gson.fromJson(value.player2Grid, type));
                    else
                        player1GridView.updateGrid(gson.fromJson(value.player2Grid, type));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        isCreator = getIntent().getBooleanExtra("creator", false);
        if (isCreator)
            player = (Player) getIntent().getSerializableExtra("player1");
        else
            player = (Player) getIntent().getSerializableExtra("player2");

        player1GridView.initGrid(GridDrawMode.Player);
        player2GridView.initGrid(GridDrawMode.Opponent);
    }
}
