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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;

    private String loginResult;


    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int result = msg.arg1;
            boolean boolValue = (result != 0);
            Toast.makeText(MainActivity.this, boolValue ? "登录成功" : "用户名或密码错误", Toast.LENGTH_SHORT).show();
            // 通过 msg.arg1, msg.arg2, msg.obj 等获取子线程数据，然后在主线程中处理
            // 如果用户名和密码均输入正确，登录到主界面
            if(result==1){
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
            if(result==2){
                Toast.makeText(MainActivity.this, "管理员登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MainActivity3_Admin.class);
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
                            String loginUrl = "http://10.0.2.2:5000/login";
                            int loginResult = NetUnit.sendLoginRequest(loginUrl, username, password);
                            Message msg = new Message();
                            msg.arg1 = loginResult;
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

