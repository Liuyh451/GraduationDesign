package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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


    }
}