package com.example.newbee2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbee2.model.Goods;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.example.newbee2.utils.ImageUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private ImageView ivBack, ivGoods, ivCart;
    private TextView tvName, tvIntro, tvPrice, tvDetail, tvCartBadge;
    private Button btnAddCart, btnBuy;
    private Long goodsId;
    private Goods currentGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_detail);

            goodsId = getIntent().getLongExtra("goodsId", 0);
            Log.d(TAG, "goodsId: " + goodsId);

            ivBack = findViewById(R.id.iv_back);
            ivGoods = findViewById(R.id.iv_goods);
            ivCart = findViewById(R.id.iv_cart);
            tvCartBadge = findViewById(R.id.tv_cart_badge);
            tvName = findViewById(R.id.tv_name);
            tvIntro = findViewById(R.id.tv_intro);
            tvPrice = findViewById(R.id.tv_price);
            tvDetail = findViewById(R.id.tv_detail);
            btnAddCart = findViewById(R.id.btn_add_cart);
            btnBuy = findViewById(R.id.btn_buy);

            ivBack.setOnClickListener(v -> finish());

            ivCart.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("tab", 2);
                startActivity(intent);
            });

            btnAddCart.setOnClickListener(v -> addToCart());
            btnBuy.setOnClickListener(v -> buyNow());

            loadGoodsDetail();
            loadCartBadge();
        } catch (Exception e) {
            Log.e(TAG, "onCreate error: " + e.getMessage(), e);
            Toast.makeText(this, "初始化错误: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartBadge();
    }

    private void loadGoodsDetail() {
        HttpUtil.get(HttpUtil.BASE_URL + "/goods/detail/" + goodsId, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Log.d(TAG, "Response: " + data);
                try {
                    Type type = new TypeToken<Result<Goods>>(){}.getType();
                    Result<Goods> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        currentGoods = result.getData();
                        runOnUiThread(() -> showGoodsInfo());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DetailActivity.this,
                                result != null ? result.getMessage() : "加载失败",
                                Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parse error: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(DetailActivity.this,
                            "解析错误: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Request error: " + error);
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "加载失败: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showGoodsInfo() {
        if (currentGoods == null) return;
        tvName.setText(currentGoods.getGoodsName());
        tvIntro.setText(currentGoods.getGoodsIntro());
        tvPrice.setText("¥" + (currentGoods.getSellingPrice() != null ? currentGoods.getSellingPrice() : 0));
        tvDetail.setText(currentGoods.getGoodsIntro());
        ImageUtil.loadImage(ivGoods, currentGoods.getGoodsCoverImg());
    }

    private void loadCartBadge() {
        HttpUtil.get(HttpUtil.BASE_URL + "/shop-cart", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result<java.util.List<java.util.Map<String, Object>>>>(){}.getType();
                    Result<java.util.List<java.util.Map<String, Object>>> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        int count = result.getData().size();
                        runOnUiThread(() -> {
                            if (count > 0) {
                                tvCartBadge.setVisibility(View.VISIBLE);
                                tvCartBadge.setText(String.valueOf(count));
                            } else {
                                tvCartBadge.setVisibility(View.GONE);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Badge parse error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void addToCart() {
        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        String token = info.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("goodsCount", 1);
        params.put("goodsId", goodsId);

        HttpUtil.postJson(HttpUtil.BASE_URL + "/shop-cart", params, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result>(){}.getType();
                Result result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess()) {
                    Toast.makeText(DetailActivity.this, "已加入购物车", Toast.LENGTH_SHORT).show();
                    // 刷新购物车红点
                    MainActivity.loadCartCountStatic(DetailActivity.this);
                    // 刷新本页红点
                    loadCartBadge();
                } else {
                    Toast.makeText(DetailActivity.this,
                            result != null ? result.getMessage() : "添加失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buyNow() {
        SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
        String token = info.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        // 先添加到购物车，然后跳转到创建订单页面
        Map<String, Object> params = new HashMap<>();
        params.put("goodsCount", 1);
        params.put("goodsId", goodsId);

        HttpUtil.postJson(HttpUtil.BASE_URL + "/shop-cart", params, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result>(){}.getType();
                Result result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess()) {
                    // 添加成功后跳转到创建订单页面
                    runOnUiThread(() -> {
                        Intent intent = new Intent(DetailActivity.this, CreateOrderActivity.class);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(DetailActivity.this,
                            result != null ? result.getMessage() : "添加失败",
                            Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
