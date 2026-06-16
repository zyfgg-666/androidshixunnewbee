package com.example.newbee2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.R;
import com.example.newbee2.model.CartItem;
import com.example.newbee2.utils.ImageUtil;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onSelectChange();
        void onCountChange(CartItem item, int newCount);
        void onDelete(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public void updateData(List<CartItem> newList) {
        this.cartList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.getGoodsName());
        holder.tvPrice.setText("¥" + item.getSellingPrice());
        holder.tvCount.setText(String.valueOf(item.getGoodsCount()));
        holder.cbSelect.setChecked(item.isSelected());

        ImageUtil.loadImage(holder.ivGoods, item.getGoodsCoverImg());

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (listener != null) listener.onSelectChange();
        });

        holder.btnMinus.setOnClickListener(v -> {
            int count = item.getGoodsCount();
            if (count > 1) {
                item.setGoodsCount(count - 1);
                holder.tvCount.setText(String.valueOf(count - 1));
                if (listener != null) listener.onCountChange(item, count - 1);
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int count = item.getGoodsCount();
            item.setGoodsCount(count + 1);
            holder.tvCount.setText(String.valueOf(count + 1));
            if (listener != null) listener.onCountChange(item, count + 1);
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivGoods;
        TextView tvName, tvPrice, tvCount;
        TextView btnMinus, btnPlus;
        ImageView ivDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cb_select);
            ivGoods = itemView.findViewById(R.id.iv_goods);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}
