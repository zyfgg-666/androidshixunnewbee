package com.example.newbee2.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.CreateOrderActivity;
import com.example.newbee2.LoginActivity;
import com.example.newbee2.R;
import com.example.newbee2.adapter.CartAdapter;
import com.example.newbee2.model.CartItem;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private LinearLayout llEmpty;
    private TextView tvTotal;
    private CheckBox cbAll;
    private Button btnSettle, btnGoHome;
    private LinearLayout llBottom;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rv_cart);
        llEmpty = view.findViewById(R.id.tv_empty);
        tvTotal = view.findViewById(R.id.tv_total);
        cbAll = view.findViewById(R.id.cb_all);
        btnSettle = view.findViewById(R.id.btn_settle);
        btnGoHome = view.findViewById(R.id.btn_go_home);
        llBottom = view.findViewById(R.id.ll_bottom);

        // 空购物车"快去选购"按钮 → 跳转首页
        btnGoHome.setOnClickListener(v -> {
            if (getActivity() != null) {
                ((com.example.newbee2.MainActivity) getActivity()).switchToTab(0);
            }
        });

        cartAdapter = new CartAdapter(getActivity(), cartList);
        rvCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCart.setAdapter(cartAdapter);

        cartAdapter.setOnCartChangeListener(new CartAdapter.OnCartChangeListener() {
            @Override
            public void onSelectChange() {
                updateTotal();
                checkAllSelect();
            }

            @Override
            public void onCountChange(CartItem item, int newCount) {
                updateCartItem(item);
            }

            @Override
            public void onDelete(CartItem item) {
                deleteCartItem(item);
            }
        });

        // 全选
        cbAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartList) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotal();
        });

        // 结算
        btnSettle.setOnClickListener(v -> {
            List<CartItem> selected = getSelectedItems();
            if (selected.isEmpty()) {
                Toast.makeText(getActivity(), "请选择商品", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
            startActivity(intent);
        });

        // 默认隐藏
        rvCart.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData();
        // 刷新购物车红点
        com.example.newbee2.MainActivity.loadCartCountStatic(getActivity());
    }

    private void loadCartData() {
        if (getActivity() == null) return;
        SharedPreferences info = getActivity().getSharedPreferences("info", 0);
        String token = info.getString("token", "");
        if (token.isEmpty()) {
            rvCart.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
            llBottom.setVisibility(View.GONE);
            return;
        }

        HttpUtil.get(HttpUtil.BASE_URL + "/shop-cart", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                if (getActivity() == null) return;
                try {
                    Type type = new TypeToken<Result<List<CartItem>>>(){}.getType();
                    Result<List<CartItem>> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        cartList.clear();
                        cartList.addAll(result.getData());
                        for (CartItem item : cartList) {
                            item.setSelected(true);
                        }
                        cartAdapter.notifyDataSetChanged();

                        getActivity().runOnUiThread(() -> {
                            if (cartList.isEmpty()) {
                                rvCart.setVisibility(View.GONE);
                                llEmpty.setVisibility(View.VISIBLE);
                                llBottom.setVisibility(View.GONE);
                            } else {
                                rvCart.setVisibility(View.VISIBLE);
                                llEmpty.setVisibility(View.GONE);
                                llBottom.setVisibility(View.VISIBLE);
                                updateTotal();
                            }
                        });
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

    private void updateCartItem(CartItem item) {
        Map<String, Object> params = new HashMap<>();
        params.put("cartItemId", item.getCartItemId());
        params.put("goodsCount", item.getGoodsCount());
        HttpUtil.postJson(HttpUtil.BASE_URL + "/shop-cart/update", params, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                updateTotal();
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void deleteCartItem(CartItem item) {
        HttpUtil.delete(HttpUtil.BASE_URL + "/shop-cart/" + item.getCartItemId(), new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result>(){}.getType();
                    Result result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess()) {
                        cartList.remove(item);
                        cartAdapter.notifyDataSetChanged();
                        updateTotal();
                        if (cartList.isEmpty()) {
                            rvCart.setVisibility(View.GONE);
                            llEmpty.setVisibility(View.VISIBLE);
                            llBottom.setVisibility(View.GONE);
                        }
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT).show();
                            // 刷新购物车红点
                            com.example.newbee2.MainActivity.loadCartCountStatic(getActivity());
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

    private void updateTotal() {
        int total = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                total += (item.getSellingPrice() != null ? item.getSellingPrice() : 0) * item.getGoodsCount();
            }
        }
        tvTotal.setText("合计: ¥" + total);
    }

    private void checkAllSelect() {
        boolean allSelected = true;
        for (CartItem item : cartList) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        cbAll.setOnCheckedChangeListener(null);
        cbAll.setChecked(allSelected);
        cbAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartList) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotal();
        });
    }

    private List<CartItem> getSelectedItems() {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }
}
