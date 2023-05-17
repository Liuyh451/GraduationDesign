package com.example.test.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.MyReview;
import com.example.test.Novel;
import com.example.test.R;

import java.util.List;

public class MyFavoriteAdapterNew extends RecyclerView.Adapter<MyFavoriteAdapterNew.ViewHolder> {

    private List<Novel> novels;
    private Context context;
    private OnItemClickListener listener;
    private FavoriteDeleteListener mListener;

    public MyFavoriteAdapterNew(Context context, List<Novel> novels, FavoriteDeleteListener listener) {
        this.context = context;
        this.novels = novels;
        this.mListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.novel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Novel novel = novels.get(position);
        holder.title.setText(novel.getTitle());
        holder.author.setText(novel.getAuthor());
        holder.description.setText(novel.getDescription());
        Glide.with(context).load(novel.getImageUrl()).into(holder.image);
        // 注册长按事件的监听器
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 弹出删除对话框并询问用户是否确认删除
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("删除小说");
                builder.setMessage("确定要删除该收藏吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户确认删除，将该项从列表中删除，并更新列表
                        novels.remove( holder.getAdapterPosition());
                        notifyItemRemoved( holder.getAdapterPosition());
                        if (mListener != null) {
                            mListener.onFavoriteDeleted(novel);
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                return true;
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
        TextView description;
        RecyclerView mRecyclerView;
        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.novel_image);
            title = itemView.findViewById(R.id.novel_title);
            author = itemView.findViewById(R.id.novel_author);
            description = itemView.findViewById(R.id.novel_description);
            mRecyclerView = itemView.findViewById(R.id.novel_list_favorite);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Novel novel);
    }
    public interface FavoriteDeleteListener {
        void onFavoriteDeleted(Novel novel);
    }

}
