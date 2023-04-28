package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AdminEBookEditActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText bookIdEditText;
    private ImageView bookCoverImageview;
    private EditText bookTitleEditText;
    private EditText bookAuthorEditText;
    private EditText bookPriceEditText;
    private EditText bookLanguageEditText;
    private EditText bookDescriptionEditText;
    private Button bookEditSaveButton;
    private Button bookEditCancelButton;
    private String filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ebook_edit);
        Context context = this;
        // 获取传递过来的头像、用户名和密码
        Intent intent = getIntent();
        String bookid = intent.getStringExtra("ebook_id");
        String title = intent.getStringExtra("title");
        String author = intent.getStringExtra("author");
        String bookCover = intent.getStringExtra("bookCover");
        filePath = bookCover;
        //初始化控件
        bookIdEditText = findViewById(R.id.novel_id);
        bookTitleEditText = findViewById(R.id.novel_title);
        bookAuthorEditText = findViewById(R.id.novel_author);
        bookCoverImageview = findViewById(R.id.novel_image);
        bookPriceEditText = findViewById(R.id.novel_price);
        bookLanguageEditText = findViewById(R.id.novel_language);
        bookDescriptionEditText = findViewById(R.id.novel_description);
        bookEditSaveButton = findViewById(R.id.book_edit_save_button);
        bookEditCancelButton = findViewById(R.id.book_edit_cancel_button);
        if (bookid != null) {
            bookIdEditText.setText(bookid);
            bookTitleEditText.setText(title);
            bookAuthorEditText.setText(author);
            //加载图书封面
            if (new File(bookCover).exists()) {
                // 如果本地文件存在，则使用本地文件
                Bitmap bitmap = BitmapFactory.decodeFile(bookCover);
                bookCoverImageview.setImageBitmap(bitmap);
            } else {
                // 如果本地文件不存在，则使用 Glide 进行网络加载
                Glide.with(this).load(bookCover).placeholder(R.drawable.placeholder_add).into(bookCoverImageview);
            }
        } else {
            bookCoverImageview.setImageResource(R.drawable.placeholder_add);
        }


        //监听封面，点击时更换封面
        bookCoverImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 示例代码：使用图库选择图片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);

            }
        });
        bookEditSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newBookId = bookIdEditText.getText().toString();
                String newBookTitle = bookTitleEditText.getText().toString();
                String newBookAuthor = bookAuthorEditText.getText().toString();
                String newBookPrice = bookPriceEditText.getText().toString();
                String newBookDescription = bookDescriptionEditText.getText().toString();
                String newBookCove = filePath;
                String newLanguage = bookLanguageEditText.getText().toString();
                NetUnit.modifyBook(context, newBookId, newBookTitle, newBookAuthor, filePath, newBookPrice, newBookDescription, newLanguage, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("msg");
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
                // 执行相应的点击操作
                // 这里可以添加你需要执行的代码
            }
        });
        bookEditCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 关闭当前活动并返回上一个Activity
                // 执行相应的点击操作
                // 这里可以添加你需要执行的代码
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // 处理相机返回的结果
                // ...
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // 处理图库返回的结果
                Uri selectedImageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImageUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
                // 加载图片
                if (new File(filePath).exists()) {
                    // 如果本地文件存在，则使用本地文件
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    bookCoverImageview.setImageBitmap(bitmap);
                } else {
                    // 如果本地文件不存在，则使用 Glide 进行网络加载
                    Glide.with(this).load(filePath).into(bookCoverImageview);
                }

                //avatarImageView.setImageURI(selectedImageUri);
                Log.d("imgpath", filePath);
            }
        }
    }
}