package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;


import java.util.ArrayList;


import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Frag_2 extends Fragment {

    private GridView gridView;
    private Button refreshButton;

    private BookGridAdapter adapter;
    private ArrayList<Book> books = new ArrayList<>();

    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_frag_2, container, false);
        Context context=getActivity();
        // 初始化界面元素
        // 查找该布局中的 "my_relative_layout" 视图
        RelativeLayout searchField = (RelativeLayout) rootView.findViewById(R.id.search_field);
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 执行下拉刷新操作，比如请求最新数据
                loadBooks();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        gridView = rootView.findViewById(R.id.books_grid);
        // 创建 GridView 的适配器
        adapter = new BookGridAdapter(getActivity(), books);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                 在这里处理项的点击事件
//                 根据点击的位置进行相应的操作，如跳转到下一个活动
                Book clickedBook = books.get(position);
                String title=clickedBook.getTitle();
                String author=clickedBook.getAuthor();
                String bookCover=clickedBook.getCoverUrl();
                String price=clickedBook.getPrice();
                Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
                intent.putExtra("novelId", clickedBook.getBookId());
                intent.putExtra("novelTitle", title);
                intent.putExtra("novelAuthor", author);
                intent.putExtra("novelCover", bookCover);
                // 创建一个 Intent 对象，用于从当前活动跳转到下一个活动
                intent.putExtra("novelRating", clickedBook.getRating());
                intent.putExtra("price", price);
                startActivity(intent);
            }
        });

        gridView.setAdapter(adapter);

        // 初始化 RequestQueue
        requestQueue = Volley.newRequestQueue(getActivity());
        // 加载图书数据
        loadBooks();
        // 设置按钮的点击事件
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 更新书籍列表
//                loadBooks();
//            }
//        });
        searchField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到搜索页面
                Intent intent = new Intent(getActivity(), SearchBook.class);
                intent.putExtra("is_Admin", "0");
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void loadBooks() {
        String url = "http://10.0.2.2:5000/api/topBooks";
        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // 将响应数据转换成 Book 对象列表
                    Log.d("topbooks",response);
                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<Book> newBooks = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Book book = new Book(
                                jsonObject.getString("title"),
                                jsonObject.getString("authors"),
                                jsonObject.getString("image_url"),
                                jsonObject.getString("price"),
                                jsonObject.getString("book_id"),
                                jsonObject.getString("rating")
                        );
                        newBooks.add(book);
                    }

                    // 更新列表数据
                    books.clear();
                    books.addAll(newBooks);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e("PopBooksLoadError", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PopBooksLoadError", error.getMessage());
            }
        });

        // 发送网络请求
        requestQueue.add(request);
    }


}
