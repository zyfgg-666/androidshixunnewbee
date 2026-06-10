package com.example.newbee2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.R;
import com.example.newbee2.model.Order;
import com.example.newbee2.model.OrderItem;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnStatusChangeListener onStatusChangeListener;

    public interface OnStatusChangeListener {
        void onStatusChanged();
    }

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.onStatusChangeListener = listener;
    }

    public void updateData(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderNo.setText("订单号: " + order.getOrderNo());
        holder.tvStatus.setText(order.getStatusText());
        holder.tvTotal.setText("合计: ¥" + (order.getTotalPrice() != null ? order.getTotalPrice() : 0));

        // 订单中的商品列表
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            OrderGoodsAdapter goodsAdapter = new OrderGoodsAdapter(context, order.getOrderItems());
            holder.rvGoods.setLayoutManager(new LinearLayoutManager(context));
            holder.rvGoods.setAdapter(goodsAdapter);
        }

        // 根据订单状态显示不同按钮
        if (order.getOrderStatus() != null) {
            switch (order.getOrderStatus()) {
                case 0: // 待支付
                    holder.tvAction.setVisibility(View.VISIBLE);
                    holder.tvAction.setText("立即支付");
                    holder.tvAction.setOnClickListener(v -> showPayDialog(order, holder));
                    break;
                case 3: // 待收货
                    holder.tvAction.setVisibility(View.VISIBLE);
                    holder.tvAction.setText("确认收货");
                    holder.tvAction.setOnClickListener(v -> finishOrder(order, holder));
                    break;
                default: // 待确认、待发货、已完成 - 不显示按钮
                    holder.tvAction.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.tvAction.setVisibility(View.GONE);
        }
    }

    private void showPayDialog(Order order, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择支付方式");

        // 自定义布局
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pay, null);
        builder.setView(dialogView);

        TextView tvWechat = dialogView.findViewById(R.id.tv_wechat);
        TextView tvAlipay = dialogView.findViewById(R.id.tv_alipay);

        AlertDialog dialog = builder.create();

        tvWechat.setOnClickListener(v -> {
            dialog.dismiss();
            payOrder(order, 1, holder); // 1=微信
        });

        tvAlipay.setOnClickListener(v -> {
            dialog.dismiss();
            payOrder(order, 2, holder); // 2=支付宝
        });

        dialog.show();
    }

    private void payOrder(Order order, int payType, ViewHolder holder) {
        // GET请求，包含orderNo和payType参数
        String url = HttpUtil.BASE_URL + "/paySuccess?orderNo=" + order.getOrderNo() + "&payType=" + payType;
        android.util.Log.d("OrderAdapter", "支付请求URL: " + url);

        HttpUtil.get(url, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                android.util.Log.d("OrderAdapter", "支付响应: " + data);
                if (data == null || data.isEmpty()) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "支付失败: 响应为空", Toast.LENGTH_SHORT).show());
                    }
                    return;
                }
                try {
                    Type type = new TypeToken<Result<Object>>(){}.getType();
                    Result<Object> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess()) {
                        order.setOrderStatus(2); // 2=已确认/待发货
                        if (context instanceof android.app.Activity) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                holder.tvAction.setVisibility(View.GONE);
                                holder.tvStatus.setText(order.getStatusText());
                                Toast.makeText(context, payType == 1 ? "微信支付成功" : "支付宝支付成功", Toast.LENGTH_SHORT).show();
                                if (onStatusChangeListener != null) {
                                    onStatusChangeListener.onStatusChanged();
                                }
                            });
                        }
                    } else {
                        String msg = result != null ? result.getMessage() : "未知错误";
                        if (context instanceof android.app.Activity) {
                            ((android.app.Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "支付失败: " + msg, Toast.LENGTH_SHORT).show());
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("OrderAdapter", "解析错误", e);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "支付失败: 数据解析错误", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("OrderAdapter", "支付错误: " + error);
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "支付失败: " + error, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void finishOrder(Order order, ViewHolder holder) {
        // 调用API完成订单：待收货 → 已完成
        String url = HttpUtil.BASE_URL + "/order/" + order.getOrderNo() + "/finish";
        android.util.Log.d("OrderAdapter", "确认收货URL: " + url);

        // PUT请求需要body，传空对象
        java.util.Map<String, String> emptyBody = new java.util.HashMap<>();
        HttpUtil.put(url, emptyBody, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                android.util.Log.d("OrderAdapter", "确认收货响应: " + data);
                if (data == null || data.isEmpty()) {
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "操作失败: 响应为空", Toast.LENGTH_SHORT).show());
                    }
                    return;
                }
                try {
                    Type type = new TypeToken<Result<Object>>(){}.getType();
                    Result<Object> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess()) {
                        order.setOrderStatus(4); // 4=已完成
                        if (context instanceof android.app.Activity) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                holder.tvAction.setVisibility(View.GONE);
                                holder.tvStatus.setText(order.getStatusText());
                                Toast.makeText(context, "确认收货成功", Toast.LENGTH_SHORT).show();
                                if (onStatusChangeListener != null) {
                                    onStatusChangeListener.onStatusChanged();
                                }
                            });
                        }
                    } else {
                        String msg = result != null ? result.getMessage() : "未知错误";
                        if (context instanceof android.app.Activity) {
                            ((android.app.Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "操作失败: " + msg, Toast.LENGTH_SHORT).show());
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("OrderAdapter", "解析错误", e);
                    if (context instanceof android.app.Activity) {
                        ((android.app.Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "操作失败: 数据解析错误", Toast.LENGTH_SHORT).show());
                    }
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("OrderAdapter", "确认收货错误: " + error);
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "操作失败: " + error, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNo, tvStatus, tvTotal, tvAction;
        RecyclerView rvGoods;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvAction = itemView.findViewById(R.id.tv_action);
            rvGoods = itemView.findViewById(R.id.rv_goods);
        }
    }
}
