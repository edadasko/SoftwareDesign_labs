package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NoteActivity extends AppCompatActivity {

    EditText titleEdit;
    EditText tagsEdit;
    EditText bodyEdit;

    private NotesDatabaseAdapter adapter;

    Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        titleEdit = findViewById(R.id.titleEdit);
        tagsEdit = findViewById(R.id.tagsEdit);
        bodyEdit = findViewById(R.id.bodyEdit);
        adapter = new NotesDatabaseAdapter(this);

        Bundle arguments = getIntent().getExtras();

        assert arguments != null;
        note = (Note)arguments.getSerializable("note");

        assert note != null;

        if (note.hasTitle())
            titleEdit.setText(note.getTitle());
        tagsEdit.setText(note.getStringOfTags());
        bodyEdit.setText(note.getBody());
    }

    public void Save(View view){
        String title = titleEdit.getText().toString();
        String body = bodyEdit.getText().toString();
        String tags = tagsEdit.getText().toString();

        Note user = new Note(note.getId(), title, body, tags);

        adapter.open();
        if (note.getId() != 0)
            adapter.update(user);
        else
            adapter.insert(user);

        adapter.close();
        goHome();
    }

    public void Delete(View view){
        if (note.getId() == 0)
            goHome();

        adapter.open();
        adapter.delete(note.getId());
        adapter.close();
        goHome();
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
