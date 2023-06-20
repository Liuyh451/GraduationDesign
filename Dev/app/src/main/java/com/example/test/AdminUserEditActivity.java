package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.cityjd.JDCityPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AdminUserEditActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView avatarImageView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView addressEditText;
    private Button saveChangesButton;
    private String filePath;
    private String userAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_edit);
        Context context = this;
        // 获取传递过来的头像、用户名和密码
        Intent intent = getIntent();
        String avatarPath = intent.getStringExtra("avatarPath");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        // 获取传递的参数（userId）
        int userId = getIntent().getIntExtra("user_id", 0);

        // 将 userId 转换为字符串类型
        String userIdString = String.valueOf(userId);
        // Find views
        // 获取控件
        avatarImageView = findViewById(R.id.avatar_image);
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        addressEditText = findViewById(R.id.address_edittext);
        saveChangesButton = findViewById(R.id.save_changes_button);
        filePath = avatarPath;
        if(userId!=0){
            if (username != null) {
                usernameEditText.setText(username);
            } else {
                Log.d("UEdit", "用户名为空");
            }
            if (password != null) {

                passwordEditText.setText(password);
            } else {
                Log.d("UEdit", "密码为空");
            }
            // 显示头像、用户名和密码
            if (new File(filePath).exists()) {
                // 如果本地文件存在，则使用本地文件
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                avatarImageView.setImageBitmap(bitmap);
            } else {
                // 如果本地文件不存在，则使用 Glide 进行网络加载
                Glide.with(this).load(filePath).placeholder(R.drawable.ic_user).into(avatarImageView);
            }
        }
        else {
            avatarImageView.setImageResource(R.drawable.ic_user);
        }
        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {getAddress();
            }
        });

        // 监听按钮的点击事件
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = userAddress;
                String newUsername = usernameEditText.getText().toString();
                String newPassword = passwordEditText.getText().toString();
                Log.d("updateUinfo", address);
                if(userId!=0){
                NetUnit.updateUserInfo(context, userIdString, newUsername, newPassword, filePath, address, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg = jsonObject.getString("message");
                            Log.d("updateuserinfo", msg);
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
                //添加用户
                else {
                    NetUnit.addUser(context,  newUsername, newPassword, filePath, address, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // 请求成功的处理
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String msg = jsonObject.getString("message");
                                Log.d("updateuserinfo", msg);
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
            }
        });
    }

    // 当用户点击Change Avatar按钮时调用该方法
    public void changeAvatarClicked(View view) {
        // 使用相机或图库获取新头像图片，并在ImageView中显示所选的图像文件
        // ...

        // 示例代码：使用图库选择图片
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // 处理相机或图库返回的结果
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
                    avatarImageView.setImageBitmap(bitmap);
                } else {
                    // 如果本地文件不存在，则使用 Glide 进行网络加载
                    Glide.with(this).load(filePath).into(avatarImageView);
                }

                //avatarImageView.setImageURI(selectedImageUri);
                Log.d("imgpath", filePath);
            }
        }
    }
    private void getAddress()   {
        JDCityPicker cityPicker = new JDCityPicker();
        JDCityConfig jdCityConfig = new JDCityConfig.Builder().build();

        jdCityConfig.setShowType(JDCityConfig.ShowType.PRO_CITY_DIS);
        cityPicker.init(this);
        cityPicker.setConfig(jdCityConfig);
        cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                addressEditText.setText(province.getName() + city.getName()+ district.getName() );
                userAddress=province.getName()+city.getName()+district.getName();
                Log.d("UserModify",userAddress);
            }

            @Override
            public void onCancel() {
            }
        });
        cityPicker.showCityPicker();

    }

}
