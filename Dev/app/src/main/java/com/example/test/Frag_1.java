package com.example.test;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Frag_1 extends Fragment {
    //jia

    private RecyclerView novelRecyclerView;
    private NovelAdapter novelAdapter;
    private List<Novel> novelList;
    private String uid;
    private String Uid = GlobalVariable.uid;


    public Frag_1() {
        // Required empty public constructor
    }

    public static Frag_1 newInstance(String uid) {
        Frag_1 fragment = new Frag_1();
        Bundle args = new Bundle();
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_1, container, false);

        novelList = new ArrayList<>();
        // 请求初始数据
        // 在 Fragment 中获取传递的值
        // 获取从 MainActivity2 传递过来的 uid
        int uid_int = Integer.parseInt(Uid);
        Log.d("TAG", "这里是frag的uid" + Uid);
        requestData(uid_int);
        novelRecyclerView = view.findViewById(R.id.novel_recycler_view);
        novelAdapter = new NovelAdapter(requireContext(), novelList);
        novelRecyclerView.setAdapter(novelAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        novelRecyclerView.setLayoutManager(layoutManager);
//        novelRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        novelAdapter.setOnItemClickListener(new NovelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Novel novel) {
                //更改：把novel的参数传过去，减少请求量 2023年4月17日18点12分
                // 跳转到目标Activity，这里是NovelDetailActivity
                Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
                intent.putExtra("novelId", novel.getNovelId());
                intent.putExtra("novelTitle", novel.getTitle());
                intent.putExtra("novelAuthor", novel.getAuthor());
                intent.putExtra("novelCover", novel.getImageUrl());
                startActivity(intent);
            }
        });

//        novelRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                int totalItemCount = layoutManager.getItemCount();
//                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//
//                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                        && firstVisibleItemPosition >= 0) {
//                    loadMoreNovels();
//                }
//            }
//        });

        return view;
    }

    private void loadMoreNovels() {
        //int nextPage = novelList.size() / 4 + 1; // 假设每次请求6个小说
//        Log.d("TAG", String.valueOf(nextPage) );
        String url = "http://10.0.2.2:5000/api/novels";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String dataStr = response.getString("data");
                            JSONArray novelsArray = new JSONArray(dataStr);
                            for (int i = 0; i < novelsArray.length(); i++) {
                                JSONObject novelObject = novelsArray.getJSONObject(i);
                                String title = novelObject.getString("title");
                                String novelId = novelObject.getString("book_id");
                                String imageUrl = novelObject.getString("image_url");
                                String author = novelObject.getString("authors");
                                String description = novelObject.getString("language_code");

                                Novel novel = new Novel(novelId, title, imageUrl, author, description);
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
                novelRecyclerView.stopScroll();
            }
        });

// 将请求添加到请求队列
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private Response.ErrorListener onErrorResponseListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // 在请求失败时停止 RecyclerView 的滚动
            novelRecyclerView.stopScroll();

            // 处理其他错误情况
            // ...
        }
    };


//    private void loadMoreNovels() { // 模拟异步请求加载更多数据
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int startIndex = novelList.size();
//                for (int i = startIndex; i < startIndex + 6; i++) {
//                    novelList.add(new Novel("小说标题" + i, "https://example.com/image" + i + ".jpg", "作者" + i, "简介" + i));
//                }
//                novelAdapter.notifyDataSetChanged();
//            }
//        }, 2000); // 延迟2秒 } }
//
//    }

//    private void requestData() {
//        String url = "http://10.0.2.2:5000//api/novels"; // 替换为实际的API地址
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String dataStr = response.getString("data");
//                            JSONArray novelsArray=new JSONArray(dataStr);
////                            JSONArray novelsArray = resp.getJSONArray();
//                            for (int i = 0; i < novelsArray.length(); i++) {
//
//                                JSONObject novelObject = novelsArray.getJSONObject(i);
//                                Log.d("TAG","novelsArray"+novelsArray.length());
//                                Log.d("TAG","novelObject"+novelObject.toString());
//                                String novelId=novelObject.getString("book_id");
//                                String title = novelObject.getString("title");
//                                Log.d("TAG","title"+title);
//                                String imageUrl = novelObject.getString("image_url");
//                                String author = novelObject.getString("authors");
//                                String description = novelObject.getString("language_code");
//
//                                Novel novel = new Novel(novelId,title, imageUrl, author, description);
//                                novelList.add(novel);
//                            }
//                            novelAdapter.notifyDataSetChanged();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // 处理错误情况
//            }
//        });
//
//// 将请求添加到请求队列
//        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
//            }


    private void requestData(int uid) {
        String url = "http://10.0.2.2:5000/getRecomBooks";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        try {
                            // 将响应转换为 JSON 对象
                            JSONObject jsonResponse = new JSONObject(response);
                            String dataStr = jsonResponse.getString("data");

                            JSONArray novelsArray = new JSONArray(dataStr);
//                            JSONArray novelsArray = resp.getJSONArray();
                            for (int i = 0; i < novelsArray.length(); i++) {
                                JSONObject novelObject = novelsArray.getJSONObject(i);
                                String novelId = novelObject.getString("book_id");
                                String title = novelObject.getString("title");

                                String imageUrl = novelObject.getString("image_url");
                                String author = novelObject.getString("authors");
                                String description = novelObject.getString("language_code");

                                Novel novel = new Novel(novelId, title, imageUrl, author, description);
                                novelList.add(novel);
                            }
                            novelAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    jsonObject.put("uid", uid);
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
        Volley.newRequestQueue(requireContext()).add(stringRequest);
    }


}
