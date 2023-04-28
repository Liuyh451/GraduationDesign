package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.User;

import java.io.File;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> usersList;
    private OnUserClickListener onUserClickListener;

    public UserAdapter(List<User> usersList, OnUserClickListener onUserClickListener) {
        this.usersList = usersList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.users_item, parent, false);
        return new UserViewHolder(view, onUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface OnUserClickListener {
        //void onUserClick(int userId);2023年4月26日21点14分修改为下面那个，目的是为了把相关的值传过去
        void onUserClick(int userId, String username, String password, String avatarPath);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivUserAvatar;
        private TextView tvUserId, tvUserPassword;
        private OnUserClickListener onUserClickListener;
        private int userId;
        private String pwd;
        private String avatarPath;
        private String username;


        public UserViewHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);

            ivUserAvatar = itemView.findViewById(R.id.iv_alluser_avatar);
            tvUserId = itemView.findViewById(R.id.tv_alluser_id);
            tvUserPassword = itemView.findViewById(R.id.tv_alluser_password);
            this.onUserClickListener = onUserClickListener;
            itemView.setOnClickListener(this);
        }

        public void bind(User user) {
            // TODO: Load user avatar, for example using Glide
            userId = user.getUserId();
            //2023年4月26日21点14分修改为下面pwd和avatarPath两行，目的是为了把相关的值传过去
            pwd = user.getPassword();
            avatarPath = user.getAvatar();
            //增加上面的那两行
            username = user.getUsername();
            tvUserId.setText("Username: " + user.getUsername());
            tvUserPassword.setText("Password: " + user.getPassword());
            if (new File(avatarPath).exists()) {
                // 如果本地文件存在，则使用本地文件
                Bitmap bitmap = BitmapFactory.decodeFile(avatarPath);
                ivUserAvatar.setImageBitmap(bitmap);
            } else {
                Glide.with(itemView)
                        .load(user.getAvatar())  // 指定用户头像 URL
                        .placeholder(R.drawable.default_avatar)  // 设置占位图
                        .error(R.drawable.default_avatar)  // 设置加载错误时显示的图片
                        .into(ivUserAvatar);  // 将头像显示在 ImageView 中
            }
        }

        @Override
        public void onClick(View view) {
            //2023年4月26日21点14分修改,，目的是为了把相关的值传过去
            onUserClickListener.onUserClick(userId, username, tvUserPassword.getText().toString(), avatarPath);
            //onUserClickListener.onUserClick(userId);
        }
    }
}