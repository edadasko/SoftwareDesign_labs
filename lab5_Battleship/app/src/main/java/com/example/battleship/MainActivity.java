package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Trace;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.battleship.model.GameInfo;
import com.example.battleship.model.Grid;
import com.example.battleship.model.Player;
import com.example.battleship.model.PlayerInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Type;

import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "StartActivity";
    private boolean logginIn;

    private EditText name;
    private EditText email;
    private EditText password;
    private Button loginButton;
    private ProgressBar progressBar;

    Grid grid;
    Player player;

    boolean isCreator;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_GRID = "grid";

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        name = findViewById(R.id.input_name);
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
    }

    public void createGridButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, CreateGridActivity.class);
        if (grid == null)
            grid = new Grid();
        intent.putExtra("grid", grid);
        startActivity(intent);
    }

    public void startMultiplayer(View view) {
        if (grid == null) {
            Toast.makeText(this, "At first create your game grid.", LENGTH_SHORT).show();
            return;
        }

        if (!arePlayServicesOk()) {
            return;
        }
        if (isAnonymous()) {
            isCreator = true;
            name.setVisibility(VISIBLE);
            email.setVisibility(VISIBLE);
            loginButton.setVisibility(VISIBLE);
            password.setVisibility(VISIBLE);
        } else {
            createGame();
        }
    }

    public void startGameById(View view) {
        if (grid == null) {
            Toast.makeText(this, "At first create your game grid.", LENGTH_SHORT).show();
            return;
        }

        if (!arePlayServicesOk()) {
            return;
        }
        if (isAnonymous()) {
            isCreator = false;
            name.setVisibility(VISIBLE);
            email.setVisibility(VISIBLE);
            loginButton.setVisibility(VISIBLE);
            password.setVisibility(VISIBLE);
        } else {
            connectToGame();
        }
    }

    public void loginWithEmail(View view) {
        String email = this.email.getText().toString();
        String name = this.name.getText().toString();
        String password = this.password.getText().toString();
        if (logginIn) {
            return;
        }
        if (isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter correct email", LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(name)) {
            Toast.makeText(this, "Enter correct name", LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password should have at least 6 characters", LENGTH_SHORT).show();
            return;
        }

        logginIn = true;
        showProgressDialog();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (isCreator)
                            createGame();
                        else
                            connectToGame();
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task1 -> {
                                    if (!isAnonymous()) {
                                        if (isCreator)
                                            createGame();
                                        else
                                            connectToGame();
                                    }

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

    private void createGame() {
        String uid =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        Player player = new Player(uid, grid);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference games = database.getReference();

        Gson gson = new Gson();
        String jsonGrid = gson.toJson(grid);
        games.child("games").child("firstGame").setValue(new GameInfo(uid, "", jsonGrid, ""));

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("player1", player);
        intent.putExtra("creator", true);
        startActivity(intent);
    }

    private void connectToGame() {
        String uid =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        Player player = new Player(uid, grid);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference game = database.getReference("games").child("firstGame");

        Gson gson = new Gson();
        String jsonGrid = gson.toJson(grid);
        game.child("player2Id").setValue(uid);
        game.child("player2Grid").setValue(jsonGrid);

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("player2", player);
        intent.putExtra("creator", false);
        startActivity(intent);
    }


}
