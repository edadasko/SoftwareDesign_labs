package com.example.lab4_rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class RssDataController extends AsyncTask<String, Integer, ArrayList<Post>> {

    private String APP_PREFERENCES_CACHE = "cache";

    private int cashedNum = 10;
    final private ProgressDialog dialog;
    private Context context;
    private ArrayList<Post> postList;
    private PostAdapter adapter;
    private SharedPreferences mSettings;
    private ArrayList<WebView> webViews;
    private boolean online;
    private String filesDir;


    public RssDataController(
            final Context context,
            ArrayList<Post> postList,
            PostAdapter adapter,
            final ProgressDialog dialog,
            SharedPreferences mSettings,
            boolean online) {
        this.context = context;
        this.postList = postList;
        this.adapter = adapter;
        this.dialog = dialog;
        this.mSettings = mSettings;
        this.online = online;
        filesDir = context.getFilesDir().getAbsolutePath();
        webViews = new ArrayList<>();

        for (int i = 0; i < cashedNum; i++) {
            webViews.add(new WebView(context));
            webViews.get(i).setWebViewClient(new WebViewClient(){

                @Override
                public void onPageFinished(WebView view, String url) {
                    int index = webViews.indexOf(view);
                    view.saveWebArchive(filesDir + File.separator + webViews.indexOf(view) + ".mht");
                    if (index == cashedNum - 1)
                        Toast.makeText(context,  "Top news have been cached.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onPreExecute(){
        dialog.setMessage("Loading news...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<Post> result) {
        postList.clear();
        if (result.size() == 0) {
            if (online) {
                Toast.makeText(context,
                        "The RSS link in wrong.", Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        }
        else {
            postList.addAll(result);
            dialog.dismiss();
            if (online) {
                new CachePosts().execute(postList);
            }

        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected ArrayList<Post> doInBackground(String... params) {
        if (online)
            return ProcessXml(GetData(params[0]));
        else
            return GetCache();
    }

    private Document GetData(String address) {
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
                            item.Image = current.getAttributes().item(0).getTextContent();
                        }
                    }
                    posts.add(item);
                }
            }
        }
        return posts;
    }

    private ArrayList<Post> GetCache() {
        ArrayList<Post> cashedPosts = new ArrayList<>();
        Type type = new TypeToken<ArrayList<Post>>(){}.getType();

        String cashedPostsString = mSettings.getString(APP_PREFERENCES_CACHE, "");

        Gson gson = new Gson();
        if (!cashedPostsString.isEmpty()) {
            cashedPosts = gson.fromJson(cashedPostsString, type);
        }

        return cashedPosts;
    }

    private class CachePosts extends AsyncTask<ArrayList<Post>, Void, ArrayList<Post>> {

        @Override
        protected void onPreExecute(){
            dialog.setMessage("Caching photos...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected ArrayList<Post> doInBackground(ArrayList<Post>... params) {
            ArrayList<Post> posts = params[0];
            ArrayList<Post> cachedPosts = new ArrayList<>();
            for (int i = 0; i < cashedNum; i++) {
                cachedPosts.add(new Post(posts.get(i)));
            }
            for (int i = 0; i < cashedNum; i++) {
                try {
                    if (cachedPosts.get(i).Image != null) {
                        URL imageURL = new URL(cachedPosts.get(i).Image);
                        Bitmap image = BitmapFactory.decodeStream(imageURL.openStream());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        cachedPosts.get(i).cachedBitmap = encodedImage;
                    }
                    else
                        cachedPosts.get(i).cachedBitmap = null;
                } catch (IOException e) {
                    cachedPosts.get(i).cachedBitmap = null;
                }
            }
            return cachedPosts;
        }

        @Override
        protected void onPostExecute(ArrayList<Post> result) {
            SharedPreferences.Editor editor = mSettings.edit();
            Gson gson = new Gson();
            String jsonCache = gson.toJson(result);
            editor.putString(APP_PREFERENCES_CACHE, jsonCache);
            editor.apply();

            for (int i = 0; i < cashedNum; i++) {
                webViews.get(i).loadUrl(result.get(i).Link);
            }

            dialog.dismiss();
        }
    }
}