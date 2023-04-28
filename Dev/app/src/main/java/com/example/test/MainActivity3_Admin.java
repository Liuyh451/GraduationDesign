package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity3_Admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3_admin);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);

        // 设置底部导航栏选中监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_admin_item1:
                        // 点击导航栏项 1
                        openFragment(new AdminUsersFragment());
                        return true;
                    case R.id.navigation_admin_item2:
                        // 点击导航栏项 2
                        openFragment(new AdminEBooksFragment());
                        return true;
                    case R.id.navigation_admin_item3:
                        // 点击导航栏项 3
                        openFragment(new AdminOrderFragment());
                        return true;
                    default:
                        return false;
                }
            }
        });

        // 默认选中第一个导航栏项
        bottomNavigationView.setSelectedItemId(R.id.navigation_admin_item1);
    }

    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}