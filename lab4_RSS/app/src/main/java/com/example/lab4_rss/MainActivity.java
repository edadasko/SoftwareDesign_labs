package com.example.lab4_rss;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Post> postList = new ArrayList<>();
    private ListView listView;
    private PostAdapter postAdapter;
    private static String RSS = "";
    private boolean isMainLoad = false;

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_RSS = "rss";
    private SharedPreferences mSettings;

    private BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        postList = new ArrayList<>();

        listView = this.findViewById(R.id.postListView);

        listView.setOnItemClickListener(onItemClickListener);

        networkReceiver = new NetworkChangeReceiver();
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (!mSettings.contains(APP_PREFERENCES_RSS)) {
            showRssRequest();
        }
        else
        {
            RSS = mSettings.getString(APP_PREFERENCES_RSS, "");
            getRssData();
            updateAdapter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!RSS.equals("")) {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_RSS, RSS);
            editor.apply();
        }
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
            isMainLoad = true;
            new RssDataController().execute(RSS);
        }else{
            Toast.makeText(this, "You don't have internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateAdapter(){
        postAdapter = new PostAdapter(this, R.layout.post, postList);
        listView = this.findViewById(R.id.postListView);
        listView.setAdapter(postAdapter);
    }

    private class RssDataController extends AsyncTask<String, Integer, ArrayList<Post>>{
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute(){
            if(isMainLoad){
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Loading news...");
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected ArrayList<Post> doInBackground(String... params) {
            return ProcessXml(Getdata(params[0]));
        }

        @Override
        protected void onPostExecute(ArrayList<Post> result) {
            postList.clear();
            if (result.size() == 0) {
                Toast.makeText(getApplicationContext(),
                        "The RSS link in wrong.", Toast.LENGTH_LONG).show();
            }
            else {
                postList.addAll(result);
            }
            postAdapter.notifyDataSetChanged();

            if(isMainLoad){
                dialog.dismiss();
                isMainLoad = false;
            }
        }

        private Document Getdata(String address) {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document xmlDoc = builder.parse(inputStream);
                return xmlDoc;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private ArrayList<Post> ProcessXml(Document data) {
            ArrayList<Post> posts = new ArrayList<>();
            if (data != null) {
                Element root = data.getDocumentElement();
                Node channel = root.getChildNodes().item(1);
                if (channel == null)
                    channel = root.getChildNodes().item(0);
                NodeList items = channel.getChildNodes();
                for (int i = 0; i < items.getLength(); i++) {
                    Node currentchild = items.item(i);
                    if (currentchild.getNodeName().equalsIgnoreCase("item")) {
                        Post item = new Post();
                        NodeList itemchilds = currentchild.getChildNodes();
                        for (int j = 0; j < itemchilds.getLength(); j++) {
                            Node current = itemchilds.item(j);
                            if (current.getNodeName().equalsIgnoreCase("title")) {
                                item.Title = current.getTextContent();
                            } else if (current.getNodeName().equalsIgnoreCase("description")) {
                                item.Content = current.getTextContent();
                            } else if (current.getNodeName().equalsIgnoreCase("pubDate")) {
                                item.Date = current.getTextContent();
                            } else if (current.getNodeName().equalsIgnoreCase("link")) {
                                item.Link = current.getTextContent();
                            } else if (current.getNodeName().equalsIgnoreCase("media:thumbnail") ||
                                    current.getNodeName().equalsIgnoreCase("enclosure")) {
                                String url = current.getAttributes().item(0).getTextContent();
                                item.Image = url;
                            }
                        }
                        posts.add(item);
                    }
                }
            }
            return posts;
        }
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
                                updateAdapter();
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

}

