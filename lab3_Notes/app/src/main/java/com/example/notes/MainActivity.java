package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Note> notes = new ArrayList<>();

    View notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInitialData();

        NoteAdapter stateAdapter = new NoteAdapter(this, R.layout.note, notes);
        notesList = findViewById(R.id.notesList);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            ((ListView)notesList).setAdapter(stateAdapter);
        else
            ((GridView)notesList).setAdapter(stateAdapter);
    }

    private void setInitialData(){

        notes.add(new Note("note 1", "aaa a a a aaa ", "a b c d"));
        notes.add(new Note("note 2", "weffwefewwef", "b c d f"));
        notes.add(new Note("", "addfs", "c d"));
        notes.add(new Note("note 4", "xcvvcx ", "a b"));
    }
}
