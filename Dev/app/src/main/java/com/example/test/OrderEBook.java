package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderEBook extends AppCompatActivity {
    private EditText evphone;
    private EditText evBuyerName;
    private String userAddress;

    private String Uid = GlobalVariable.uid;
    private RecyclerView novelRecyclerView;
    private List<Novel> novelList;
    private NovelAdapter novelAdapter;
    private TextView addressResulTv;
    private ImageView addressArrowIv;
    private TextView tvTotal;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ebook);
        novelRecyclerView = findViewById(R.id.novel_list);
        novelList = new ArrayList<>();
        novelAdapter = new NovelAdapter(this, novelList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        novelRecyclerView.setLayoutManager(layoutManager);
        novelRecyclerView.setAdapter(novelAdapter);
        evphone = findViewById(R.id.phone_edit_text);
        evBuyerName = findViewById(R.id.buyerName);
        addressResulTv = findViewById(R.id.address_edt);
        addressArrowIv = findViewById(R.id.address_right_iv);
        tvTotal = findViewById(R.id.tv_total);
        String formattedPrice = getIntent().getStringExtra("price");
        tvTotal.setText("￥" + formattedPrice);
        //设置地址修改监听
        addressArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });

        // 获取Intent对象
        String tag = getIntent().getStringExtra("tag");

        NetUnit.getUserInfo(this, Uid, new Response.Listener<String>() {
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
                    userAddress = userInfo.getString("address");
                    userPhone = userInfo.getString("phone");
                    addressResulTv.setText(userAddress);
                    evphone.setText(userPhone);
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
        NetUnit.getOrderInfo(this, tag, new Response.Listener<String>() {
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
                        String rating = novelObject.getString("book_id");
                        String imageUrl = novelObject.getString("image_url");
                        String price = novelObject.getString("price");
                        String quantity = novelObject.getString("quantity");
                        Novel novel = new Novel(rating, title, imageUrl, price, quantity, price);
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
                // 请求失败的处理
                Log.e("Error", error.toString());
            }
        });

        Button btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnPlaceOrder.setOnClickListener(view -> {

            // Handle place order click
            String buyerName = evBuyerName.getText().toString();
            String address_final = addressResulTv.getText().toString();
            NetUnit.placeOrder(this, tag, buyerName, address_final, evphone.getText().toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 请求成功的处理
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String ratings = jsonObject.optString("ratings");
                        Toast.makeText(getApplication(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

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
            //finish();
        });


    }

    private void getAddress() {
        JDCityPicker cityPicker = new JDCityPicker();
        JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

        jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
        cityPicker.init(this);
        cityPicker.setConfig(jdCityConfig);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                addressResulTv.setText(province.getName() + city.getName() + district.getName());
                userAddress = province.getName() + city.getName() + district.getName();

            }

            @Override
            public void onCancel() {
            }
        });
        cityPicker.showCityPicker();

    }
}
