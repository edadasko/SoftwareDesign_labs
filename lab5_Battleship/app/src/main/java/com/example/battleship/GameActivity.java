package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.Date;

public class GameActivity extends AppCompatActivity {
    boolean isCreator;

    private String gameId;

    private GameGridView player1GridView;
    private GameGridView player2GridView;

    private FirebaseDatabase database;
    private DatabaseReference grid1Ref;
    private DatabaseReference grid2Ref;
    private DatabaseReference player1ScoreRef;
    private DatabaseReference player2ScoreRef;
    private DatabaseReference currentMoveRef;

    private TextView firstEmailTextView;
    private TextView secondEmailTextView;

    private TextView firstScoreTextView;
    private TextView secondScoreTextView;

    boolean isOpponentsConnected = false;

    final int MAX_SCORE = 20;
    public int score1 = 0;
    public int score2 = 0;

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
        player1ScoreRef = database.getReference("games").child(gameId).child("player1Score");
        player2ScoreRef = database.getReference("games").child(gameId).child("player2Score");
        currentMoveRef = database.getReference("games").child(gameId).child("currentMove");

        grid1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value == null) {
                    onBackPressed();
                    return;
                }

                if (value.isEmpty()) {
                    finish();
                    return;
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
                if (value == null) {
                    onBackPressed();
                    return;
                }

                if (isOpponentsConnected && value.isEmpty()) {
                    finish();
                    return;
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
                if (value == null) {
                    onBackPressed();
                    return;
                }

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

                int value;
                try {
                    value = dataSnapshot.getValue(int.class);
                }
                catch (NullPointerException exp) {
                    onBackPressed();
                    return;
                }

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
                if (value == null) {
                    onBackPressed();
                    return;
                }
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
                if(value == null) {
                    onBackPressed();
                    return;
                }
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

        player1ScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int value;

                try {
                    value = dataSnapshot.getValue(int.class);
                }
                catch (NullPointerException exp) {
                    onBackPressed();
                    return;
                }

                score1 = value;
                if (isCreator) {
                    firstScoreTextView = findViewById(R.id.first_score);
                    firstScoreTextView.setText(String.valueOf(score1));
                }
                else {
                    secondScoreTextView = findViewById(R.id.second_score);
                    secondScoreTextView.setText(String.valueOf(score1));
                }

                if (value == MAX_SCORE) {
                    if (isCreator) {
                        showWinMessage();
                    }
                    else {
                        showLooseMessage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        player2ScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int value;

                try {
                    value = dataSnapshot.getValue(int.class);
                }
                catch (NullPointerException exp) {
                    onBackPressed();
                    return;
                }

                score2 = value;
                if (isCreator) {
                    secondScoreTextView = findViewById(R.id.second_score);
                    secondScoreTextView.setText(String.valueOf(score2));
                }
                else {
                    firstScoreTextView = findViewById(R.id.first_score);
                    firstScoreTextView.setText(String.valueOf(score2));
                }

                if (value == MAX_SCORE) {
                    if (!isCreator) {
                        showWinMessage();
                    }
                    else {
                        showLooseMessage();
                    }
                }
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
            if (isCreator) {
                score1++;
                player1ScoreRef.setValue(score1);
            }
            else {
                score2++;
                player2ScoreRef.setValue(score2);
            }
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
        database.getReference("games").child(gameId).removeValue();

        super.onBackPressed();
    }

    private void showWinMessage() {
        showMessage("You win!");
    }

    private void showLooseMessage() {
        showMessage("You lose :(");
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.getReference("games").child(gameId).removeValue();
                saveStatistics();
                onBackPressed();
            }
        });

        builder.show();
    }

    private void saveStatistics() {
        DatabaseReference stats = database.getReference("stats").child(gameId);
        firstEmailTextView = findViewById(R.id.first_email);
        stats.child("user1").setValue(firstEmailTextView.getText());
        secondEmailTextView = findViewById(R.id.second_email);
        stats.child("user2").setValue(secondEmailTextView.getText());
        stats.child("score1").setValue(score1);
        stats.child("score2").setValue(score2);
        stats.child("date").setValue(new Date(System.currentTimeMillis()));
    }
}
