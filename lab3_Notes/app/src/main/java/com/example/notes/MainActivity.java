package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Note> notes = new ArrayList<>();

    AdapterView notesListView;
    NoteAdapter noteAdapter;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addButton);
        notesListView = findViewById(R.id.notesList);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("note", notes.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        NotesDatabaseAdapter adapter = new NotesDatabaseAdapter(this);
        adapter.open();

        notes = adapter.getNotes();

        noteAdapter = new NoteAdapter(this, R.layout.note, notes);
        notesListView = findViewById(R.id.notesList);
        notesListView.setAdapter(noteAdapter);
        adapter.close();
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("note", new Note());
        startActivity(intent);
    }
}
