package com.example.zhihudaily;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private int Flag = 0;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private RefreshLayout refreshLayout;
    private String url_1;
    private String url_2;
    private int t;
    private int time;
    private String m;
    private String HH;
    private String Today;
    private String MM;
    private ImageView user;
    private String dd;
    private static List<String> sPics = new ArrayList<>();
    private String Yesterday;
    private String Yesterday_1;
    private int RefreshFlag = 0;
    private Handler handler;
    private Intent go;
    private SharedPreferencesUtil check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        Bitmap bitmap =
//        imageView.setImageBitmap();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorMain));

        check = SharedPreferencesUtil.getInstance(getApplicationContext());
        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.refreshLayout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);  //避免滑动卡顿
//        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
//        recyclerView.setAdapter(new MainAdapter(MainActivity.this, list));
        String account_id = check.getAccountId();

        t = 0;
        url_1 = "https://news-at.zhihu.com/api/3/news/before/";
        Today = getOldDate(t, 1);
        MM = getOldDate(t, 4);
        HH = getOldDate(t, 5);
        time = Integer.valueOf(HH);
        switch (MM) {
            case "01":
                m = "一月";
                break;
            case "02":
                m = "二月";
                break;
            case "03":
                m = "三月";
                break;
            case "04":
                m = "四月";
                break;
            case "05":
                m = "五月";
                break;
            case "06":
                m = "六月";
                break;
            case "07":
                m = "七月";
                break;
            case "08":
                m = "八月";
                break;
            case "09":
                m = "九月";
                break;
            case "10":
                m = "十月";
                break;
            case "11":
                m = "十一月";
                break;
            case "12":
                m = "十二月";
                break;
        }
        user = findViewById(R.id.imageView);

        dd = getOldDate(t, 3);
        TextView tv = (TextView) findViewById(R.id.hello);
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        TextView tv1 = (TextView) findViewById(R.id.ddd);
        tv1.setText(dd);
        TextPaint tp1 = tv1.getPaint();
        tp1.setFakeBoldText(true);
        TextView tv2 = (TextView) findViewById(R.id.month);
        tv2.setText(m);
        if (time >= 0 && time < 6) {
            tv.setText("早点休息( ˙-˙ )");
        } else if (time >= 6 && time < 9) {
            tv.setText("早上好(◦˙▽˙◦)");
        } else if (time >= 12 && time < 15) {
            tv.setText("中午好~");
        } else if (time >= 19 && time <= 24) {
            tv.setText("晚上好(￣▽￣)");
        }

        wzyAwesome();
        Start();
    }


    protected void Start() {
        list.clear();
        t = 0;

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check.isLogin()) {
                    Intent intent = new Intent(MainActivity.this,MyselfActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {

                    URL url = new URL("https://news-at.zhihu.com/api/4/stories/latest");
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

                    showResponse(response.toString());
//                    showResponse("{\"date\":\"20201117\",\"stories\":[{\"image_hue\":\"0x99828f\",\"title\":\"金发碧眼为什么很少在白人以外的种族出现？\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9730032\",\"hint\":\"biokiwi · 2 分钟阅读\",\"ga_prefix\":\"111707\",\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-a501fdbea6733624292b2c53d0391207.jpg?source=8673f162\"],\"type\":0,\"id\":9730032},{\"image_hue\":\"0x81878a\",\"title\":\"为什么很多功能饮料，都有一股子相同的廉价香精味？\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9730042\",\"hint\":\"伦西 · 1 分钟阅读\",\"ga_prefix\":\"111707\",\"images\":[\"https:\\/\\/pic4.zhimg.com\\/v2-be72469ee68d4f14a4ca200428b0192d.jpg?source=8673f162\"],\"type\":0,\"id\":9730042},{\"image_hue\":\"0x452f18\",\"title\":\"为什么人们不像制造化学试剂一样制造酒，而要去酿造呢？\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9730052\",\"hint\":\"知乎用户 · 3 分钟阅读\",\"ga_prefix\":\"111707\",\"images\":[\"https:\\/\\/pic2.zhimg.com\\/v2-7dc612c22af18f22ca414c0d39635349.jpg?source=8673f162\"],\"type\":0,\"id\":9730052},{\"image_hue\":\"0x999191\",\"title\":\"建筑系的学生都是怎么制作建筑模型的？\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9730038\",\"hint\":\"知乎用户 · 2 分钟阅读\",\"ga_prefix\":\"111707\",\"images\":[\"https:\\/\\/pic2.zhimg.com\\/v2-622b37b0d9e36ba9afa846b3c9f236aa.jpg?source=8673f162\"],\"type\":0,\"id\":9730038},{\"image_hue\":\"0x4f5e5b\",\"title\":\"传说中的全球史前大洪水真的存在过吗？\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9730041\",\"hint\":\"知乎用户 · 4 分钟阅读\",\"ga_prefix\":\"111707\",\"images\":[\"https:\\/\\/pic1.zhimg.com\\/v2-599339f3c9f91ef02a89174215662ef4.jpg?source=8673f162\"],\"type\":0,\"id\":9730041},{\"image_hue\":\"0x99938d\",\"title\":\"瞎扯 · 如何正确地吐槽\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9730051\",\"hint\":\"VOL.2529\",\"ga_prefix\":\"111706\",\"images\":[\"https:\\/\\/pic4.zhimg.com\\/v2-7fc7983dd22e88f4d79f96111a5fe318.jpg?source=8673f162\"],\"type\":0,\"id\":9730051}],\"top_stories\":[{\"image_hue\":\"0x2b3025\",\"hint\":\"作者 \\/ 简单心理\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9729944\",\"image\":\"https:\\/\\/pic4.zhimg.com\\/v2-eb69a0ae9ee0c6c6726832b01d54320b.jpg?source=8673f162\",\"title\":\"为什么有些气特别难消，过很久还是越想越气？\",\"ga_prefix\":\"111607\",\"type\":0,\"id\":9729944},{\"image_hue\":\"0x543c3e\",\"hint\":\"作者 \\/ 一颗青木\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9729963\",\"image\":\"https:\\/\\/pic1.zhimg.com\\/v2-89b066592ab54d4789ee37cdcd12db01.jpg?source=8673f162\",\"title\":\"古代屠城是每个人都要找出来处死吗？\",\"ga_prefix\":\"111507\",\"type\":0,\"id\":9729963},{\"image_hue\":\"0x8b5a3a\",\"hint\":\"作者 \\/ 波士顿科学\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9729918\",\"image\":\"https:\\/\\/pic4.zhimg.com\\/v2-5c0b3bba4ac49dd482554cb308486686.jpg?source=8673f162\",\"title\":\"人猝死前会有特殊感觉吗？\",\"ga_prefix\":\"111407\",\"type\":0,\"id\":9729918},{\"image_hue\":\"0x0c0a0d\",\"hint\":\"作者 \\/ 白岛岩心\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9729847\",\"image\":\"https:\\/\\/pic3.zhimg.com\\/v2-2e3d8f6ae57058aabb0c4d24ed2f5276.jpg?source=8673f162\",\"title\":\"晚上睡不着怎么办？\",\"ga_prefix\":\"111207\",\"type\":0,\"id\":9729847},{\"image_hue\":\"0x909599\",\"hint\":\"作者 \\/ 知乎用户\",\"url\":\"https:\\/\\/daily.zhihu.com\\/story\\/9729832\",\"image\":\"https:\\/\\/pic4.zhimg.com\\/v2-b510a0e7178196d132f62ff469b6d0f9.jpg?source=8673f162\",\"title\":\"为什么难过的时候心真的会痛？\",\"ga_prefix\":\"111107\",\"type\":0,\"id\":9729832}]}");
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(MainActivity.this, "网络罢工了呢(ó﹏ò｡)", Toast.LENGTH_LONG);
                            toast.show();
                            Log.i("TAG","TAG");
                        }
                    });
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

    public void showResponse(final String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String title = jsonObject1.getString("title");
                String hint = jsonObject1.getString("hint");
                JSONArray images = jsonObject1.getJSONArray("images");
                String image = images.get(0).toString();
                String url0 = jsonObject1.getString("url");
                String id = jsonObject1.getString("id");

                Map map = new HashMap();

                map.put("title", title);
                map.put("hint", hint);
                map.put("image", image);
                map.put("url", url0);
                map.put("id", id);
                map.put("type", 1);

                list.add(map);

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(new MainAdapter(MainActivity.this, list));
                }
            });
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    public void showResponse2(final String string) {
        Yesterday_1 = getOldDate(t, 2);
        Map map1 = new HashMap();
        map1.put("type", 2);
        map1.put("date", Yesterday_1);
        list.add(map1);
        try {

            JSONObject jsonObject2 = new JSONObject(string);

            JSONArray jsonArray = jsonObject2.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String title = jsonObject1.getString("title");
                String hint = jsonObject1.getString("hint");
                JSONArray images = jsonObject1.getJSONArray("images");
                String image = images.get(0).toString();
                String url0 = jsonObject1.getString("url");
                String id = jsonObject1.getString("id");

                Map map = new HashMap();

                map.put("title", title);
                map.put("hint", hint);
                map.put("image", image);
                map.put("url", url0);
                map.put("id", id);
                map.put("type", 1);
                list.add(map);


            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(new MainAdapter(MainActivity.this, list));

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void wzyAwesome() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                Start();
                refreshlayout.finishRefresh(200/*,false*/);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection = null;
                        BufferedReader reader = null;
                        try {
                            t = t - 1;
                            Yesterday = getOldDate(t, 1);
                            url_2 = url_1 + Yesterday;
                            URL url = new URL(url_2);

//                            URL url = new URL("https://news-at.zhihu.com/api/3/stories/latest");

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

                            showResponse2(response.toString());
                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(MainActivity.this, "网络罢工了呢(ó﹏ò｡)", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
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
                refreshlayout.finishLoadMore(200/*,false*/);//传入false表示加载失败
            }
        });

    }


    public static String getOldDate(int distanceDay, int n) {
        SimpleDateFormat dft = new SimpleDateFormat();
        if (n == 1) {
            dft = new SimpleDateFormat("yyyyMMdd");
        } else if (n == 2) {
            dft = new SimpleDateFormat("MM月dd日");
        } else if (n == 3) {
            dft = new SimpleDateFormat("dd");
        } else if (n == 4) {
            dft = dft = new SimpleDateFormat("MM");
        } else if (n == 5) {
            dft = new SimpleDateFormat("HH");
        }
        dft.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dft.format(endDate);
    }

    public Bitmap stringToBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}




