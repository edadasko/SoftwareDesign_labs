package com.example.lab4_rss;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class PostAdapter extends ArrayAdapter<Post> {
    private Activity myContext;
    private ArrayList<Post> posts;

    public PostAdapter(Context context, int textViewResourceId,
                       ArrayList<Post> objects, boolean isOnline) {
        super(context, textViewResourceId, objects);
        myContext = (Activity) context;
        posts = objects;
    }

    static class ViewHolder {
        TextView postTitleView;
        TextView postDateView;
        ImageView postThumbView;
        String postThumbViewURL;
        Bitmap bitmapImage;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = myContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.post, null);

            viewHolder = new ViewHolder();
            viewHolder.postThumbView = convertView
                    .findViewById(R.id.postThumb);
            viewHolder.postTitleView = convertView
                    .findViewById(R.id.postTitleLabel);
            viewHolder.postDateView =  convertView
                    .findViewById(R.id.postDateLabel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Post post = posts.get(position);

        viewHolder.postThumbView.setImageResource(R.drawable.ic_photo_black_24dp);

        if (post.bitmapImage != null) {
            viewHolder.postThumbView.setImageBitmap(post.bitmapImage);
        }
        else {
            viewHolder.postThumbViewURL = post.Image;
            new DownloadImageTask().execute(viewHolder);
        }
        viewHolder.postTitleView.setText(post.Title);
        viewHolder.postDateView.setText(post.Date);

        return convertView;
    }

    private class DownloadImageTask extends AsyncTask<ViewHolder, Void, ViewHolder> {
        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            ViewHolder viewHolder = params[0];
            try {
                URL imageURL = new URL(viewHolder.postThumbViewURL);
                viewHolder.bitmapImage = BitmapFactory.decodeStream(imageURL.openStream());
            } catch (IOException e) {
                Log.e("error", "Downloading Image Failed");
                viewHolder.bitmapImage = null;
            }

            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder result) {
            if (result.bitmapImage != null) {
                result.postThumbView.setImageBitmap(result.bitmapImage);
            }
        }
    }
}