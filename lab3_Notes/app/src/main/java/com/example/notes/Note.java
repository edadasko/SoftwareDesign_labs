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

    Note(String title, String body, String tags) {
        this.title = (title.equals("")) ?
                (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date()) : title;

        this.body = body;
        this.tags = Arrays.asList(tags.split(" "));
    }

    String getTitle() {
        return title;
    }

    String getBody() {
        return body;
    }

    List<String> getTags() {
        return tags;
    }

    String getStringOfTags() {
        return String.join(", ", tags);
    }

    boolean hasTag(String tag) {
        return tags.contains(tag);
    }


}
