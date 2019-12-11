package com.example.lab4_rss;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Post> postList = new ArrayList<>();
    private ArrayList<Post> cashedPosts = new ArrayList<>();

    private Gson gson = new Gson();

    private ListView listView;
    private PostAdapter postAdapter;
    private static String RSS = "";

    private ProgressDialog dialog;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_RSS = "rss";
    public static final String APP_PREFERENCES_CACHE = "cache";
    private SharedPreferences mSettings;

    private BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Type type = new TypeToken<ArrayList<Post>>(){}.getType();

        String cashedPostsString = mSettings.getString(APP_PREFERENCES_CACHE, "");

        if (!cashedPostsString.isEmpty())
            cashedPosts = gson.fromJson(cashedPostsString, type);

        dialog = new ProgressDialog(this);
        updateAdapter();

        listView.setOnItemClickListener(onItemClickListener);

        networkReceiver = new NetworkChangeReceiver();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (!mSettings.contains(APP_PREFERENCES_RSS)) {
            showRssRequest();
        }
        else {
            RSS = mSettings.getString(APP_PREFERENCES_RSS, "");
            getRssData();
        }

        setOfflineMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mSettings.edit();
        if (!RSS.equals("")) {
            editor.putString(APP_PREFERENCES_RSS, RSS);
        }

        String jsonCache = gson.toJson(cashedPosts);
        editor.putString(APP_PREFERENCES_CACHE, jsonCache);
        editor.apply();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            Post post = postList.get(pos);

            Intent i = new Intent(MainActivity.this, PostActivity.class);
            i.putExtra("post", post);
            startActivity(i);
        }
    };

    private void getRssData(){
        if(NetworkUtil.getConnectivityStatus(this) != NetworkState.NOT_CONNECTED){
            new RssDataController(this, postList, postAdapter, dialog, cashedPosts).execute(RSS);
        }else{
            Toast.makeText(this, "You don't have internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateAdapter() {
        postAdapter = new PostAdapter(this, R.layout.post, postList);
        listView = this.findViewById(R.id.postListView);
        listView.setAdapter(postAdapter);
    }

    public void setOfflineMode() {
        postList.clear();
        postList.addAll(cashedPosts);
        updateAdapter();
    }

    public void setOnlineMode() {
        getRssData();
    }

    private void showRssRequest(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.rss_link, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        mDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.input_rss);

        userInput.setText(RSS);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString(APP_PREFERENCES_RSS, userInput.getText().toString());
                                editor.apply();

                                RSS = mSettings.getString(APP_PREFERENCES_RSS, "");
                                getRssData();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = mDialogBuilder.create();

        alertDialog.show();
    }

    public void settingsButtonClick(View view) {
        showRssRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if((dialog != null) && dialog.isShowing() ){
            dialog.dismiss();
        }
    }
}