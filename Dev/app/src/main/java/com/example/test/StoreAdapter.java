package com.example.test;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.test.bean.CarResponse;
import com.example.test.util.GoodsCallback;


import java.util.List;

/**
 * 店铺适配器
 *
 * @author llw
 */
public class StoreAdapter extends BaseQuickAdapter<CarResponse.OrderDataBean, BaseViewHolder> {

    private RecyclerView rvGood;
    private Context mContext;
    //商品回调
    private GoodsCallback goodsCallback;
    //店铺对象
    private List<CarResponse.OrderDataBean> storeBean;


    public StoreAdapter(int layoutResId,Context context, GoodsCallback goodsCallback, @Nullable List<CarResponse.OrderDataBean> data) {
        super(layoutResId, data);
        mContext=context;
        storeBean=data;
        this.goodsCallback=goodsCallback;

    }

    @Override
    protected void convert(BaseViewHolder helper, CarResponse.OrderDataBean item) {
        rvGood = helper.getView(R.id.rv_goods);
        helper.setText(R.id.tv_store_name,item.getShopName());

        final GoodsAdapter goodsAdapter = new GoodsAdapter(R.layout.item_good,mContext,item.getCartlist());
        rvGood.setLayoutManager(new LinearLayoutManager(mContext));
        goodsAdapter.addChildClickViewIds(R.id.iv_checked_goods,R.id.tv_increase_goods_num,R.id.tv_reduce_goods_num);
        rvGood.setAdapter(goodsAdapter);
        //商品item中的点击事件
        goodsAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CarResponse.OrderDataBean.CartlistBean goodsBean = item.getCartlist().get(position);

                switch (view.getId()) {
                    case R.id.iv_checked_goods://选中商品
                        //如果已选中则取消选中，未选中则选中
                        goodsBean.setChecked(!goodsBean.isChecked());
                        //刷新适配器
                        goodsAdapter.notifyDataSetChanged();
                        //控制店铺是否选中
                        controlStore(item);
                        //商品控制价格
                        goodsCallback.calculationPrice();
                        break;
                    case R.id.tv_increase_goods_num://增加商品数量
                        updateGoodsNum(goodsBean, goodsAdapter,true);
                        break;
                    case R.id.tv_reduce_goods_num://减少商品数量
                        updateGoodsNum(goodsBean, goodsAdapter,false);

                        break;
                    default:
                        break;
                }
            }
        });

    }
    /**
     * 控制商品是否选中
     */
    public void controlGoods(int shopId, boolean state) {
        //根据店铺id选中该店铺下所有商品
        for (CarResponse.OrderDataBean orderDataBean : storeBean) {
            //店铺id等于传递过来的店铺id  则选中该店铺下所有商品
            if (orderDataBean.getShopId() == shopId) {
                for (CarResponse.OrderDataBean.CartlistBean cartlistBean : orderDataBean.getCartlist()) {
                    cartlistBean.setChecked(state);
                    //刷新
                    notifyDataSetChanged();
                }
            }
        }
    }	/**
     * 控制店铺是否选中
     */
    private void controlStore(CarResponse.OrderDataBean item) {
        int num = 0;
        for (CarResponse.OrderDataBean.CartlistBean bean : item.getCartlist()) {
            if (bean.isChecked()) {
                ++num;
            }
        }
        if (num == item.getCartlist().size()) {
            //全选中  传递需要选中的店铺的id过去
            goodsCallback.checkedStore(item.getShopId(),true);
        } else {
            goodsCallback.checkedStore(item.getShopId(),false);
        }
    }
    /**
     * 修改商品数量  增加或者减少
     * @param goodsBean
     * @param goodsAdapter
     * @param state  true增加 false减少
     */
    private void updateGoodsNum(CarResponse.OrderDataBean.CartlistBean goodsBean, GoodsAdapter goodsAdapter,boolean state) {
        //其实商品应该还有一个库存值或者其他的限定值，我这里写一个假的库存值为10
        int inventory = 10;
        int count = goodsBean.getCount();

        if(state){
            if (count >= inventory){
                Toast.makeText(mContext,"商品数量不可超过库存值~",Toast.LENGTH_SHORT).show();
                return;
            }
            count++;
        }else {
            if (count <= 1){
                Toast.makeText(mContext,"已是最低商品数量~",Toast.LENGTH_SHORT).show();
                return;
            }
            count--;
        }
        goodsBean.setCount(count);//设置商品数量
        //刷新适配器
        goodsAdapter.notifyDataSetChanged();
        //计算商品价格
        goodsCallback.calculationPrice();
    }

}