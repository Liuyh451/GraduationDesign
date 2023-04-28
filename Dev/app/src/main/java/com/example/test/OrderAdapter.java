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
    private OrderAdapter.OnOrderClickListener OnOrderClickListener;

    public OrderAdapter(List<Order> ordersList, OrderAdapter.OnOrderClickListener onUserClickListener) {
        this.ordersList = ordersList;
        this.OnOrderClickListener = OnOrderClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view, OnOrderClickListener);
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
        void OnOrderClick(int orderId);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvOrderId, tvOrderBookAuthor, tvOrderBookTitle, tvOrderBuyerName;
        private TextView tvOrderBuyerQuantity, tvOrderBuyerAddress, tvOrderBuyerPhone;
        private ImageView tvOrderBookCover;
        private OnOrderClickListener onOrderClickListener;
        private int orderId;

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
            tvOrderId.setText("Order ID: " + order.getOrderNumber());
            //todo 用glide获取封面
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
            onOrderClickListener.OnOrderClick(orderId);
        }
    }
}