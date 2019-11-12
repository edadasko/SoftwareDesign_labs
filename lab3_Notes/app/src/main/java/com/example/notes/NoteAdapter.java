package com.example.notes;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.List;

class NoteAdapter extends ArrayAdapter<Note> {
    private LayoutInflater inflater;
    private int layout;
    private List<Note> notes;

    NoteAdapter(Context context, int resource, List<Note> notes) {
        super(context, resource, notes);
        this.notes = notes;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        TextView titleView = view.findViewById(R.id.titleView);
        TextView tagsView = view.findViewById(R.id.tagsView);
        TextView bodyView = view.findViewById(R.id.bodyView);

        Note note = notes.get(position);

        titleView.setText(note.getTitle());
        tagsView.setText(note.getStringOfTags());
        bodyView.setText(note.getBody());

        return view;
    }
}
