package com.example.notes.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.notes.R;
import com.example.notes.activities.MainActivity;
import com.example.notes.activities.NoteActivity;
import com.example.notes.adapters.NoteAdapter;
import com.example.notes.adapters.NotesDatabaseAdapter;
import com.example.notes.models.Note;
import com.example.notes.SortTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NoteListFragment extends Fragment
        implements MainActivity.OnActivitySortListener, MainActivity.OnActivityFindListener {

    private List<Note> notes = new ArrayList<>();
    private AdapterView notesListView;
    private NoteAdapter noteAdapter;
    private SortTypes sortType = SortTypes.Date;
    private String tagForSearching = "";

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
        NotesDatabaseAdapter dbAdapter = new NotesDatabaseAdapter(getContext());
        dbAdapter.open();

        notes = dbAdapter.getNotes();
        sortNotes();
        if(!tagForSearching.equals(""))
            filterNotesByTag();

        noteAdapter = new NoteAdapter(getContext(), R.layout.note, notes);
        notesListView = getView().findViewById(R.id.notesList);
        notesListView.setAdapter(noteAdapter);
        dbAdapter.close();
    }

    @Override
    public void onActivitySortListener (SortTypes sortType) {
        this.sortType = sortType;
        sortNotes();
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityFindListener (String tag) {
        tagForSearching = tag;
        NotesDatabaseAdapter dbAdapter = new NotesDatabaseAdapter(getContext());
        dbAdapter.open();
        notes.clear();
        notes.addAll(dbAdapter.getNotes());
        filterNotesByTag();
        dbAdapter.close();
        if(noteAdapter != null)
            noteAdapter.notifyDataSetChanged();
    }

    private void sortNotes() {
        switch (sortType) {
            case Date:
                notes.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                });
                break;
            case Title:
                notes.sort(new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
                    }
                });
                break;
        }
    }

    private void filterNotesByTag() {
        if (tagForSearching.equals("")) {
            sortNotes();
            return;
        }
        List<Note> filteredNotes = new ArrayList<>();
        for (Note note: notes) {
            if (note.hasTag(tagForSearching.toLowerCase()))
                filteredNotes.add(note);
        }
        sortNotes();
        notes.clear();
        notes.addAll(filteredNotes);
    }
}