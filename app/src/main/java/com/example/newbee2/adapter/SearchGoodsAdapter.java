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
import com.example.newbee2.model.Goods;
import com.example.newbee2.utils.ImageUtil;

import java.util.List;

public class SearchGoodsAdapter extends RecyclerView.Adapter<SearchGoodsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Goods goods);
    }

    private Context context;
    private List<Goods> goodsList;
    private OnItemClickListener listener;

    public SearchGoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.tvName.setText(goods.getGoodsName());
        holder.tvPrice.setText("¥" + (goods.getSellingPrice() != null ? goods.getSellingPrice() : 0));
        ImageUtil.loadImage(holder.ivGoods, goods.getGoodsCoverImg());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(goods);
        });
    }

    @Override
    public int getItemCount() {
        return goodsList != null ? goodsList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoods;
        TextView tvName, tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGoods = itemView.findViewById(R.id.iv_goods);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
