package com.example.notes;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class Note implements Serializable {

    private long id;
    private String title;
    private String body;
    private List<String> tags;

    Note(long id, String title, String body, String tags) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.tags = Arrays.asList(tags.split(" "));
    }

    Note() {
        this.id = 0;
        this.title = "";
        this.body = "";
        this.tags = null;
    }

    long getId() {
        return id;
    }

    String getTitle() {
        if (title.equals(""))
            return (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(new Date());
        return title;
    }

    String getBody() {
        return body;
    }

    List<String> getTags() {
        return tags;
    }

    String getStringOfTags() {
        if (tags == null)
            return "";
        return String.join(" ", tags);
    }

    boolean hasTitle() {
        return !title.equals("");
    }

    boolean hasTag(String tag) {
        return tags.contains(tag);
    }
}
