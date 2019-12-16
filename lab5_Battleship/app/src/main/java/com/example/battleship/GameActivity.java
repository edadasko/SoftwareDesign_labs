package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

    private String gameId;

    private GameGridView player1GridView;
    private GameGridView player2GridView;

    private FirebaseDatabase database;
    private DatabaseReference grid1Ref;
    private DatabaseReference grid2Ref;
    private DatabaseReference currentMoveRef;

    private TextView firstEmailTextView;
    private TextView secondEmailTextView;

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
        grid1Ref = database.getReference("games").child(gameId).child("player1Grid");
        grid2Ref = database.getReference("games").child(gameId).child("player2Grid");
        currentMoveRef = database.getReference("games").child(gameId).child("currentMove");

        grid1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                if (value.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Connection error!",
                            Toast.LENGTH_SHORT).show();
                    grid1Ref.removeEventListener(this);
                    finish();
                }

                Gson gson = new Gson();
                Type type = new TypeToken<Grid>(){}.getType();
                if(isCreator)
                    player1GridView.updateGrid(gson.fromJson(value, type));
                else {
                    player2GridView.updateGrid(gson.fromJson(value, type));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        grid2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                if (isOpponentsConnected && value.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Connection error!",
                            Toast.LENGTH_SHORT).show();
                    grid2Ref.removeEventListener(this);
                    finish();
                }

                Gson gson = new Gson();
                Type type = new TypeToken<Grid>(){}.getType();

                if (!value.isEmpty()) {
                    isOpponentsConnected = true;
                    if (isCreator)
                        player2GridView.updateGrid(gson.fromJson(value, type));
                    else {
                        player1GridView.updateGrid(gson.fromJson(value, type));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        grid2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (!value.isEmpty() && isCreator) {
                    isOpponentsConnected = true;
                    Toast.makeText(getApplicationContext(),
                            "Opponent connects! Your move!",
                            Toast.LENGTH_SHORT).show();
                    player2GridView.setMode(GridDrawMode.Opponent);
                    grid2Ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        currentMoveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!isOpponentsConnected)
                    return;

                int value = dataSnapshot.getValue(int.class);
                if (isCreator) {
                    if (value == 1) {
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
                    if (value == 2) {
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

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        player1GridView.initGrid(GridDrawMode.Player);
        player2GridView.initGrid(GridDrawMode.Opponent);

        player2GridView.setMode(GridDrawMode.Inactive);

        DatabaseReference email1 = database.getReference("games").child(gameId).child(isCreator ? "player1Email" : "player2Email");

        email1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                firstEmailTextView = findViewById(R.id.first_email);
                firstEmailTextView.setText(value);
                email1.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference email2 = database.getReference("games").child(gameId).child(isCreator ? "player2Email" : "player1Email");

        email2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(value.isEmpty())
                    return;
                secondEmailTextView = findViewById(R.id.second_email);
                secondEmailTextView.setText(value);
                email2.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void updateGrids(PlayerMoveStatus status) {
        if (status == PlayerMoveStatus.Attacked) {
            Toast.makeText(getApplicationContext(),
                        "Your move again!",
                    Toast.LENGTH_SHORT).show();
        }
        else if (status == PlayerMoveStatus.Missed){
            if (isCreator) {
                currentMoveRef.setValue(2);
            }
            else {
                currentMoveRef.setValue(1);
            }
        }
        else
            return;

        Gson gson = new Gson();
        String jsonGrid1 = gson.toJson(player1GridView.getGrid());
        if (isCreator)
            grid1Ref.setValue(jsonGrid1);
        else
            grid2Ref.setValue(jsonGrid1);

        String jsonGrid2 = gson.toJson(player2GridView.getGrid());
        if (isCreator)
            grid2Ref.setValue(jsonGrid2);
        else
            grid1Ref.setValue(jsonGrid2);
    }

    @Override
    public void onBackPressed() {
        grid1Ref.setValue("");
        grid2Ref.setValue("");

        super.onBackPressed();
    }
}
