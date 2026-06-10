package com.example.newbee2.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newbee2.widget.MyGridView;

import com.bumptech.glide.Glide;
import com.example.newbee2.DetailActivity;
import com.example.newbee2.LoginActivity;
import com.example.newbee2.R;
import com.example.newbee2.SearchActivity;
import com.example.newbee2.adapter.BannerAdapter;
import com.example.newbee2.adapter.CategoryGridAdapter;
import com.example.newbee2.adapter.GoodsAdapter;
import com.example.newbee2.model.Category;
import com.example.newbee2.model.Goods;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.example.newbee2.utils.ImageUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 vpBanner;
    private MyGridView gvCategory, gvNew, gvHot, gvLatest;
    private LinearLayout llSearch;
    private GoodsAdapter newAdapter, hotAdapter, latestAdapter;
    private CategoryGridAdapter categoryGridAdapter;
    private BannerAdapter bannerAdapter;
    private List<Goods> newList = new ArrayList<>();
    private List<Goods> hotList = new ArrayList<>();
    private List<Goods> latestList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private List<String> bannerUrls = new ArrayList<>();
    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;
    private static final long BANNER_DELAY = 3000; // 3秒切换一次

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vpBanner = view.findViewById(R.id.vp_banner);
        gvCategory = (MyGridView) view.findViewById(R.id.gv_category);
        gvNew = (MyGridView) view.findViewById(R.id.gv_new);
        gvHot = (MyGridView) view.findViewById(R.id.gv_hot);
        gvLatest = (MyGridView) view.findViewById(R.id.gv_latest);
        llSearch = view.findViewById(R.id.ll_search);

        // 搜索
        llSearch.setOnClickListener(v -> {
            if (isLogin()) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        // 个人中心按钮点击，跳转到"我的"页面
        view.findViewById(R.id.tv_my_center).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((com.example.newbee2.MainActivity) getActivity()).switchToTab(3);
            }
        });

        // 三道杠菜单按钮点击，跳转到分类页面
        view.findViewById(R.id.tv_menu).setOnClickListener(v -> {
            if (getActivity() != null) {
                ((com.example.newbee2.MainActivity) getActivity()).switchToTab(1);
            }
        });

        // 初始化适配器
        newAdapter = new GoodsAdapter(getActivity(), newList);
        gvNew.setAdapter(newAdapter);

        hotAdapter = new GoodsAdapter(getActivity(), hotList);
        gvHot.setAdapter(hotAdapter);

        latestAdapter = new GoodsAdapter(getActivity(), latestList);
        gvLatest.setAdapter(latestAdapter);

        categoryGridAdapter = new CategoryGridAdapter(getActivity(), categoryList);
        gvCategory.setAdapter(categoryGridAdapter);

        // 轮播图适配器
        bannerAdapter = new BannerAdapter(bannerUrls);
        vpBanner.setAdapter(bannerAdapter);

        // 自动轮播
        bannerRunnable = () -> {
            if (!bannerUrls.isEmpty()) {
                int next = (vpBanner.getCurrentItem() + 1) % bannerUrls.size();
                vpBanner.setCurrentItem(next, true);
            }
            bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY);
        };

        // 商品点击跳转详情
        gvNew.setOnItemClickListener((parent, v, position, id) -> {
            Goods goods = newList.get(position);
            goDetail(goods.getGoodsId());
        });
        gvHot.setOnItemClickListener((parent, v, position, id) -> {
            Goods goods = hotList.get(position);
            goDetail(goods.getGoodsId());
        });

        gvLatest.setOnItemClickListener((parent, v, position, id) -> {
            Goods goods = latestList.get(position);
            goDetail(goods.getGoodsId());
        });

        // 分类点击跳转到分类Tab
        gvCategory.setOnItemClickListener((parent, v, position, id) -> {
            if (getActivity() != null) {
                ((com.example.newbee2.MainActivity) getActivity()).switchToTab(1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        // 恢复自动轮播
        if (!bannerUrls.isEmpty()) {
            bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止自动轮播
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    private boolean isLogin() {
        if (getActivity() == null) return false;
        SharedPreferences info = getActivity().getSharedPreferences("info", 0);
        String token = info.getString("token", "");
        return !token.isEmpty();
    }

    private void goDetail(Long goodsId) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("goodsId", goodsId);
        startActivity(intent);
    }

    private void loadBanner() {
        // 从接口获取轮播图、新品、热销、推荐商品
        HttpUtil.get(HttpUtil.BASE_URL + "/index-infos", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    com.google.gson.JsonObject json = HttpUtil.getGson().fromJson(data, com.google.gson.JsonObject.class);
                    if (json != null && json.has("data")) {
                        com.google.gson.JsonObject dataObj = json.getAsJsonObject("data");
                        // 轮播图
                        if (dataObj.has("carousels")) {
                            com.google.gson.JsonArray carousels = dataObj.getAsJsonArray("carousels");
                            bannerUrls.clear();
                            for (int i = 0; i < carousels.size(); i++) {
                                String url = carousels.get(i).getAsJsonObject().get("carouselUrl").getAsString();
                                bannerUrls.add(url);
                            }
                        }
                        // 新品上线
                        if (dataObj.has("newGoodses")) {
                            com.google.gson.JsonArray newGoods = dataObj.getAsJsonArray("newGoodses");
                            newList.clear();
                            for (int i = 0; i < newGoods.size(); i++) {
                                Goods goods = HttpUtil.getGson().fromJson(newGoods.get(i), Goods.class);
                                newList.add(goods);
                            }
                        }
                        // 热门商品
                        if (dataObj.has("hotGoodses")) {
                            com.google.gson.JsonArray hotGoods = dataObj.getAsJsonArray("hotGoodses");
                            hotList.clear();
                            for (int i = 0; i < hotGoods.size(); i++) {
                                Goods goods = HttpUtil.getGson().fromJson(hotGoods.get(i), Goods.class);
                                hotList.add(goods);
                            }
                        }
                        // 最新推荐
                        if (dataObj.has("recommendGoodses")) {
                            com.google.gson.JsonArray recGoods = dataObj.getAsJsonArray("recommendGoodses");
                            latestList.clear();
                            for (int i = 0; i < recGoods.size(); i++) {
                                Goods goods = HttpUtil.getGson().fromJson(recGoods.get(i), Goods.class);
                                latestList.add(goods);
                            }
                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                bannerAdapter.notifyDataSetChanged();
                                newAdapter.notifyDataSetChanged();
                                hotAdapter.notifyDataSetChanged();
                                latestAdapter.notifyDataSetChanged();
                                // 开始自动轮播
                                bannerHandler.postDelayed(bannerRunnable, BANNER_DELAY);
                            });
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

    private void loadData() {
        if (!isLogin()) {
            return;
        }

        // 轮播图
        loadBanner();

        // 分类
        HttpUtil.get(HttpUtil.BASE_URL + "/categories", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result<List<Category>>>(){}.getType();
                    Result<List<Category>> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        categoryList.clear();
                        categoryList.addAll(result.getData());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> categoryGridAdapter.notifyDataSetChanged());
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

    // 搜索结果内部类
    public static class SearchResult {
        private int totalCount;
        private int pageSize;
        private int currPage;
        private List<Goods> list;

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public int getCurrPage() { return currPage; }
        public void setCurrPage(int currPage) { this.currPage = currPage; }
        public List<Goods> getList() { return list; }
        public void setList(List<Goods> list) { this.list = list; }
    }
}
