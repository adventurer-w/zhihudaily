package com.example.zhihudaily;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Intent intent_back;
    private ImageView back;
    private List<Map<String, Object>> list = new ArrayList<>();
    private String id;
    private String long_comments;
    private String short_comments;
    private String url_long;
    private String url_short;
    private TextView num;
    static int here = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comments);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorMain));
        num = findViewById(R.id.textView7);
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        recyclerView.setAdapter(new CommentsAdapter(CommentsActivity.this, list));

        intent_back = new Intent(CommentsActivity.this, NewsActivity.class);
        back = findViewById(R.id.imageView3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        short_comments = intent.getStringExtra("short_comments");
        long_comments = intent.getStringExtra("long_comments");
        url_long = "https://news-at.zhihu.com/api/4/story/" + id + "/long-comments";
        url_short = "https://news-at.zhihu.com/api/4/story/" + id + "/short-comments";

        num.setText(Integer.toString(Integer.valueOf(short_comments) + Integer.valueOf(long_comments)) + " 条评论");

        if ((Integer.valueOf(long_comments)) != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {

                        URL url = new URL(url_long);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                here=1;
                            }
                        });
                        showResponse(response.toString(), 1, long_comments);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }).start();
        } else { here = 1 ;}

        if ((Integer.valueOf(short_comments)) != 0&&here!=0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {

                        URL url = new URL(url_short);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);

                        }
                        showResponse(response.toString(), 2, short_comments);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            }).start();
        }

    }


    public void showResponse(final String string, final int flag, String num) {
        Map map1 = new HashMap();
        map1.put("type", flag);
        map1.put("num", num);
        list.add(map1);


        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("comments");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String author = jsonObject1.getString("author");
                String content = jsonObject1.getString("content");
                String avatar = jsonObject1.getString("avatar");
                String time = jsonObject1.getString("time");

                Map map = new HashMap();

                map.put("author", author);
                map.put("content", content);
                map.put("image", avatar);
                map.put("time", time);
                map.put("id", id);
                map.put("type", 0);

                list.add(map);


            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
                    recyclerView.setAdapter(new CommentsAdapter(CommentsActivity.this, list));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
