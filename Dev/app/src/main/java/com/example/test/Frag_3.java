package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class Frag_3 extends Fragment {
    private String Uid = GlobalVariable.uid;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public Frag_3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_3, container, false);

        // Find TextViews by id
        ImageView imUserAvatar=view.findViewById(R.id.user_image);
        TextView Username = view.findViewById(R.id.user_name);
//        TextView tvMyOrders = view.findViewById(R.id.tv_my_orders);
//        TextView tvMyRatings = view.findViewById(R.id.tv_my_ratings);
//        TextView tvMyFavorites = view.findViewById(R.id.tv_my_favorites);
//        TextView tvMyReviews = view.findViewById(R.id.tv_my_reviews);
        Log.d("UID",Uid);
        NetUnit.getUserInfo(context, Uid,   new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 请求成功的处理
                try {
                    JSONObject data = new JSONObject(response);
                    String userInfoStr = data.getString("userInfo");

                    //从userInfo字符串构建一个JSONObject
                    JSONObject userInfo = new JSONObject(userInfoStr);

                    //获取username和avatar字段
                    String username = userInfo.getString("username");
                    String avatar = userInfo.getString("avatar");
                    Username.setText(username);
                    Glide.with(context)
                            .load(avatar)
                            .into(imUserAvatar);

                    //在Logcat中输出结果
                    Log.d("JSON", "Username is: " + username);
                    Log.d("JSON", "Avatar is: " + avatar);

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
//
//        // Set click listeners
//        tvMyOrders.setOnClickListener(v -> navigateToMyOrders());
//        tvMyRatings.setOnClickListener(v -> navigateToMyRatings());
//        tvMyFavorites.setOnClickListener(v -> navigateToMyFavorites());
//        tvMyReviews.setOnClickListener(v -> navigateToMyReviews());
        LinearLayout myOrdersLayout = view.findViewById(R.id.my_orders);
        LinearLayout myRatingsLayout = view.findViewById(R.id.my_ratings);
        LinearLayout myFavoritesLayout = view.findViewById(R.id.my_favorites);
        LinearLayout myReviewsLayout = view.findViewById(R.id.my_reviews);

// Set click listeners
        myOrdersLayout.setOnClickListener(v -> navigateToMyOrders());
        myRatingsLayout.setOnClickListener(v -> navigateToMyRatings());
        myFavoritesLayout.setOnClickListener(v -> navigateToMyFavorites());
        myReviewsLayout.setOnClickListener(v -> navigateToMyReviews());


        return view;
    }

    private void navigateToMyOrders() {
        Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
        startActivity(intent);
    }

    private void navigateToMyRatings() {
        Intent intent = new Intent(getActivity(), MyRatingsActivity.class);
        startActivity(intent);
    }

    private void navigateToMyFavorites() {
        Intent intent = new Intent(getActivity(), MyFavoritesActivity.class);
        startActivity(intent);
    }

    private void navigateToMyReviews() {
        Intent intent = new Intent(getActivity(), MyReviewsActivity.class);
        startActivity(intent);
    }
}