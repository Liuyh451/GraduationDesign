package com.example.test;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.test.bean.CarResponse;


import java.util.List;

/**
 * 商品适配器
 * @author llw
 */
public class GoodsAdapter extends BaseQuickAdapter<CarResponse.OrderDataBean.CartlistBean, BaseViewHolder> {
    private Context mContext;

    public GoodsAdapter(int layoutResId, Context context, @Nullable List<CarResponse.OrderDataBean.CartlistBean> data) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CarResponse.OrderDataBean.CartlistBean item) {
        helper.setText(R.id.tv_good_name, item.getProductName())
                .setText(R.id.tv_good_color,item.getColor())
                .setText(R.id.tv_good_size,item.getSize())
                .setText(R.id.tv_goods_price,item.getPrice()+"")
                .setText(R.id.tv_goods_num,item.getCount()+"");
        ImageView goodImg = helper.getView(R.id.iv_goods);
        Glide.with(mContext).load(item.getDefaultPic()).into(goodImg);
        ImageView checkedGoods = helper.getView(R.id.iv_checked_goods);
        //判断商品是否选中
        if (item.isChecked()) {
            checkedGoods.setImageDrawable(mContext.getDrawable(R.drawable.ic_checked));
        } else {
            checkedGoods.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_diy));
        }
        //添加点击事件


    }
}