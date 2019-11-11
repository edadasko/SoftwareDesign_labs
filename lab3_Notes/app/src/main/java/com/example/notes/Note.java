package com.example.notes;

import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

class Note {
    private String title;
    private String body;
    private ArrayList<String> tags;

    public Note(String title, String body, ArrayList<String> tags) {
        if (title != null)

        this.title = (title.equals("")) ?
                (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date()) : title;

        this.body = body;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }


}
