package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
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
                NotesDatabaseHelper.COLUMN_TAGS};
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
                notes.add(new Note(id, title, body, tags));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, NotesDatabaseHelper.TABLE);
    }

    public Note getNote(long id) {
        Note note = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",
                NotesDatabaseHelper.TABLE, NotesDatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String title = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_TITLE));
            String body = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_BODY));
            String tags = cursor.getString(cursor.getColumnIndex(NotesDatabaseHelper.COLUMN_TAGS));
            note = new Note(id, title, body, tags);
        }
        cursor.close();
        return note;
    }

    public long insert(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(NotesDatabaseHelper.COLUMN_TITLE, note.hasTitle() ? note.getTitle() : "");
        cv.put(NotesDatabaseHelper.COLUMN_BODY, note.getBody());
        cv.put(NotesDatabaseHelper.COLUMN_TAGS, note.getStringOfTags());
        return  database.insert(NotesDatabaseHelper.TABLE, null, cv);
    }

    public long delete(long id) {
        return database.delete(NotesDatabaseHelper.TABLE,
                NotesDatabaseHelper.COLUMN_ID + "=" + id, null);
    }

    public long update(Note note) {
        ContentValues cv = new ContentValues();
        cv.put(NotesDatabaseHelper.COLUMN_TITLE, note.hasTitle() ? note.getTitle() : "");
        cv.put(NotesDatabaseHelper.COLUMN_BODY, note.getBody());
        cv.put(NotesDatabaseHelper.COLUMN_TAGS, note.getStringOfTags());
        return database.update(NotesDatabaseHelper.TABLE, cv,
                NotesDatabaseHelper.COLUMN_ID + "=" + note.getId(), null);
    }
}
