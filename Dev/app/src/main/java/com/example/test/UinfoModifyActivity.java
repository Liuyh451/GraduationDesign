package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
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


public class UinfoModifyActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private TextView addressResulTv;
    private TextView nameResulTv;
    private TextView phoneResulTv;
    private TextView genderResulTv;
    private TextView infoSaveTv;
    private TextView passwordTV;
    private ImageView addressArrowIv;
    private ImageView phoneArrowIv;
    private ImageView nameArrowIv;
    private ImageView userAvatarIv;
    private ImageView genderArrowIv;
    private ImageView passWordArrowIv;
    private ImageView backArrowIv;
    private  int mSelectedGenderIndex=0;
    private String[] mGenders = {"男", "女"};
    private String userAddress;
    private String userGender;
    private String userPhone;
    private String userName;
    private  String passWord;
    private String userAvatar;
    private String filePath;
    private String Uid = GlobalVariable.uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uinfo_modify);
        addressArrowIv=findViewById(R.id.address_right_iv);
        phoneArrowIv=findViewById(R.id.phone_right_iv);
        nameArrowIv=findViewById(R.id.name_right_iv);
        addressResulTv=findViewById(R.id.address_edt);
        nameResulTv=findViewById(R.id.name_content_edt);
        genderResulTv=findViewById(R.id.gender_edt);
        phoneResulTv=findViewById(R.id.phone_edt);
        userAvatarIv=findViewById(R.id.image_view_avatar);
        genderArrowIv=findViewById(R.id.gender_right_iv);
        infoSaveTv=findViewById(R.id.tv_forward);
        // 获取Intent对象
        Intent intent = getIntent();
        userAvatar = getIntent().getStringExtra("avatar");
        // 获取传递的数据
        userPhone = intent.getStringExtra("phone");
        userAddress = intent.getStringExtra("address");
        userGender = intent.getStringExtra("gender");
        passWord = intent.getStringExtra("password");
        String nickName = intent.getStringExtra("nickname");
        passwordTV= findViewById(R.id.password_content_edt);
        passWordArrowIv=findViewById(R.id.password_right_iv);
        backArrowIv=findViewById(R.id.iv_backward);
        if(userPhone!=null){
            phoneResulTv.setText(userPhone);
        }
        if(userAddress!=null){
            addressResulTv.setText(userAddress);
        }
        if(passWord!=null){
            passwordTV.setText(passWord);
        }
        if(userGender!=null){
            genderResulTv.setText(userGender);
        }
        if(nickName!=null){
            nameResulTv.setText(nickName);
        }
        filePath=userAvatar;
        Context context=this;
        if (new File(userAvatar).exists()) {
            // 如果本地文件存在，则使用本地文件
            Bitmap bitmap = BitmapFactory.decodeFile(userAvatar);
            userAvatarIv.setImageBitmap(bitmap);
        } else {
            // 如果本地文件不存在，则使用 Glide 进行网络加载
            Glide.with(this).load(userAvatar).into(userAvatarIv);
        }
        //设置头像修改监听
        userAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAvatarClicked(userAvatarIv);
            }
        });
        //设置密码修改监听
        passWordArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogToUpdateValue(passwordTV);
            }
        });
        //设置性别修改监听
        genderArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderPickerDialog(genderResulTv);
            }
        });
        //设置地址修改监听
        addressArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress();
            }
        });
        //设置手机修改监听
        phoneArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToUpdateValue(phoneResulTv);
            }
        });
        //设置姓名修改监听
        nameArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogToUpdateValue(nameResulTv);
            }
        });
        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置保存监听

        infoSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName=nameResulTv.getText().toString();
                passWord=passwordTV.getText().toString();
                userPhone=phoneResulTv.getText().toString();
                userGender=genderResulTv.getText().toString();
                NetUnit.userInfoModify(context, Uid,userName, userPhone,passWord, filePath, userAddress,userGender, new Response.Listener<String>() {
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
        });
    }
    public void showDialogToUpdateValue(final TextView textView) {

        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入值");

        // 创建一个EditText对象用于输入值
        final EditText editText = new EditText(this);
        builder.setView(editText);

        // 设置对话框的“确定”按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取输入的值
                String value = editText.getText().toString().trim();

                // 在这里根据需要进行值的处理，例如更新组件的值
                // 以下是一个示例，将值显示在TextView组件上
                textView.setText(value);
                Log.d("UserModify",value);
            }
        });

        // 设置对话框的“取消”按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 取消操作，不需要执行任何操作
            }
        });

        // 显示对话框
        builder.show();

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
                addressResulTv.setText(province.getName() + "(" + province.getId() + ")\n"
                        + city.getName() + "(" + city.getId() + ")\n"
                        + district.getName() + "(" + district.getId() + ")");
                userAddress=province.getName()+city.getName()+district.getName();
                Log.d("UserModify",userAddress);
            }

            @Override
            public void onCancel() {
            }
        });
        cityPicker.showCityPicker();

    }
    private void showGenderPickerDialog(final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择性别");

        builder.setSingleChoiceItems(mGenders, mSelectedGenderIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedGenderIndex = i;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textView.setText(mGenders[mSelectedGenderIndex]);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
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
                    userAvatarIv.setImageBitmap(bitmap);
                } else {
                    // 如果本地文件不存在，则使用 Glide 进行网络加载
                    Glide.with(this).load(filePath).into(userAvatarIv);
                }

                //avatarImageView.setImageURI(selectedImageUri);
            }
        }
    }
}