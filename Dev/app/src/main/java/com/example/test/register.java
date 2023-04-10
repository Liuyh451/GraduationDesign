package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register extends AppCompatActivity {
    private EditText mUsername, mPassword, mConfirmPassword;
    private Button mRegisterBtn;
    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean result = (Boolean) msg.obj;
            // 通过 msg.arg1, msg.arg2, msg.obj 等获取子线程数据，然后在主线程中处理
            Toast.makeText(register.this, result ? "注册成功，已为您自动登录" : "用户名已存在请重新注册", Toast.LENGTH_SHORT).show();
            // 注册完成后自动登录主界面，登录到主界面
            if(result){
                Intent intent = new Intent(register.this, MainActivity2.class);
                startActivity(intent);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // 获取UI组件的引用
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mRegisterBtn = findViewById(R.id.register_btn);

        // 点击注册按钮时执行注册操作
        mRegisterBtn.setOnClickListener(view -> {
            // 获取输入的用户名和密码
            String username = mUsername.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            // 验证输入是否合法
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Username and password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查密码是否匹配
            String confirmPassword = mConfirmPassword.getText().toString().trim();
            if (!password.equals(confirmPassword)) {
                Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(new Runnable() {
                public void run() {
                    Log.d("TAG", username);
                    Log.d("TAG", password);
                    String registerUrl = "http://10.0.2.2:5000/register";
                    boolean loginResult = NetUnit.sendRegisterRequest(registerUrl, username, password);
                    Log.d("TAG", registerUrl);
                    Message msg = new Message();
                    msg.obj = loginResult;
                    mHandler.sendMessage(msg);

                    // 在此处执行网络操作
                }
            }).start();

        });
    }
}