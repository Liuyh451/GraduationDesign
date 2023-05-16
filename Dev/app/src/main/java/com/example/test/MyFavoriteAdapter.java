package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.ViewHolder> {
    private List<Novel> novels;
    private Context context;
    private OnItemClickListener listener;

    public MyFavoriteAdapter(Context context, List<Novel> novels) {
        this.context = context;
        this.novels = novels;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Novel novel = novels.get(position);
        holder.title.setText(novel.getTitle());
        holder.author.setText(novel.getAuthor());
        holder.rating.setText(novel.getDescription());
        holder.date.setText(novel.getNovelId());
        Glide.with(context).load(novel.getImageUrl()).into(holder.image);
        // 设置itemView的点击监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // 调用接口方法，通知Activity跳转到目标Activity
                    listener.onItemClick(novel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return novels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView author;
        TextView rating;
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.novel_image);
            title = itemView.findViewById(R.id.novel_title);
            author = itemView.findViewById(R.id.novel_author);
            rating = itemView.findViewById(R.id.novel_rating);
            date = itemView.findViewById(R.id.novel_date);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Novel novel);
    }

}
