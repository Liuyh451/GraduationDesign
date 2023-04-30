package com.example.test;

import android.app.Activity;
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
import com.example.test.Order;
import com.example.test.OrderAdapter;
import com.example.test.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderFragment extends Fragment {
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    public static final int REQUEST_CODE_B = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin__order, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, (orderId,bookCover,title,author,buyerQuantity,buyerName,price,address,phone) -> {
            // 处理订单条目点击事件
            navigateToOrderEdit(orderId,bookCover,title,author,buyerQuantity,buyerName,price,address,phone);
        });
        recyclerView.setAdapter(orderAdapter);

        requestData();

        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_B && resultCode == Activity.RESULT_OK) {
            requestData();
            // 在此进行数据的更新操作
            // ...
        }
    }

    private void navigateToOrderEdit(int orderId,String bookCover,String title,String author,String buyerQuantity,String buyerName,String price,String address,String phone) {
        Intent intent = new Intent(getActivity(), AdminOrderEditActivity.class);
        // 添加需要传递的值
        intent.putExtra("orderId", Integer.toString(orderId));
        intent.putExtra("title", title);
        intent.putExtra("author", author);
        intent.putExtra("bookCover", bookCover);
        intent.putExtra("buyerQuantity", buyerQuantity);
        intent.putExtra("buyerName", buyerName);
        intent.putExtra("price", price);
        intent.putExtra("address", address);
        intent.putExtra("phone", phone);
        startActivityForResult(intent, REQUEST_CODE_B);
        //startActivity(intent);
    }

    private void requestData() {
        String url = "http://10.0.2.2:5000/allOrders"; // 替换为实际的 API 地址

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String dataStr = response.getString("orders");
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
                // 处理错误情况
            }
        });

        // 将请求添加到请求队列
        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }
}
