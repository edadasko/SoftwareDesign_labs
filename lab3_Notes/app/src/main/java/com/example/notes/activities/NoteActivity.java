package com.example.notes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notes.models.Note;
import com.example.notes.adapters.NotesDatabaseAdapter;
import com.example.notes.R;

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

        fillEdits();
    }

    private void fillEdits() {
        Bundle arguments = getIntent().getExtras();

        assert arguments != null;
        note = (Note)arguments.getSerializable("note");

        assert note != null;

        if (note.hasTitle())
            titleEdit.setText(note.getTitle());
        tagsEdit.setText(note.getStringOfTags());
        bodyEdit.setText(note.getBody());
    }

    public void save(View view){
        String title = titleEdit.getText().toString();
        String body = bodyEdit.getText().toString();
        String tags = tagsEdit.getText().toString();

        if (body.equals("")) {
            showErrorToast();
            return;
        }

        Note newNote = new Note(note.getId(), title, body, tags);

        adapter.open();
        if (note.getId() != 0)
            adapter.update(newNote);
        else
            adapter.insert(newNote);

        adapter.close();
        goBack();
    }

    public void delete(View view){
        if (note.getId() == 0)
            goBack();

        adapter.open();
        adapter.delete(note.getId());
        adapter.close();
        goBack();
    }

    private void goBack(){
        this.onBackPressed();
    }

    private void showErrorToast() {
        Toast toast = Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.bodyNotFilledError),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
