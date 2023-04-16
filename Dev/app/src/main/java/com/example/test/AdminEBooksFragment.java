package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.Book;
import com.example.test.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminEBooksFragment extends Fragment {
    private BookAdapter bookAdapter;
    private List<Books> bookList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin__books, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_ebooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(bookList, bookId -> {
            // 处理书籍条目点击事件
        });
        recyclerView.setAdapter(bookAdapter);

        requestData();

        return view;
    }
    private void navigateToEBookEdit(int ebookId) {
        Intent intent = new Intent(getActivity(), AdminEBookEditActivity.class);
        intent.putExtra("ebook_id", ebookId);
        startActivity(intent);
    }

    private void requestData() {
        String url = "http://10.0.2.2:5000/getAllBooks"; // 替换为实际的 API 地址

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String dataStr = response.getString("books");
                            JSONArray booksArray = new JSONArray(dataStr);
                            Log.d("TTT",dataStr);
                            bookList.clear(); // 清除原有数据
                            for (int i = 0; i < booksArray.length(); i++) {
                                JSONObject booksObject = booksArray.getJSONObject(i);
                                String bookid=booksObject.getString("book_id");
                                int id = Integer.parseInt(bookid);
                                String title = booksObject.getString("title");
                                String author = booksObject.getString("authors");
                                String coverUrl = booksObject.getString("image_url");
                                Books book = new Books(title, author, coverUrl,id);
                                bookList.add(book);
                            }
                            bookAdapter.notifyDataSetChanged();
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
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }
}
