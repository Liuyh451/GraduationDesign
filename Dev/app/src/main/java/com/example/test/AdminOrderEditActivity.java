package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AdminOrderEditActivity extends AppCompatActivity {
    private ImageView bookImage;
    private TextView bookTitle;
    private TextView bookAuthor;
    private TextView bookPrice;

    private EditText quantityText;

    private EditText addressSpinner;
    private EditText phoneEditText;
    private EditText nameEditText;
    private Button submitButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_edit);
        Context context=this;
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String bookCover = intent.getStringExtra("bookCover");
        String buyerQuantity = intent.getStringExtra("buyerQuantity");
        String buyerName = intent.getStringExtra("buyerName");
        String price = intent.getStringExtra("price");
        String address = intent.getStringExtra("address");
        String phone = intent.getStringExtra("phone");
        // Initialize Views
        bookImage = findViewById(R.id.book_image);
        bookTitle = findViewById(R.id.book_title);
        bookAuthor = findViewById(R.id.book_author);
        bookPrice = findViewById(R.id.book_price);
        quantityText = findViewById(R.id.quantity_text);
        addressSpinner = findViewById(R.id.address_spinner);
        phoneEditText = findViewById(R.id.phone_edit_text);
        nameEditText = findViewById(R.id.name_edit_text);
        submitButton = findViewById(R.id.submit_button);
        deleteButton = findViewById(R.id.delete_button);
        if (orderId != null) {
            bookTitle.setText(title);
            bookAuthor.setText(author);
            bookPrice.setText(price);
            phoneEditText.setText(phone);
            addressSpinner.setText(address);
            nameEditText.setText(buyerName);
            quantityText.setText(buyerQuantity);
            //加载图书封面
            if (new File(bookCover).exists()) {
                // 如果本地文件存在，则使用本地文件
                Bitmap bitmap = BitmapFactory.decodeFile(bookCover);
                bookImage.setImageBitmap(bitmap);
            } else {
                // 如果本地文件不存在，则使用 Glide 进行网络加载
                Glide.with(this).load(bookCover).placeholder(R.drawable.placeholder_add).into(bookImage);
            }
        } else {
            bookImage.setImageResource(R.drawable.placeholder_add);
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取书籍价格
                String newPrice = bookPrice.getText().toString();
                //获取手机号
                String newPhone = phoneEditText.getText().toString();
                // 获取客户姓名
                String newBuyerName = nameEditText.getText().toString();
                //获取地址
                String newAddress = addressSpinner.getText().toString();
                //获取数量
                String newQuantity = quantityText.getText().toString();


                // 在此处理submitButton被点击后的逻辑代码
                NetUnit.modifyOrder(context, orderId, newBuyerName, newPrice, newQuantity, address,newPhone, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.d("updateuserinfo", msg);
                            setResult(RESULT_OK);
                            finish();
                            //requestReviews(book.getBookId());
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
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetUnit.deleteOrder(context, orderId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            Log.d("updateuserinfo", msg);
                            setResult(RESULT_OK);
                            finish();
                            //requestReviews(book.getBookId());
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
                // 在此处理deleteButton被点击后的逻辑代码
            }
        });


    }
}