package com.example.newbee2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.adapter.OrderGoodsAdapter;
import com.example.newbee2.model.CartItem;
import com.example.newbee2.model.OrderItem;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class CreateOrderActivity extends AppCompatActivity {

    private static final int REQUEST_ADDRESS = 100;

    private ImageView ivBack;
    private LinearLayout llAddress, llOrderNo;
    private TextView tvAddressName, tvAddressDetail, tvTotal, tvOrderNo;
    private RecyclerView rvOrder;
    private Button btnSubmit;
    private Long selectedAddressId = null;
    private List<CartItem> cartItems = new ArrayList<>();
    private java.util.Set<Long> selectedItemIds = new java.util.HashSet<>();
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        ivBack = findViewById(R.id.iv_back);
        llAddress = findViewById(R.id.ll_address);
        llOrderNo = findViewById(R.id.ll_order_no);
        tvAddressName = findViewById(R.id.tv_address_name);
        tvAddressDetail = findViewById(R.id.tv_address_detail);
        tvTotal = findViewById(R.id.tv_total);
        tvOrderNo = findViewById(R.id.tv_order_no);
        rvOrder = findViewById(R.id.rv_order);
        btnSubmit = findViewById(R.id.btn_submit);

        // 接收购物车传入的选中商品ID
        long[] ids = getIntent().getLongArrayExtra("selectedIds");
        if (ids != null) {
            for (long id : ids) {
                selectedItemIds.add(id);
            }
        }

        ivBack.setOnClickListener(v -> finish());

        llAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressListActivity.class);
            intent.putExtra("selectMode", true);
            startActivityForResult(intent, 100);
        });

        btnSubmit.setOnClickListener(v -> submitOrder());

        loadCartData();
        loadDefaultAddress();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedAddressId = data.getLongExtra("addressId", 0);
            tvAddressName.setText(data.getStringExtra("name"));
            tvAddressDetail.setText(data.getStringExtra("detail"));
        }
    }

    private void loadCartData() {
        HttpUtil.get(HttpUtil.BASE_URL + "/shop-cart", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<List<CartItem>>>(){}.getType();
                Result<List<CartItem>> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess() && result.getData() != null) {
                    cartItems.clear();
                    // 如果有选中ID列表，只保留选中的商品
                    if (!selectedItemIds.isEmpty()) {
                        for (CartItem ci : result.getData()) {
                            if (selectedItemIds.contains(ci.getCartItemId())) {
                                cartItems.add(ci);
                            }
                        }
                    } else {
                        cartItems.addAll(result.getData());
                    }

                    // 转换为OrderItem用于展示
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (CartItem ci : cartItems) {
                        OrderItem oi = new OrderItem();
                        oi.setGoodsId(ci.getGoodsId());
                        oi.setGoodsName(ci.getGoodsName());
                        oi.setGoodsCoverImg(ci.getGoodsCoverImg());
                        oi.setSellingPrice(ci.getSellingPrice());
                        oi.setGoodsCount(ci.getGoodsCount());
                        orderItems.add(oi);
                        totalPrice += (ci.getSellingPrice() != null ? ci.getSellingPrice() : 0) * ci.getGoodsCount();
                    }

                    OrderGoodsAdapter adapter = new OrderGoodsAdapter(CreateOrderActivity.this, orderItems);
                    rvOrder.setLayoutManager(new LinearLayoutManager(CreateOrderActivity.this));
                    rvOrder.setAdapter(adapter);

                    tvTotal.setText("合计: ¥" + totalPrice);
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    // 自动加载默认收货地址
    private void loadDefaultAddress() {
        HttpUtil.get(HttpUtil.BASE_URL + "/address",
                new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                try {
                    Type type = new TypeToken<Result<List<com.example.newbee2.model.Address>>>(){}.getType();
                    Result<List<com.example.newbee2.model.Address>> result =
                            HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        for (com.example.newbee2.model.Address addr : result.getData()) {
                            if (addr.getDefaultFlag() != null && addr.getDefaultFlag() == 1) {
                                runOnUiThread(() -> {
                                    selectedAddressId = addr.getAddressId();
                                    tvAddressName.setText(addr.getUserName());
                                    tvAddressDetail.setText(addr.getFullAddress());
                                });
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void submitOrder() {
        if (selectedAddressId == null) {
            Toast.makeText(this, "请选择收货地址", Toast.LENGTH_SHORT).show();
            return;
        }

        // 获取购物项id数组
        List<Long> cartItemIds = new ArrayList<>();
        for (CartItem item : cartItems) {
            cartItemIds.add(item.getCartItemId());
        }

        if (cartItemIds.isEmpty()) {
            Toast.makeText(this, "购物车为空", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("addressId", selectedAddressId);
        params.put("cartItemIds", cartItemIds);

        HttpUtil.postJson(HttpUtil.BASE_URL + "/saveOrder", params, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<String>>(){}.getType();
                Result<String> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess()) {
                    String orderNo = result.getData();
                    // 订单创建成功，显示订单号
                    runOnUiThread(() -> {
                        llOrderNo.setVisibility(View.VISIBLE);
                        tvOrderNo.setText(orderNo);
                        Toast.makeText(CreateOrderActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                        // 弹出支付选择
                        showPayDialog(orderNo);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CreateOrderActivity.this,
                            result != null ? result.getMessage() : "下单失败",
                            Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(CreateOrderActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showPayDialog(String orderNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择支付方式");
        builder.setCancelable(false);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pay, null);
        builder.setView(dialogView);

        TextView tvWechat = dialogView.findViewById(R.id.tv_wechat);
        TextView tvAlipay = dialogView.findViewById(R.id.tv_alipay);
        TextView tvLater = dialogView.findViewById(R.id.tv_later);

        AlertDialog dialog = builder.create();

        tvWechat.setOnClickListener(v -> {
            dialog.dismiss();
            payOrder(orderNo, 1);
        });

        tvAlipay.setOnClickListener(v -> {
            dialog.dismiss();
            payOrder(orderNo, 2);
        });

        // 稍后支付：不调用支付接口，直接跳转待支付列表
        tvLater.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(CreateOrderActivity.this,
                    "订单已保存，请尽快支付", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateOrderActivity.this,
                    OrderListActivity.class);
            intent.putExtra("status", 0); // 待支付
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void payOrder(String orderNo, int payType) {
        String url = HttpUtil.BASE_URL + "/paySuccess?orderNo=" + orderNo + "&payType=" + payType;

        HttpUtil.get(url, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<Object>>(){}.getType();
                Result<Object> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess()) {
                    runOnUiThread(() -> {
                        Toast.makeText(CreateOrderActivity.this,
                                payType == 1 ? "微信支付成功" : "支付宝支付成功",
                                Toast.LENGTH_SHORT).show();
                        // 跳转到待发货页面
                        Intent intent = new Intent(CreateOrderActivity.this, OrderListActivity.class);
                        intent.putExtra("status", 2); // 待发货
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        String msg = result != null ? result.getMessage() : "支付失败";
                        Toast.makeText(CreateOrderActivity.this, "支付失败: " + msg, Toast.LENGTH_SHORT).show();
                        // 支付失败也跳转到待支付页面
                        Intent intent = new Intent(CreateOrderActivity.this, OrderListActivity.class);
                        intent.putExtra("status", 0); // 待支付
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(CreateOrderActivity.this, "支付失败: " + error, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateOrderActivity.this, OrderListActivity.class);
                    intent.putExtra("status", 0); // 待支付
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}
