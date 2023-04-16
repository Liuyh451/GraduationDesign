package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Books> bookList;
    private OnBookClickListener onBookClickListener;

    public BookAdapter(List<Books> bookList, OnBookClickListener onBookClickListener) {
        this.bookList = bookList;
        this.onBookClickListener = onBookClickListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view, onBookClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Books book = bookList.get(position);
        holder.bind(book);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public interface OnBookClickListener {
        void onBookClick(int bookId);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivBookCover;
        private TextView tvBookTitle, tvBookAuthor;
        private OnBookClickListener onBookClickListener;
        private int bookId;

        public BookViewHolder(@NonNull View itemView, OnBookClickListener onBookClickListener) {
            super(itemView);

            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            this.onBookClickListener = onBookClickListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Books book) {
            bookId = book.getBookId();
            tvBookTitle.setText(book.getTitle());
            tvBookAuthor.setText(book.getAuthor());
            Glide.with(itemView)
                    .load(book.getCoverUrl())  // 指定书籍封面 URL
                    .placeholder(R.drawable.bookerror)  // 设置占位图
                    .error(R.drawable.bookerror)  // 设置加载错误时显示的图片
                    .into(ivBookCover);  // 将封面图片显示在 ImageView 中
        }

        @Override
        public void onClick(View view) {
            onBookClickListener.onBookClick(bookId);
        }
    }
}
