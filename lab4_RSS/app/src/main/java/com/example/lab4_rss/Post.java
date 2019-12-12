package com.example.lab4_rss;

public class Post {
    String Title;
    String Content;
    String Date;
    String Image;
    String Link;

    String cachedBitmap;

    Post() {

    }

    Post(Post other) {
        Title = other.Title;
        Content = other.Content;
        Date = other.Date;
        Image = other.Image;
        Link = other.Link;
        cachedBitmap = other.cachedBitmap;
    }
}