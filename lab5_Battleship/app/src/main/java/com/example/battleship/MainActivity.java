package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.battleship.model.GameInfo;
import com.example.battleship.model.Grid;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Type;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button loginButton;
    private ProgressBar progressBar;

    Grid grid;

    boolean isCreator;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_GRID = "grid";

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String strGrid = mSettings.getString(APP_PREFERENCES_GRID, "");

        Type type = new TypeToken<Grid>(){}.getType();

        Gson gson = new Gson();
        if (!strGrid.isEmpty()) {
            grid = gson.fromJson(strGrid, type);
        }

        email.setVisibility(GONE);
        loginButton.setVisibility(GONE);
        password.setVisibility(GONE);
        progressBar.setVisibility(GONE);
    }

    public void createGridButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, CreateGridActivity.class);
        if (grid == null)
            grid = new Grid();
        intent.putExtra("grid", grid);
        startActivity(intent);
    }

    public void startNewGame(View view) {
        start(true);
    }

    public void startGameById(View view) {
        start(false);
    }

    private void start(boolean isCreator) {
        if (grid == null) {
            Toast.makeText(this, "At first create your game grid.", LENGTH_SHORT).show();
            return;
        }

        this.isCreator = isCreator;

        if (!arePlayServicesOk()) {
            return;
        }
        if (isAnonymous()) {
            this.isCreator = isCreator;
            email.setVisibility(VISIBLE);
            loginButton.setVisibility(VISIBLE);
            password.setVisibility(VISIBLE);
        } else {
            showIdRequest();
        }
    }

    private void showIdRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter ID:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startConnection(input.getText().toString());
            }
        });

        builder.show();
    }


    private void startConnection(String gameId) {
        progressBar.setVisibility(VISIBLE);
        String email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference game = database.getReference("games").child(gameId);

        ValueEventListener checkListener;

        if (isCreator) {
            checkListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Gson gson = new Gson();
                        String jsonGrid = gson.toJson(grid);
                        game.removeEventListener(this);
                        game.setValue(new GameInfo(email, "", jsonGrid, ""));
                        startGame(gameId, true);
                    } else {
                        showIdErrorMessage();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }
        else {
            checkListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Gson gson = new Gson();
                        String jsonGrid = gson.toJson(grid);
                        game.removeEventListener(this);
                        game.child("player2Email").setValue(email);
                        game.child("player2Grid").setValue(jsonGrid);
                        startGame(gameId, false);
                    } else {
                        showIdErrorMessage();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        game.addValueEventListener(checkListener);
    }

    public void login(View view) {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            return;
        }

        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        if (isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter correct email", LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should have at least 6 characters", LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showIdRequest();
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task1 -> {
                                    if (!isAnonymous()) {
                                        showIdRequest();
                                    }
                                }).addOnFailureListener(task2 -> {
                                    hideProgressDialog();
                                    Toast.makeText(
                                            this,
                                            "Invalid email or password.",
                                            Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void showProgressDialog() {
        progressBar.setVisibility(VISIBLE);
    }

    private void hideProgressDialog() {
        progressBar.setVisibility(GONE);
    }

    private boolean isAnonymous() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser == null || currentUser.isAnonymous();
    }

    private boolean arePlayServicesOk() {
        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, 5000).show();
            }
            return false;
        }

        return true;
    }

    private void startGame(String gameId, boolean isCreator) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("creator", isCreator);
        intent.putExtra("gameId", gameId);
        startActivity(intent);
    }

    private void showIdErrorMessage() {
        Toast.makeText(this, "ID is invalid.", LENGTH_SHORT).show();
        hideProgressDialog();
    }

    public void showStatisticsButtonClick(View view) {
        startActivity(new Intent(this, StatisticsActivity.class));
    }
}
