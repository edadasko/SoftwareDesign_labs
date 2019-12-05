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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Post> postList;
    private ListView listView;
    private PostAdapter postAdapter;
    private static String RSS = "";
    private boolean isMainLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RSS = this.getString(R.string.rss);
        postList = new ArrayList<>();

        listView = this.findViewById(R.id.postListView);

        listView.setOnItemClickListener(onItemClickListener);

        getRssData();

        updateAdapter();
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
        if(haveInternet()){
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
            postList.addAll(result);
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
            ArrayList<Post> posts = null;
            if (data != null) {
                posts = new ArrayList<>();
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

    private boolean haveInternet(){
        ConnectivityManager connManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = null;

        if(connManager != null){
            network = connManager.getActiveNetworkInfo();
        }
        return network != null && network.isConnected();
    }
}
