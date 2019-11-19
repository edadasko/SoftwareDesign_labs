package com.example.notes.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import com.example.notes.helpers.NotesDatabaseHelper;
import com.example.notes.models.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotesDatabaseAdapter {

    private NotesDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public NotesDatabaseAdapter(Context context){
        dbHelper = new NotesDatabaseHelper(context.getApplicationContext());
    }

    public NotesDatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private Cursor getAllEntries() {
        String[] columns = new String[] {NotesDatabaseHelper.COLUMN_ID,
                NotesDatabaseHelper.COLUMN_TITLE, NotesDatabaseHelper.COLUMN_BODY,
                NotesDatabaseHelper.COLUMN_TAGS, NotesDatabaseHelper.COLUMN_DATE};
        return  database.query(NotesDatabaseHelper.TABLE, columns,
                null, null, null, null, null);
    }

    public List<Note> getNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_TITLE));
                String body = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_BODY));
                String tags = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_TAGS));
                String date = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_DATE));
                notes.add(new Note(id, title, body, tags, date));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public long insert(Note note) {
        ContentValues cv = getContentValues(note);
        return  database.insert(NotesDatabaseHelper.TABLE, null, cv);
    }

    public long delete(long id) {
        return database.delete(NotesDatabaseHelper.TABLE,
                NotesDatabaseHelper.COLUMN_ID + "=" + id, null);
    }

    public long update(Note note) {
        ContentValues cv = getContentValues(note);
        return database.update(NotesDatabaseHelper.TABLE, cv,
                NotesDatabaseHelper.COLUMN_ID + "=" + note.getId(), null);
    }

    private ContentValues getContentValues(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(NotesDatabaseHelper.COLUMN_TITLE, note.hasTitle() ? note.getTitle() : "");
        cv.put(NotesDatabaseHelper.COLUMN_BODY, note.getBody());
        cv.put(NotesDatabaseHelper.COLUMN_TAGS, note.getStringOfTags());
        cv.put(NotesDatabaseHelper.COLUMN_DATE,
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
        return cv;
    }
}
