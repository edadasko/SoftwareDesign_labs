package com.example.notes;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class Note implements Serializable {

    private long id;
    private String title;
    private String body;
    private Date creatingDate;
    private List<String> tags;

    Note(long id, String title, String body, String tags) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.tags = Arrays.asList(tags.split(" "));
        this.creatingDate = new Date();
    }

    Note(long id, String title, String body, String tags, String date) {
        this(id, title, body, tags);
        try {
            this.creatingDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(date);
        }
        catch (ParseException exp) {
            this.creatingDate = new Date();
        }
    }

    Note() {
        this.id = 0;
        this.title = "";
        this.body = "";
        this.tags = null;
        this.creatingDate = new Date();
    }

    long getId() {
        return id;
    }

    String getTitle() {
        if (title.equals(""))
            return getStringOfDate();
        return title;
    }

    private String getStringOfDate() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(creatingDate);
    }

    String getBody() {
        return body;
    }

    Date getDate() {
        return creatingDate;
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
