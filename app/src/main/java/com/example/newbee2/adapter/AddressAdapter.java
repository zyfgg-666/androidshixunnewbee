package com.example.newbee2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.R;
import com.example.newbee2.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addressList;
    private OnAddressListener listener;
    private boolean selectMode = false;

    public interface OnAddressListener {
        void onEdit(Address address);
        void onDelete(Address address);
        void onSelect(Address address);
    }

    public AddressAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    public void setOnAddressListener(OnAddressListener listener) {
        this.listener = listener;
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
    }

    public void updateData(List<Address> newList) {
        this.addressList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addressList.get(position);

        holder.tvName.setText(address.getUserName());
        holder.tvPhone.setText(address.getUserPhone());
        holder.tvAddress.setText(address.getFullAddress());
        holder.cbDefault.setChecked(address.getDefaultFlag() == 1);

        if (selectMode) {
            // 选择模式：隐藏编辑删除，点击整个item选择
            holder.tvEdit.setVisibility(View.GONE);
            holder.tvDelete.setVisibility(View.GONE);
            holder.cbDefault.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSelect(address);
            });
        } else {
            // 普通模式：显示编辑删除
            holder.tvEdit.setVisibility(View.VISIBLE);
            holder.tvDelete.setVisibility(View.VISIBLE);
            holder.cbDefault.setVisibility(View.VISIBLE);
            holder.tvEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(address);
            });
            holder.tvDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(address);
            });
        }
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress, tvEdit, tvDelete;
        CheckBox cbDefault;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvEdit = itemView.findViewById(R.id.tv_edit);
            tvDelete = itemView.findViewById(R.id.tv_delete);
            cbDefault = itemView.findViewById(R.id.cb_default);
        }
    }
}
