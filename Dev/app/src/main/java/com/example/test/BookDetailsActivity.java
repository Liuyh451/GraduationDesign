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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailsActivity extends AppCompatActivity {
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvScore;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;
    private Button btnBuy;
    private RecyclerView rvReviews;
    private List<Review> reviewList;
    private ReviewAdapter reviewAdapter;
    private String Uid = GlobalVariable.uid;

    private Books book;
    private  float bookRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Intent intent = getIntent();
        Books book = new Books(getIntent().getStringExtra("novelTitle"), getIntent().getStringExtra("novelAuthor"), getIntent().getStringExtra("novelCover"), getIntent().getStringExtra("novelId"));
        Log.d("TAG", "这里是详情页的uid" + Uid);
//
        ivCover = findViewById(R.id.iv_book_cover);
        tvTitle = findViewById(R.id.tv_book_title);
        tvAuthor = findViewById(R.id.tv_book_author);
        tvScore = findViewById(R.id.tv_score);
        ratingBar = findViewById(R.id.rating_bar);
        etComment = findViewById(R.id.et_comment);
        btnSubmit = findViewById(R.id.btn_submit);
        rvReviews = findViewById(R.id.rv_reviews);
        btnBuy = findViewById(R.id.btn_buy);
//
        Glide.with(this).load(book.getCoverUrl()).into(ivCover);
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor()); // 请替换成你自己的获取书籍作者的方法
//        //tvScore.setText(String.format("%.1f", book.getScore())); // 请替换成你自己的获取书籍平均评分的方法
        NetUnit.getRating(this, Uid, book.getBookId(),  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 请求成功的处理
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String ratings = jsonObject.optString("ratings");
                    bookRating=Float.parseFloat(ratings);
                    //todo 更新评分值
                    Log.d("NetUnit", "Ratings: " + bookRating);
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

        ratingBar.setRating(bookRating); // 请替换成你自己的获取用户对这本书的评分的方法
        //todo 网络没问题，解决一下为什么显示星星
        requestReviews(book.getBookId());
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setAdapter(reviewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(layoutManager);

        // TODO: 减小卡片的间隔

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理提交评价的事件
                //String comment = etComment.getText().toString();
                float rating = ratingBar.getRating();
                String ratingStr = Float.toString(rating);
                saveRating(Uid,book.getBookId(),ratingStr);
                Log.d("TAG","ratingBar"+rating);
                // TODO: 将评价信息传递给后台服务保存，并更新界面评价列表
            }
        });
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 处理提交评价的事件
                Intent intent = new Intent(BookDetailsActivity.this, OrderEBook.class);
                intent.putExtra("bookid", book.getBookId());
                intent.putExtra("book_cover", book.getCoverUrl());
                intent.putExtra("title",book.getTitle());
                intent.putExtra("author",book.getAuthor());
                startActivity(intent);
            }
        });
    }

    private void requestReviews(String bookId) {
        String url = "http://10.0.2.2:5000/book/reviews"; // 替换为实际的API地址
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
                    String dataStr = response.getString("reviews");
                    JSONArray reviewsArray = new JSONArray(dataStr);

                    reviewList.clear(); // 清除原有数据

                    for (int i = 0; i < reviewsArray.length(); i++) {
                        JSONObject reviewObject = reviewsArray.getJSONObject(i);
                        String username = reviewObject.getString("user_id");
                        String content = reviewObject.getString("review");

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

    private void saveRating(String uid, String book_id, String rating) {
        String url = "http://10.0.2.2:5000/rating_and_recom";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        //todo 更新评分值

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 请求失败的处理
                        Log.e("Error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("user_id", uid);
                    jsonObject.put("book_id", book_id);
                    jsonObject.put("rating", rating);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("data", jsonObject.toString());

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // 将请求添加到请求队列
        Volley.newRequestQueue(this).add(stringRequest);
    }


}
