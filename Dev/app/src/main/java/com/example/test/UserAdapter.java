package com.example.test;

import android.content.Context;
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
        void onUserClick(int userId);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivUserAvatar;
        private TextView tvUserId, tvUserPassword;
        private OnUserClickListener onUserClickListener;
        private int userId;

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
            tvUserId.setText("Username: " + user.getUsername());
            tvUserPassword.setText("Password: " + user.getPassword());
            Glide.with(itemView)
                    .load(user.getAvatar())  // 指定用户头像 URL
                    .placeholder(R.drawable.default_avatar)  // 设置占位图
                    .error(R.drawable.default_avatar)  // 设置加载错误时显示的图片
                    .into(ivUserAvatar);  // 将头像显示在 ImageView 中
        }

        @Override
        public void onClick(View view) {
            onUserClickListener.onUserClick(userId);
        }
    }
}