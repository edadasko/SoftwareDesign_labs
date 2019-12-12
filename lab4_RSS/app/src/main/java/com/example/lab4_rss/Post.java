package com.example.lab4_rss;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {
    public String Title;
    public String Content;
    public String Date;
    public String Image;
    public String Link;

    public String cachedBitmap;
    public Bitmap bitmapImage;

    public Post() {
        super();
    }

    private Post(Parcel parcel) {
        Title = parcel.readString();
        Content = parcel.readString();
        Date = parcel.readString();
        Image = parcel.readString();
        Link = parcel.readString();
    }

    public Post(String _title, String _pubDate) {
        this.Title = _title;
        this.Date = _pubDate;
    }

    public Post(Post other) {
        Title = other.Title;
        Content = other.Content;
        Date = other.Date;
        Image = other.Image;
        Link = other.Link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int arg1) {
        parcel.writeString(Title);
        parcel.writeString(Content);
        parcel.writeString(Date);
        parcel.writeString(Image);
        parcel.writeString(Link);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {

        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public String toString() {
        return this.Content + "<br>"
                + "<a href='" + this.Link + "'>Go to website</a>";
    }
}