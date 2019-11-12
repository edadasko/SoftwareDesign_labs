package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Note> notes = new ArrayList();

    ListView notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInitialData();

        notesList = findViewById(R.id.notesList);
        NoteAdapter stateAdapter = new NoteAdapter(this, R.layout.note, notes);
        notesList.setAdapter(stateAdapter);
    }

    private void setInitialData(){

        notes.add(new Note("note 1", "aaa a a a aaa ", "a b c d"));
        notes.add(new Note("note 2", "weffwefewwef", "b c d f"));
        notes.add(new Note("", "addfs", "c d"));
        notes.add(new Note("note 4", "xcvvcx ", "a b"));
    }
}
