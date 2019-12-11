package com.example.lab4_rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RssDataController extends AsyncTask<String, Integer, ArrayList<Post>> {

    private int cashedNum = 10;
    private ProgressDialog dialog;
    private Context context;
    ArrayList<Post> postList;
    ArrayList<Post> cashedPosts;
    PostAdapter adapter;


    public RssDataController(Context context, ArrayList<Post> postList, PostAdapter adapter, ProgressDialog dialog, ArrayList<Post> cashedPosts) {
        this.context = context;
        this.postList = postList;
        this.adapter = adapter;
        this.dialog = dialog;
        this.cashedPosts = cashedPosts;
    }
    @Override
    protected void onPreExecute(){
        dialog.setMessage("Loading news...");
        dialog.setCancelable(false);
        dialog.show();
        }

    @Override
    protected ArrayList<Post> doInBackground(String... params) {
        return ProcessXml(GetData(params[0]));
    }

    @Override
    protected void onPostExecute(ArrayList<Post> result) {
        postList.clear();
        if (result.size() == 0) {
            Toast.makeText(context,
                    "The RSS link in wrong.", Toast.LENGTH_LONG).show();
        }
        else {
            postList.addAll(result);
            cashedPosts.clear();
            cashedPosts.addAll(result.subList(0, cashedNum));
        }
        adapter.notifyDataSetChanged();

        dialog.dismiss();
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
}