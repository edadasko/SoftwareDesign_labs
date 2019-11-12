package com.example.notes;

import android.icu.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class Note {
    private String title;
    private String body;
    private List<String> tags;

    public Note(String title, String body, String tags) {
        this.title = (title.equals("")) ?
                (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date()) : title;

        this.body = body;
        this.tags = Arrays.asList(tags.split(" "));
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getStringOfTags() {
        return String.join(", ", tags);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }


}
