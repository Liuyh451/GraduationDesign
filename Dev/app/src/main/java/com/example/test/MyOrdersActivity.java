package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private RecyclerView ordersRecView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private String uid=GlobalVariable.uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        orderList = new ArrayList<>();
        ordersRecView=findViewById(R.id.my_orders);

        ordersRecView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        orderAdapter = new OrderAdapter(orderList, (orderId,bookCover,title,author,buyerQuantity,buyerName,price,address,phone) -> {
            //do nothing
        });
        ordersRecView.setAdapter(orderAdapter);
        NetUnit.getMyOrder(this, uid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 请求成功的处理
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String dataStr = jsonObject.getString("orders");
                    JSONArray ordersArray = new JSONArray(dataStr);
                    orderList.clear(); // 清除原有数据
                    for (int i = 0; i < ordersArray.length(); i++) {
                        JSONObject orderObject = ordersArray.getJSONObject(i);
                        int orderNumber = orderObject.getInt("orderid");
                        //int bookId = orderObject.getInt("bookId");
                        String bookCover = orderObject.getString("book_cover");
                        String bookAuthor = orderObject.getString("author");
                        String bookTitle = orderObject.getString("title");
                        double price = orderObject.getDouble("price");
                        String buyerName = orderObject.getString("buyername");
                        int quantity = orderObject.getInt("quantity");
                        double totalPrice = orderObject.getDouble("totalPrice");
                        String address = orderObject.getString("address");
                        String phone = orderObject.getString("phone");
                        Order order = new Order(orderNumber, bookCover, bookAuthor, bookTitle, price, buyerName, quantity, totalPrice, address, phone);
                        orderList.add(order);
                    }
                    orderAdapter.notifyDataSetChanged();
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
}