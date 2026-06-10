package com.example.newbee2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newbee2.R;
import com.example.newbee2.model.Goods;
import com.example.newbee2.utils.ImageUtil;

import java.util.List;

public class GoodsAdapter extends BaseAdapter {

    private Context context;
    private List<Goods> goodsList;
    private LayoutInflater inflater;

    public GoodsAdapter(Context context, List<Goods> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Goods> newList) {
        this.goodsList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return goodsList != null ? goodsList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_goods_grid, parent, false);
            holder = new ViewHolder();
            holder.ivGoods = convertView.findViewById(R.id.iv_goods);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Goods goods = goodsList.get(position);
        holder.tvName.setText(goods.getGoodsName());
        holder.tvPrice.setText("¥" + (goods.getSellingPrice() != null ? goods.getSellingPrice() : 0));
        ImageUtil.loadImage(holder.ivGoods, goods.getGoodsCoverImg());

        return convertView;
    }

    static class ViewHolder {
        ImageView ivGoods;
        TextView tvName;
        TextView tvPrice;
    }
}
