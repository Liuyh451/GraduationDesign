package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.test.R;
import com.example.test.User;
import com.example.test.UserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {
    private UserAdapter userAdapter;
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin__users, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_users);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(-1)) {
                    // 滚动停止且滚动到顶部，启动新活动
                    Intent intent = new Intent(getActivity(), AdminUserEditActivity.class);
                    startActivity(intent);
                }
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, (userId, username, password, avatarPath) -> {
            // Handle user item click
            navigateToUserEdit(userId, username, password, avatarPath);

        });
        recyclerView.setAdapter(userAdapter);

        requestUserData();

        return view;
    }

    private void navigateToUserEdit(int userId, String username, String password, String avatarpath) {
        Intent intent = new Intent(getActivity(), AdminUserEditActivity.class);
        intent.putExtra("user_id", userId);
        // 添加需要传递的值
        intent.putExtra("avatarPath", avatarpath);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void requestUserData() {
        String url = "http://10.0.2.2:5000/getAllUsers"; // 替换为实际的API地址

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String dataStr = response.getString("users");
                    JSONArray usersArray = new JSONArray(dataStr);

                    userList.clear(); // 清除原有数据

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject usersObject = usersArray.getJSONObject(i);
                        String userid = usersObject.getString("id");
                        int id = Integer.parseInt(userid);
                        String username = usersObject.getString("username");
                        String password = usersObject.getString("password");
                        String avatar = usersObject.getString("avatar");

                        User user = new User(username, password, avatar, id);
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
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