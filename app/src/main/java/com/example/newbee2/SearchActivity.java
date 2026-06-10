package com.example.newbee2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.adapter.SearchGoodsAdapter;
import com.example.newbee2.fragment.HomeFragment;
import com.example.newbee2.model.Goods;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etSearch;
    private TextView tvSearch, tvSortDefault, tvSortNew, tvSortPrice;
    private RecyclerView rvSearch;
    private SearchGoodsAdapter adapter;
    private List<Goods> goodsList = new ArrayList<>();
    private String currentOrderBy = "";
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化HttpUtil
        HttpUtil.init(this);

        // 检查登录状态
        SharedPreferences info = getSharedPreferences("info", 0);
        String token = info.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ivBack = findViewById(R.id.iv_back);
        etSearch = findViewById(R.id.et_search);
        tvSearch = findViewById(R.id.tv_search);
        tvSortDefault = findViewById(R.id.tv_sort_default);
        tvSortNew = findViewById(R.id.tv_sort_new);
        tvSortPrice = findViewById(R.id.tv_sort_price);
        rvSearch = findViewById(R.id.rv_search);

        adapter = new SearchGoodsAdapter(this, goodsList);
        rvSearch.setLayoutManager(new GridLayoutManager(this, 2));
        rvSearch.setAdapter(adapter);

        adapter.setOnItemClickListener(goods -> {
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            intent.putExtra("goodsId", goods.getGoodsId());
            startActivity(intent);
        });

        ivBack.setOnClickListener(v -> finish());

        tvSearch.setOnClickListener(v -> {
            currentPage = 1;
            doSearch();
        });

        // 排序
        tvSortDefault.setOnClickListener(v -> {
            currentOrderBy = "";
            resetSortColor();
            tvSortDefault.setTextColor(0xFF00C9A7);
            doSearch();
        });

        tvSortNew.setOnClickListener(v -> {
            currentOrderBy = "new";
            resetSortColor();
            tvSortNew.setTextColor(0xFF00C9A7);
            doSearch();
        });

        tvSortPrice.setOnClickListener(v -> {
            currentOrderBy = "price";
            resetSortColor();
            tvSortPrice.setTextColor(0xFF00C9A7);
            doSearch();
        });

        // 上拉加载更多
        rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    currentPage++;
                    doSearch();
                }
            }
        });
    }

    private void resetSortColor() {
        tvSortDefault.setTextColor(0xFF333333);
        tvSortNew.setTextColor(0xFF333333);
        tvSortPrice.setTextColor(0xFF333333);
    }

    private void doSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (keyword.isEmpty()) {
            keyword = "a"; // 默认关键词
        }
        String url = HttpUtil.BASE_URL + "/search?pageNumber=" + currentPage
                + "&keyword=" + keyword + "&orderBy=" + currentOrderBy;

        HttpUtil.get(url, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<HomeFragment.SearchResult>>(){}.getType();
                Result<HomeFragment.SearchResult> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess() && result.getData() != null
                        && result.getData().getList() != null) {
                    if (currentPage == 1) {
                        goodsList.clear();
                    }
                    goodsList.addAll(result.getData().getList());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }
}
