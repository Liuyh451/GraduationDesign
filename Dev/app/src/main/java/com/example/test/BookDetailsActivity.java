package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookDetailsActivity extends AppCompatActivity {
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvScore;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;
    private RecyclerView rvReviews;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;

    private Books book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Intent intent = getIntent();
        Books book =new Books(getIntent().getStringExtra("novelTitle"),getIntent().getStringExtra("novelAuthor"),getIntent().getStringExtra("novelCover"),getIntent().getStringExtra("novelId"));
        Log.d("TAG","这里是getintentid"+getIntent().getStringExtra("novelId"));
//
        ivCover = findViewById(R.id.iv_book_cover);
        tvTitle = findViewById(R.id.tv_book_title);
        tvAuthor = findViewById(R.id.tv_book_author);
//        tvScore = findViewById(R.id.tv_score);
//        ratingBar = findViewById(R.id.rating_bar);
//        etComment = findViewById(R.id.et_comment);
//        btnSubmit = findViewById(R.id.btn_submit);
//        rvReviews = findViewById(R.id.rv_reviews);
//
        Glide.with(this).load(book.getCoverUrl()).into(ivCover);
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor()); // 请替换成你自己的获取书籍作者的方法
//        //tvScore.setText(String.format("%.1f", book.getScore())); // 请替换成你自己的获取书籍平均评分的方法
//        //ratingBar.setRating(book.getUserScore()); // 请替换成你自己的获取用户对这本书的评分的方法
//
//        reviewList = new ArrayList<>();
//        reviewAdapter = new ReviewAdapter(reviewList);
//        rvReviews.setAdapter(reviewAdapter);
//
//        // TODO: 添加获取所有用户评价的代码，并将评价数据存入 reviewList 中
//
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 处理提交评价的事件
//                String comment = etComment.getText().toString();
//                float rating = ratingBar.getRating();
//                // TODO: 将评价信息传递给后台服务保存，并更新界面上的评分信息和评价列表
//            }
//        });
    }
    private void requestReviews(int bookId) {
        String url = "http://your-api-url.com/reviews"; // 替换为实际的API地址

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("book_id", bookId);
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
                        String username = reviewObject.getString("username");
                        String content = reviewObject.getString("content");

                        Review review = new Review(username, content);
                        reviewList.add(review);
                    }

                    reviewAdapter.notifyDataSetChanged();
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