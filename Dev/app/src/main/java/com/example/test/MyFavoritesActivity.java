package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyFavoritesActivity extends AppCompatActivity {
    private RecyclerView favoriteRecyclerView;
    private MyFavoriteAdapter novelAdapter;
    private List<Novel> novelList;
    private String uid = GlobalVariable.uid;
    private ImageView backArrowIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        favoriteRecyclerView = findViewById(R.id.novel_list_favorite);
        // 初始化RecyclerView
        novelList = new ArrayList<>();
        novelAdapter = new MyFavoriteAdapter(this, novelList);
        favoriteRecyclerView.setAdapter(novelAdapter);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        backArrowIv = findViewById(R.id.iv_backward);
        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        NetUnit.getFavorite(this, uid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 请求成功的处理
                try {
                    novelList.clear();

                    // 将响应数据转换成 Book 对象列表
                    JSONObject jsonObject = new JSONObject(response);
                    String dataStr = jsonObject.getString("data");
                    JSONArray jsonArray = new JSONArray(dataStr);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject novelObject = jsonArray.getJSONObject(i);
                        String title = novelObject.getString("title");
                        String rating = novelObject.getString("rating");
                        String imageUrl = novelObject.getString("image_url");
                        String author = novelObject.getString("authors");
                        String date = novelObject.getString("date");
                        String price = novelObject.getString("price");
                        Novel novel = new Novel(rating, title, imageUrl, author, date, price);
                        novelList.add(novel);
                    }

                    novelAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 请求失败的处理
                Log.e("Error", error.toString());
            }
        });
    }
}