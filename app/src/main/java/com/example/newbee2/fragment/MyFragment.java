package com.example.newbee2.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newbee2.AboutActivity;
import com.example.newbee2.AddressListActivity;
import com.example.newbee2.LoginActivity;
import com.example.newbee2.OrderListActivity;
import com.example.newbee2.R;
import com.example.newbee2.model.Result;
import com.example.newbee2.model.User;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MyFragment extends Fragment {

    private TextView tvNickname, tvLoginName, tvSign;
    private TextView tvBadgeUnpay, tvBadgeDeliver, tvBadgeReceive;
    private LinearLayout llUser, llOrderAll, llOrderUnpay, llOrderConfirm, llOrderDeliver, llOrderReceive, llOrderDone;
    private LinearLayout llAddress, llAbout, llLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvLoginName = view.findViewById(R.id.tv_login_name);
        tvSign = view.findViewById(R.id.tv_sign);
        llUser = view.findViewById(R.id.ll_user);
        llOrderAll = view.findViewById(R.id.ll_order_all);
        llOrderUnpay = view.findViewById(R.id.ll_order_unpay);
        llOrderConfirm = view.findViewById(R.id.ll_order_confirm);
        llOrderDeliver = view.findViewById(R.id.ll_order_deliver);
        llOrderReceive = view.findViewById(R.id.ll_order_receive);
        llOrderDone = view.findViewById(R.id.ll_order_done);
        tvBadgeUnpay = view.findViewById(R.id.tv_badge_unpay);
        tvBadgeDeliver = view.findViewById(R.id.tv_badge_deliver);
        tvBadgeReceive = view.findViewById(R.id.tv_badge_receive);
        llAddress = view.findViewById(R.id.ll_address);
        llAbout = view.findViewById(R.id.ll_about);
        llLogout = view.findViewById(R.id.ll_logout);

        // 用户信息点击
        View.OnClickListener loginListener = v -> {
            if (!isLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        };
        llUser.setOnClickListener(loginListener);
        tvNickname.setOnClickListener(loginListener);

        // 订单点击
        llOrderAll.setOnClickListener(v -> goOrderList(-1));
        llOrderUnpay.setOnClickListener(v -> goOrderList(0));
        llOrderConfirm.setOnClickListener(v -> goOrderList(1));
        llOrderDeliver.setOnClickListener(v -> goOrderList(2));
        llOrderReceive.setOnClickListener(v -> goOrderList(3));
        llOrderDone.setOnClickListener(v -> goOrderList(4));

        // 地址管理
        llAddress.setOnClickListener(v -> {
            if (!isLogin()) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            startActivity(new Intent(getActivity(), AddressListActivity.class));
        });

        // 关于我们
        llAbout.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AboutActivity.class));
        });

        // 退出登录
        llLogout.setOnClickListener(v -> {
            if (getActivity() == null) return;
            if (!isLogin()) {
                Toast.makeText(getActivity(), "您还未登录", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        SharedPreferences info = getActivity().getSharedPreferences("info", 0);
                        info.edit().clear().apply();
                        tvNickname.setText("点击登录");
                        tvLoginName.setText("");
                        tvSign.setText("这家伙很懒，没有写签名！");
                        Toast.makeText(getActivity(), "已退出登录", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
        loadOrderCounts();
    }

    private boolean isLogin() {
        if (getActivity() == null) return false;
        SharedPreferences info = getActivity().getSharedPreferences("info", 0);
        String token = info.getString("token", "");
        return !token.isEmpty();
    }

    private void goOrderList(int status) {
        if (!isLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        Intent intent = new Intent(getActivity(), OrderListActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
    }

    private void loadUserInfo() {
        if (getActivity() == null) return;
        SharedPreferences info = getActivity().getSharedPreferences("info", 0);
        String token = info.getString("token", "");
        if (token.isEmpty()) {
            tvNickname.setText("点击登录");
            tvLoginName.setText("");
            tvSign.setText("这家伙很懒，没有写签名！");
            return;
        }

        HttpUtil.get(HttpUtil.BASE_URL + "/user/info", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                if (getActivity() == null) return;
                try {
                    Type type = new TypeToken<Result<User>>(){}.getType();
                    Result<User> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        User user = result.getData();
                        getActivity().runOnUiThread(() -> {
                            // 昵称：默认为手机号
                            String nickname = user.getNickName();
                            if (nickname == null || nickname.isEmpty()) {
                                nickname = user.getLoginName();
                            }
                            tvNickname.setText(nickname);

                            // 登录名
                            String loginName = user.getLoginName();
                            if (loginName != null && !loginName.isEmpty()) {
                                tvLoginName.setText("登录名：" + loginName);
                            }

                            // 个性签名
                            String sign = user.getIntroduceSign();
                            if (sign != null && !sign.isEmpty()) {
                                tvSign.setText(sign);
                            } else {
                                tvSign.setText("这家伙很懒，没有写签名！");
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

    private void loadOrderCounts() {
        if (getActivity() == null) return;
        if (!isLogin()) {
            // 未登录隐藏所有红点
            tvBadgeUnpay.setVisibility(View.GONE);
            tvBadgeDeliver.setVisibility(View.GONE);
            tvBadgeReceive.setVisibility(View.GONE);
            return;
        }

        // 加载待支付订单数量
        loadOrderCount(0, tvBadgeUnpay);
        // 加载待发货订单数量
        loadOrderCount(2, tvBadgeDeliver);
        // 加载待收货订单数量
        loadOrderCount(3, tvBadgeReceive);
    }

    private void loadOrderCount(int status, TextView badgeView) {
        String url = HttpUtil.BASE_URL + "/order?pageNumber=1&status=" + status;
        HttpUtil.get(url, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                if (getActivity() == null) return;
                try {
                    Type type = new TypeToken<Result<OrderSearchResult>>(){}.getType();
                    Result<OrderSearchResult> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        int count = result.getData().getTotalCount();
                        getActivity().runOnUiThread(() -> {
                            if (count > 0) {
                                badgeView.setVisibility(View.VISIBLE);
                                badgeView.setText(String.valueOf(count));
                            } else {
                                badgeView.setVisibility(View.GONE);
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

    // 订单搜索结果内部类
    public static class OrderSearchResult {
        private int totalCount;
        private int pageSize;
        private int currPage;

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public int getCurrPage() { return currPage; }
        public void setCurrPage(int currPage) { this.currPage = currPage; }
    }
}
