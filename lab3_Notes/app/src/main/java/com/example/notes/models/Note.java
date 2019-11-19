package com.example.notes.models;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Note implements Serializable {

    private long id;
    private String title;
    private String body;
    private Date creatingDate;
    private List<String> tags;

    public Note(long id, String title, String body, String tags) {
        this.id = id;
        this.title = title;
        this.body = body;
        tags = tags.toLowerCase();
        this.tags = Arrays.asList(tags.split(" "));
        this.creatingDate = new Date();
    }

    public Note(long id, String title, String body, String tags, String date) {
        this(id, title, body, tags);
        try {
            this.creatingDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(date);
        }
        catch (ParseException exp) {
            this.creatingDate = new Date();
        }
    }

    public Note() {
        this.id = 0;
        this.title = "";
        this.body = "";
        this.tags = null;
        this.creatingDate = new Date();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        if (title.equals(""))
            return getStringOfDate();
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return creatingDate;
    }

    public String getStringOfTags() {
        if (tags == null)
            return "";
        return String.join(" ", tags);
    }

    public boolean hasTitle() {
        return !title.equals("");
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag.toLowerCase());
    }

    private String getStringOfDate() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(creatingDate);
    }
}
