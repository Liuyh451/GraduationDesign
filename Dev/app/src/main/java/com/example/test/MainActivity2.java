package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        frg_def();
    }
    /**
     * 默认第一个页面，为主页面
     */
    public void  frg_def(){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Frag_1 f1=new Frag_1();
        ft.replace(R.id.lv,f1);
        ft.commit();
    }
    /**
     * 点击底部按钮时进行Fragment切换
     *
     * @param v   组件
     */
    public void switch_frg(View v){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        Frag_1 f1=new Frag_1();
        Frag_2 f2=new Frag_2();
        Frag_3 f3=new Frag_3();
        switch (v.getId()){
            case R.id.bt1:
                ft.replace(R.id.lv,f1);
                break;
            case  R.id.bt2:
                ft.replace(R.id.lv,f2);
                break;
            case R.id.bt3:
                ft.replace(R.id.lv,f3);
                break;
        }
        ft.commit();
    }
}

