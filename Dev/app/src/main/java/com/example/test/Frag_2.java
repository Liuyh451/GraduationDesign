package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import java.util.ArrayList;


import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Frag_2 extends Fragment {

    private GridView gridView;
    private EditText searchField;
    private Button refreshButton;

    private BookGridAdapter adapter;
    private ArrayList<Book> books = new ArrayList<>();

    private RequestQueue requestQueue;
    private StringRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_frag_2, container, false);

        // 初始化界面元素
        searchField = rootView.findViewById(R.id.search_field);
        gridView = rootView.findViewById(R.id.books_grid);
        refreshButton = rootView.findViewById(R.id.refresh_button);

        // 创建 GridView 的适配器
        adapter = new BookGridAdapter(getActivity(), books);
        gridView.setAdapter(adapter);

        // 初始化 RequestQueue
        requestQueue = Volley.newRequestQueue(getActivity());

        // 加载图书数据
        loadBooks();

        // 设置按钮的点击事件
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新书籍列表
                loadBooks();
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
                    JSONArray jsonArray = new JSONArray(response);
                    ArrayList<Book> newBooks = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Book book = new Book(
                                jsonObject.getString("title"),
                                jsonObject.getString("authors"),
                                jsonObject.getString("image_url")
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
