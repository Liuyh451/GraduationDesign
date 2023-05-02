package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ViewHolder> {
    private Context context;
    private List<MyReview> myReviews;


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
            mDeleteButton = itemView.findViewById(R.id.review_delete_button);
        }
    }

    public MyReviewAdapter(List<MyReview> items, Context context) {
        myReviews = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyReview item = myReviews.get(position);
        holder.bookTitle.setText(item.getTitle());
        holder.bookAuthor.setText(item.getAuthor());
        holder.mTextView.setText(item.getReviewContent());
        Glide.with(context).load(item.getBookCover()).into(holder.bookCover);
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item.
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myReviews.size();
    }

    public void remove(int position) {
        myReviews.remove(position);  // 从数据集合中移除指定位置的项

        // 通知 RecyclerView 刷新列表
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

}

