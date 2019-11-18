package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int SCHEMA = 1;
    static final String TABLE = "notes";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_TAGS = "tags";

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TITLE
                + " TEXT, " + COLUMN_BODY
                + " TEXT, " + COLUMN_TAGS + " TEXT);");


        insertNote(db, "note 1", "afwefewf", "a b c d");
        init(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    private void insertNote(SQLiteDatabase db, String title,
                            String body, String tags) {
        ContentValues notes = new ContentValues();
        notes.put("title", title);
        notes.put("body", body);
        notes.put("tags", tags);
        db.insert(TABLE, null, notes);
    }

    private void init(SQLiteDatabase db) {
        insertNote(db, "note 1", "afwefewf", "a b c d");
        insertNote(db, "note 2", "fewfwewekwfeo", "c d");
        insertNote(db, "", "mcvmdsvsdml", "a b");
        insertNote(db, "note 3", "afwefewf", "d");
    }
}
