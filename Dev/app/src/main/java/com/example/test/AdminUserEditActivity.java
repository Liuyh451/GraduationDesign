package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class AdminUserEditActivity extends AppCompatActivity {

    private EditText etUserId, etUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_edit);

        // Find views
        etUserId = findViewById(R.id.et_user_id);
        etUserPassword = findViewById(R.id.et_user_password);

        // TODO: Load user data and populate EditTexts
        // For example: int userId = getIntent().getIntExtra("user_id", 0);
    }
}