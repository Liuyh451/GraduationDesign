package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchBook extends AppCompatActivity {
    private EditText searchField;
    private ImageView searchIcon;
    private Button searchButton;
    private RecyclerView searchResults;
    private NovelAdapter novelAdapter;
    //private ArrayList<Book> bookList = new ArrayList<>();
    private List<Novel> novelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        Context context = this;
        Intent intent = getIntent();
        String is_Admin = intent.getStringExtra("is_Admin");
        // 获取布局控件
        searchField = findViewById(R.id.search_field);
        //searchIcon = findViewById(R.id.search_icon);
        searchButton = findViewById(R.id.search_button);
        searchResults = findViewById(R.id.rv_search_results);

        // 初始化RecyclerView
        novelList = new ArrayList<>();
        novelAdapter = new NovelAdapter(this, novelList);
        searchResults.setAdapter(novelAdapter);
        searchResults.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        novelAdapter.setOnItemClickListener(new NovelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Novel novel) {
                //如果是用户跳转过来的，进入详情页面
                if (is_Admin.equals("0")) {
                    //更改：把novel的参数传过去，减少请求量 2023年4月17日18点12分
                    // 跳转到目标Activity，这里是NovelDetailActivity
                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra("novelId", novel.getNovelId());
                    intent.putExtra("novelTitle", novel.getTitle());
                    intent.putExtra("novelAuthor", novel.getAuthor());
                    intent.putExtra("novelCover", novel.getImageUrl());
                    startActivity(intent);
                }
                //如果是管理跳转过来的，进入编辑页面
                else {
                    Toast.makeText(getApplicationContext(), "跳到管理", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, AdminEBookEditActivity.class);
                    intent.putExtra("ebook_id", novel.getNovelId());
                    intent.putExtra("title", novel.getTitle());
                    intent.putExtra("author", novel.getAuthor());
                    intent.putExtra("bookCover", novel.getImageUrl());
                    startActivity(intent);
                }
            }
        });


        // 设置搜索按钮点击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = searchField.getText().toString().trim();
                NetUnit.searchBook(context, keyword, new Response.Listener<String>() {
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
                                String novelId = novelObject.getString("book_id");
                                String imageUrl = novelObject.getString("image_url");
                                String author = novelObject.getString("authors");
                                String description = "111";

                                Novel novel = new Novel(novelId, title, imageUrl, author, description);

                                novelList.add(novel);


                            }

                            novelAdapter.notifyDataSetChanged();
                            searchResults.invalidate();
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
        });

    }


}
