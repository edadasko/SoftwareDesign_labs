package com.example.lab4_rss;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private String[] listData = new String[]{"Post 1", "Post 2", "Post 3", "Post 4", "Post 5", "Post 6"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = this.findViewById(R.id.postListView);
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.post, listData);
        listView.setAdapter(itemAdapter);
    }

}
