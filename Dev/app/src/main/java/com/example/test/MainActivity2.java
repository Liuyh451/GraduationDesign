package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 设置底部导航栏选中监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_item1:
                        // 点击导航栏项 1
                        openFragment(new Frag_1());
                        return true;
                    case R.id.navigation_item2:
                        // 点击导航栏项 2
                        openFragment(new Frag_2());
                        return true;
                    case R.id.navigation_item3:
                        // 点击导航栏项 3
                        openFragment(new Frag_3());
                        return true;
                    default:
                        return false;
                }
            }
        });

        // 默认选中第一个导航栏项
        bottomNavigationView.setSelectedItemId(R.id.navigation_item1);
    /**
     * 默认第一个页面，为主页面
     */
}
    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.novel_recommend_fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

}

