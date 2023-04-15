package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Book> books;

    public BookGridAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_book_grid, null);
            viewHolder = new ViewHolder();
            viewHolder.cover = view.findViewById(R.id.book_cover);
            viewHolder.title = view.findViewById(R.id.book_title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 显示图书封面和标题
        Glide.with(context)
                .load(books.get(position).getCoverUrl())
                .into(viewHolder.cover);

        viewHolder.title.setText(books.get(position).getTitle());

        return view;
    }

    static class ViewHolder {
        ImageView cover;
        TextView title;
    }

}
