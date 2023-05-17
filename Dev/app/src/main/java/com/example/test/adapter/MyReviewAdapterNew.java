package com.example.test.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.MyReview;
import com.example.test.R;
import com.example.test.Review;

import java.util.List;

public class MyReviewAdapterNew extends RecyclerView.Adapter<MyReviewAdapterNew.ViewHolder> {
    private Context context;
    private List<MyReview> myReviews;
    private ReviewDeleteListener mListener;



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public Button mDeleteButton;
        private TextView bookTitle;
        private TextView bookAuthor;
        private ImageView bookCover;


        public ViewHolder(View itemView) {
            super(itemView);
            bookCover = itemView.findViewById(R.id.my_review_book_image);
            bookTitle = itemView.findViewById(R.id.my_review_book_title);
            bookAuthor = itemView.findViewById(R.id.my_review_book_author);
            mTextView = itemView.findViewById(R.id.my_review_book_review);

        }
    }

    public MyReviewAdapterNew(List<MyReview> items, Context context,ReviewDeleteListener listener) {
        myReviews = items;
        this.context = context;
        this.mListener = listener;
    }

    @Override
    public MyReviewAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_review_item, parent, false);
        MyReviewAdapterNew.ViewHolder viewHolder = new MyReviewAdapterNew.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyReviewAdapterNew.ViewHolder holder, int position) {
        final MyReview item = myReviews.get(position);
        holder.bookTitle.setText(item.getTitle());
        holder.bookAuthor.setText(item.getAuthor());
        holder.mTextView.setText(item.getReviewContent());
        Glide.with(context).load(item.getBookCover()).into(holder.bookCover);
        // 注册长按事件的监听器
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // 弹出删除对话框并询问用户是否确认删除
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("删除小说");
                builder.setMessage("确定要删除该评论吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户确认删除，将该项从列表中删除，并更新列表
                        myReviews.remove( holder.getAdapterPosition());
                        notifyItemRemoved( holder.getAdapterPosition());
                        // 将删除的数据通过 mListener 的 onReviewDeleted() 方法传递给相应的活动
                        if (mListener != null) {
                            mListener.onReviewDeleted(item);
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
        return myReviews.size();
    }
    public interface ReviewDeleteListener {
        void onReviewDeleted(MyReview review);
    }


}


