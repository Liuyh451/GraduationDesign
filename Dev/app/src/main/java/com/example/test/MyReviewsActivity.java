package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyReviewsActivity extends AppCompatActivity {
    //    偷个懒复用一下之前的适配器
    private RecyclerView mRecyclerView;
    private List<MyReview> reviewList;

    private MyReviewAdapter mAdapter;
    private String uid = GlobalVariable.uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);
        mRecyclerView = findViewById(R.id.review_list);
        reviewList = new ArrayList<>();
        mAdapter = new MyReviewAdapter(reviewList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestReviews(uid);

    }

    private void requestReviews(String userId) {
        String url = "http://10.0.2.2:5000/user/reviews"; // 替换为实际的API地址
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("uid", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, requestObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String dataStr = response.getString("data");
                    JSONArray reviewsArray = new JSONArray(dataStr);

                    reviewList.clear(); // 清除原有数据

                    for (int i = 0; i < reviewsArray.length(); i++) {
                        JSONObject reviewObject = reviewsArray.getJSONObject(i);
                        String title = reviewObject.getString("title");
                        String author = reviewObject.getString("authors");
                        String bookCover = reviewObject.getString("image_url");
                        String review = reviewObject.getString("review");
                        MyReview myReview = new MyReview(title, author, bookCover, review);
                        reviewList.add(myReview);
                    }

                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 处理错误情况
            }
        });

        // 将请求添加到请求队列
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}