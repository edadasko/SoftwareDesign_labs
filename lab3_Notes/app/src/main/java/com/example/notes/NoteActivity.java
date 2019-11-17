package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity {

    EditText title;
    EditText tags;
    EditText body;

    Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        title = findViewById(R.id.titleEdit);
        tags = findViewById(R.id.tagsEdit);
        body = findViewById(R.id.bodyEdit);

        Bundle arguments = getIntent().getExtras();

        assert arguments != null;
        note = (Note)arguments.getSerializable("note");

        assert note != null;

        if (note.hasTitle())
            title.setText(note.getTitle());
        tags.setText(note.getStringOfTags());
        body.setText(note.getBody());
    }
}
