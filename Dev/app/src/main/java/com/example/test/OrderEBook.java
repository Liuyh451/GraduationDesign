package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class OrderEBook extends AppCompatActivity {
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ebook);
        ivCover = findViewById(R.id.book_image);
        tvTitle = findViewById(R.id.book_title);
        tvAuthor = findViewById(R.id.book_author);
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

        Spinner addressSpinner = findViewById(R.id.address_spinner);
//        String selectedAddress = addressSpinner.getSelectedItem().toString();
        // TODO 这里获取的是空值会引起程序崩溃
        Button btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnPlaceOrder.setOnClickListener(view -> {
            // Handle place order click
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
            //finish();
        });


    }
}
