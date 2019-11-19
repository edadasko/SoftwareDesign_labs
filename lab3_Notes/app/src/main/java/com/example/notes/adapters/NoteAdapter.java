package com.example.notes.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.notes.R;
import com.example.notes.models.Note;

import java.util.List;

class NoteViewHolderItem {
    TextView titleView;
    TextView tagsView;
    TextView bodyView;
}

public class NoteAdapter extends ArrayAdapter<Note> {
    private LayoutInflater inflater;
    private int layoutResourceId;
    private List<Note> notes;

    public NoteAdapter(Context context, int resource, List<Note> notes) {
        super(context, resource, notes);
        this.notes = notes;
        this.layoutResourceId = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        NoteViewHolderItem noteViewHolderItem;

        if (convertView == null){

            convertView = inflater.inflate(layoutResourceId, parent, false);

            noteViewHolderItem = new NoteViewHolderItem();
            noteViewHolderItem.titleView = convertView.findViewById(R.id.titleView);
            noteViewHolderItem.tagsView = convertView.findViewById(R.id.tagsView);
            noteViewHolderItem.bodyView = convertView.findViewById(R.id.bodyView);

            convertView.setTag(noteViewHolderItem);

        } else {
            noteViewHolderItem = (NoteViewHolderItem) convertView.getTag();
        }

        Note note = notes.get(position);

        noteViewHolderItem.titleView.setText(note.getTitle());
        noteViewHolderItem.tagsView.setText(note.getStringOfTags());
        noteViewHolderItem.bodyView.setText(note.getBody());

        return convertView;
    }
}
