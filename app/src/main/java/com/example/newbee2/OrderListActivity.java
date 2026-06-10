package com.example.newbee2;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.adapter.OrderAdapter;
import com.example.newbee2.model.Order;
import com.example.newbee2.model.OrderItem;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TabLayout tabLayout;
    private RecyclerView rvOrder;
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private int currentStatus = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        currentStatus = getIntent().getIntExtra("status", -1);

        ivBack = findViewById(R.id.iv_back);
        tabLayout = findViewById(R.id.tab_layout);
        rvOrder = findViewById(R.id.rv_order);

        ivBack.setOnClickListener(v -> finish());

        // 设置Tab
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("待支付"));
        tabLayout.addTab(tabLayout.newTab().setText("待确认"));
        tabLayout.addTab(tabLayout.newTab().setText("待发货"));
        tabLayout.addTab(tabLayout.newTab().setText("待收货"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));

        // 根据传入状态选中Tab
        switch (currentStatus) {
            case 0: tabLayout.selectTab(tabLayout.getTabAt(1)); break;
            case 1: tabLayout.selectTab(tabLayout.getTabAt(2)); break;
            case 2: tabLayout.selectTab(tabLayout.getTabAt(3)); break;
            case 3: tabLayout.selectTab(tabLayout.getTabAt(4)); break;
            case 4: tabLayout.selectTab(tabLayout.getTabAt(5)); break;
            default: tabLayout.selectTab(tabLayout.getTabAt(0)); break;
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentStatus = -1; break;
                    case 1: currentStatus = 0; break;
                    case 2: currentStatus = 1; break;
                    case 3: currentStatus = 2; break;
                    case 4: currentStatus = 3; break;
                    case 5: currentStatus = 4; break;
                }
                loadOrders();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadOrders();
            }
        });

        adapter = new OrderAdapter(this, orderList);
        adapter.setOnStatusChangeListener(() -> {
            // 状态变更后刷新列表
            loadOrders();
        });
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        String statusParam = currentStatus == -1 ? "" : String.valueOf(currentStatus);
        String url = HttpUtil.BASE_URL + "/order?pageNumber=1&status=" + statusParam;

        HttpUtil.get(url, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<OrderSearchResult>>(){}.getType();
                Result<OrderSearchResult> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess() && result.getData() != null
                        && result.getData().getList() != null) {
                    orderList.clear();
                    orderList.addAll(result.getData().getList());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    public static class OrderSearchResult {
        private int totalCount;
        private int pageSize;
        private int currPage;
        private List<Order> list;

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public int getCurrPage() { return currPage; }
        public void setCurrPage(int currPage) { this.currPage = currPage; }
        public List<Order> getList() { return list; }
        public void setList(List<Order> list) { this.list = list; }
    }
}
