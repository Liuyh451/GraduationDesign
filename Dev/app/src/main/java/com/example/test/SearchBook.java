package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class SearchBook extends AppCompatActivity {
    private EditText searchField;
    private ImageView searchIcon;
    private Button searchButton;
    private RecyclerView searchResults;
    private BookAdapter bookAdapter;
    private ArrayList<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        // 获取布局控件
        searchField = findViewById(R.id.search_field);
        //searchIcon = findViewById(R.id.search_icon);
        searchButton = findViewById(R.id.search_button);
        searchResults = findViewById(R.id.search_results);

        // 初始化RecyclerView
        //bookAdapter = new BookAdapter(bookList);
        searchResults.setAdapter(bookAdapter);
        searchResults.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 设置搜索按钮点击事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = searchField.getText().toString().trim();
               // searchBooks(keyword);
            }
        });
    }


}
