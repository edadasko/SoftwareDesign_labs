package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.battleship.model.GameStats;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static android.view.View.GONE;

public class StatisticsActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference statsRef;

    private ArrayList<GameStats> stats;
    private StatsAdapter statsAdapter;
    private ListView statsListView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stats = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        statsRef = database.getReference("stats");
        statsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        collectStatistics((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        setContentView(R.layout.activity_statistics);
    }

    private void collectStatistics(Map<String,Object> stats) {
        for (Map.Entry<String, Object> entry : stats.entrySet()){
            Map stat = (Map) entry.getValue();
            this.stats.add(new GameStats(
                    (String)stat.get("user1"),
                    (String)stat.get("user2"),
                    (Long)stat.get("score1"),
                    (Long)stat.get("score2"),
                    new Date((Long)stat.get("date"))));
        }


        statsAdapter = new StatsAdapter(this, R.layout.stats, this.stats);
        statsListView = findViewById(R.id.statistics_list_view);
        statsListView.setAdapter(statsAdapter);
        progressBar = findViewById(R.id.progress_stat);
        progressBar.setVisibility(GONE);
    }
}
