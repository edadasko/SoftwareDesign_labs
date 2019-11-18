package com.example.notes;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NoteListFragment extends Fragment implements MainActivity.OnActivitySortListener {

    private List<Note> notes = new ArrayList<>();
    private AdapterView notesListView;
    private NoteAdapter noteAdapter;
    private String SortType = "Date";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        notesListView = view.findViewById(R.id.notesList);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), NoteActivity.class);
                intent.putExtra("note", notes.get(position));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        NotesDatabaseAdapter adapter = new NotesDatabaseAdapter(getContext());
        adapter.open();

        notes = adapter.getNotes();
        SortNotes();

        noteAdapter = new NoteAdapter(getContext(), R.layout.note, notes);
        notesListView = getView().findViewById(R.id.notesList);
        notesListView.setAdapter(noteAdapter);
        adapter.close();
    }

    private void SortNotes() {
        switch (SortType) {
            case "Date":
                notes.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                break;
            case "Title":
                notes.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
                break;
        }
    }

    @Override
    public void onActivitySortListener (String sortType) {
        SortType = sortType;
        SortNotes();
        noteAdapter.notifyDataSetChanged();
    }

}
