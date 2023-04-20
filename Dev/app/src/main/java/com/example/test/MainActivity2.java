package com.example.test;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.test.Frag_1;
import com.example.test.Frag_2;
import com.example.test.Frag_3;
import com.example.test.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        uid = getIntent().getStringExtra("uid");
        Log.d("TAG","m2------"+uid);

        // 设置底部导航栏选中监听器
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_item1:
                        // 点击导航栏项 1
                        openFragment(Frag_1.newInstance(uid));
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
        openFragment(Frag_1.newInstance(uid));
    }

//    private void openFragment(Fragment fragment, String uid) {
//        if (fragment instanceof Frag_1 && uid != null) {
//            Bundle bundle = new Bundle();
//            bundle.putString("uid", uid);
//            fragment.setArguments(bundle);
//        }
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.novel_recommend_fragment_container, fragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .commit();
//    }
    private void openFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.novel_recommend_fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}
