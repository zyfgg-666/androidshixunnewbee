package com.example.newbee2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.R;
import com.example.newbee2.model.OrderItem;
import com.example.newbee2.utils.ImageUtil;

import java.util.List;

public class OrderGoodsAdapter extends RecyclerView.Adapter<OrderGoodsAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> itemList;

    public OrderGoodsAdapter(Context context, List<OrderItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_goods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = itemList.get(position);
        holder.tvName.setText(item.getGoodsName());
        holder.tvPrice.setText("¥" + item.getSellingPrice());
        holder.tvCount.setText("x" + item.getGoodsCount());
        ImageUtil.loadImage(holder.ivGoods, item.getGoodsCoverImg());
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoods;
        TextView tvName, tvPrice, tvCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGoods = itemView.findViewById(R.id.iv_goods);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
        }
    }
}
