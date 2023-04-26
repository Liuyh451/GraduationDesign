package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderEBook extends AppCompatActivity {
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private EditText evphone;
    private  String selectedAddress;
    private String Uid = GlobalVariable.uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ebook);
        ivCover = findViewById(R.id.book_image);
        tvTitle = findViewById(R.id.book_title);
        tvAuthor = findViewById(R.id.book_author);
        evphone = findViewById(R.id.phone_edit_text);
        Books book = new Books(getIntent().getStringExtra("title"), getIntent().getStringExtra("author"), getIntent().getStringExtra("book_cover"), getIntent().getStringExtra("bookid"));
        Glide.with(this).load(book.getCoverUrl()).into(ivCover);
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor()); // 请替换成你自己的获取书籍作者的方法
        //这里是控制加减按钮的部分
        Button addButton = findViewById(R.id.add_button);
        Button minusButton = findViewById(R.id.minus_button);
        TextView quantityText = findViewById(R.id.quantity_text);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentValue = quantityText.getText().toString();
                int newValue = Integer.parseInt(currentValue) + 1;
                quantityText.setText(String.valueOf(newValue));
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentValue = quantityText.getText().toString();
                int newValue = Integer.parseInt(currentValue) - 1;
                if(newValue < 0){
                    newValue = 0;
                }
                quantityText.setText(String.valueOf(newValue));
            }
        });
        // 适配器中设置 Spinner 的选项列表和显示方式
        Spinner spinner = findViewById(R.id.address_spinner);
        //todo 通过网络获取用户常用地址
        ArrayList<String> items = new ArrayList<String>(Arrays.asList("Item 1", "Item 2", "Item 3"));
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, items);
        spinner.setAdapter(adapter);
        Spinner addressSpinner = findViewById(R.id.address_spinner);
        // 在确定选定项时，将其与已知索引进行比较，并将其赋值给选定地址变量
        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAddress = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //String selectedAddress = addressSpinner.getSelectedItem().toString();
        //  这里获取的是空值会引起程序崩溃
        Button btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnPlaceOrder.setOnClickListener(view -> {
            Log.d("order",book.getBookId());
            Log.d("order",book.getTitle());
            Log.d("order",book.getAuthor());
            Log.d("order",book.getCoverUrl());
            Log.d("order",quantityText.getText().toString());
            Log.d("order",selectedAddress);
            Log.d("order",evphone.getText().toString());


            // Handle place order click
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
            NetUnit.placeOrder(this, Uid, book.getBookId(), book.getTitle(),book.getAuthor(), book.getCoverUrl(),"9.9",quantityText.getText().toString(),selectedAddress,evphone.getText().toString(),new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 请求成功的处理
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        String ratings = jsonObject.optString("ratings");


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
}
