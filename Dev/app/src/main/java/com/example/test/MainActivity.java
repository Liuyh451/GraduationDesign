package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;

    private String loginResult;


    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean result = (Boolean) msg.obj;

            Toast.makeText(MainActivity.this, result ? "登录成功" : "用户名或密码错误", Toast.LENGTH_SHORT).show();
            // 通过 msg.arg1, msg.arg2, msg.obj 等获取子线程数据，然后在主线程中处理
            // 如果用户名和密码均输入正确，登录到主界面
            if(result){
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }

        }
    };

    // 登录按钮控件
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);

        // 为登录按钮添加点击事件监听器
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入的用户名和密码
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // 检查用户名和密码是否合法
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    // 如果用户名或密码为空，提示用户重新输入
                    Toast.makeText(MainActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        public void run() {
                            Log.d("TAG", username);
                            Log.d("TAG", password);
                            String loginUrl = "http://10.0.2.2:5000/login";
                            boolean loginResult = NetUnit.sendRegisterRequest(loginUrl, username, password);
                            Log.d("TAG", loginUrl);
                            Message msg = new Message();
                            msg.obj = loginResult;
                            mHandler.sendMessage(msg);

                            // 在此处执行网络操作
                        }
                    }).start();
                }
            }
        });
    }
    //点击注册按钮后跳转到注册页面
    public void openNextActivity(View view) {
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }


}

