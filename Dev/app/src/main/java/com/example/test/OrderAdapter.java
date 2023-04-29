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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> ordersList;
    private OnOrderClickListener onOrderClickListener;

    public OrderAdapter(List<Order> ordersList, OnOrderClickListener onOrderClickListener) {
        this.ordersList = ordersList;
        this.onOrderClickListener = onOrderClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view, onOrderClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = ordersList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public interface OnOrderClickListener {
        //2023年4月30日00点20分修改,，目的是为了把相关的值传过去
        void OnOrderClick(int orderId, String bookCover, String title, String author, String buyerQuantity, String buyerName, String price, String address, String phone);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvOrderId, tvOrderBookAuthor, tvOrderBookTitle, tvOrderBuyerName;
        private TextView tvOrderBuyerQuantity, tvOrderBuyerAddress, tvOrderBuyerPhone;
        private ImageView tvOrderBookCover;
        private OnOrderClickListener onOrderClickListener;
        private int orderId;
        private String bookCover;
        private String author;
        private String title;
        private String price;
        private String phone;
        private String address;
        private String buyerName;
        private String buyerQuantity;

        public OrderViewHolder(@NonNull View itemView, OnOrderClickListener onOrderClickListener) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderBookCover = itemView.findViewById(R.id.book_cover_order);
            tvOrderBookAuthor = itemView.findViewById(R.id.book_author_order);
            tvOrderBookTitle = itemView.findViewById(R.id.book_title_order);
            tvOrderBuyerName = itemView.findViewById(R.id.buyer_name);
            tvOrderBuyerQuantity = itemView.findViewById(R.id.buyer_quantity);
            tvOrderBuyerAddress = itemView.findViewById(R.id.buyer_address);
            tvOrderBuyerPhone = itemView.findViewById(R.id.buyer_phone);
            //tvOrderDetails = itemView.findViewById(R.id.tv_order_details);
            //todo 设计用户的信息电话等
            this.onOrderClickListener = onOrderClickListener;
            itemView.setOnClickListener(this);
        }

        public void bind(Order order) {
            orderId = order.getOrderNumber();
            bookCover = order.getBookCover();
            title = order.getBookTitle();
            author = order.getBookAuthor();
            price = Double.toString(order.getPrice());
            address = order.getAddress();
            buyerName = order.getBuyerName();
            phone = order.getPhone();
            buyerQuantity = Integer.toString(order.getQuantity());
            //2023年4月30日00点20分修改,，目的是为了把相关的值传过去
            tvOrderId.setText("Order ID: " + order.getOrderNumber());
            tvOrderBookAuthor.setText("作者" + order.getBookAuthor());
            tvOrderBookTitle.setText("标题" + order.getBookTitle());
            tvOrderBuyerName.setText("购买者" + order.getBuyerName());
            tvOrderBuyerQuantity.setText("数量" + order.getQuantity());
            tvOrderBuyerAddress.setText("地址" + order.getAddress());
            tvOrderBuyerPhone.setText("电话" + order.getPhone());
            //todo 价格
            //tvOrderDetails.setText("Order Details: " + order.getDetails());
            Glide.with(itemView)
                    .load(order.getBookCover())  // 指定书籍封面 URL
                    .placeholder(R.drawable.bookerror)  // 设置占位图
                    .error(R.drawable.bookerror)  // 设置加载错误时显示的图片
                    .into(tvOrderBookCover);  // 将封面图片显示在 ImageView 中
        }

        @Override
        public void onClick(View view) {
            //2023年4月30日00点20分修改,，目的是为了把相关的值传过去
            onOrderClickListener.OnOrderClick(orderId, bookCover, title, author, buyerQuantity, buyerName, price, address, phone);
        }
    }
}