package com.example.notes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.notes.models.Note;
import com.example.notes.SortTypes;
import com.example.notes.R;

public class MainActivity extends AppCompatActivity {

    public interface OnActivitySortListener {
        void onActivitySortListener(SortTypes sortType);
    }
    public interface OnActivityFindListener {
        void onActivityFindListener(String tag);
    }

    private OnActivitySortListener sortListener;
    private OnActivityFindListener findListener;

    private EditText tagFindEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.notesListFragment);
        sortListener = (OnActivitySortListener) fragment;
        findListener = (OnActivityFindListener) fragment;

        Spinner spinner = findViewById(R.id.sortSpinner);
        ArrayAdapter<SortTypes> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SortTypes.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SortTypes sortType = (SortTypes)parent.getItemAtPosition(position);
                sortListener.onActivitySortListener(sortType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tagFindEdit = findViewById(R.id.tagFindEdit);
        tagFindEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                findListener.onActivityFindListener(editable.toString());
            }
        });
        tagFindEdit.clearFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        tagFindEdit.clearFocus();
    }

    public void addButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        intent.putExtra("note", new Note());
        startActivity(intent);
    }
}
