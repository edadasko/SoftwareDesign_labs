package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Note> notes = new ArrayList<>();

    AdapterView notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInitialData();

        NoteAdapter stateAdapter = new NoteAdapter(this, R.layout.note, notes);
        notesList = findViewById(R.id.notesList);

        notesList.setAdapter(stateAdapter);
        notesList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("orientation", getResources().getConfiguration().orientation);
                intent.putExtra("note", notes.get(position));
                startActivity(intent);
            }
        });
    }

    private void setInitialData(){

        notes.add(new Note("note 1", "aaa a a a aaa ", "a b c d"));
        notes.add(new Note("note 2", "weffwefewwef", "b c d f"));
        notes.add(new Note("", "addfs", "c d"));
        notes.add(new Note("note 4", "xcvvcx ", "a b"));
    }
}
