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

public class MyRatingsActivity extends AppCompatActivity {
    private RecyclerView userRatingRecView;
    private UserRatingAdapter userRatingAdapter;
    private List<Novel> novelList;
    private String uid=GlobalVariable.uid;
    private ImageView backArrowIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ratings);
        novelList = new ArrayList<>();
        userRatingRecView=findViewById(R.id.user_ratings);
        userRatingRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        userRatingAdapter = new UserRatingAdapter(this,novelList);
        userRatingRecView.setAdapter(userRatingAdapter);
        backArrowIv=findViewById(R.id.iv_backward);
        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        NetUnit.getUserRating(this, uid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 请求成功的处理
                try {
                    novelList.clear();

                    // 将响应数据转换成 Book 对象列表
                    JSONObject jsonObject = new JSONObject(response);
                    String dataStr = jsonObject.getString("ratings");
                    JSONArray jsonArray = new JSONArray(dataStr);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject novelObject = jsonArray.getJSONObject(i);
                        String novelId = novelObject.getString("book_id");
                        String title = novelObject.getString("title");
                        String rating = novelObject.getString("rating");
                        String imageUrl = novelObject.getString("image_url");
                        String author = novelObject.getString("authors");
                        String price=novelObject.getString("price");
                        Novel novel = new Novel(novelId, title, imageUrl, author, rating,price);
                        novelList.add(novel);
                    }

                    userRatingAdapter.notifyDataSetChanged();
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