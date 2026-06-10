package com.example.newbee2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.DetailActivity;
import com.example.newbee2.R;
import com.example.newbee2.adapter.CategoryLeftAdapter;
import com.example.newbee2.adapter.GoodsAdapter;
import com.example.newbee2.model.Category;
import com.example.newbee2.model.Goods;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView rvCategory;
    private GridView gvGoods;
    private CategoryLeftAdapter leftAdapter;
    private GoodsAdapter goodsAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private List<Goods> goodsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategory = view.findViewById(R.id.rv_category);
        gvGoods = view.findViewById(R.id.gv_goods);

        // 左侧分类
        leftAdapter = new CategoryLeftAdapter(getActivity(), categoryList);
        rvCategory.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategory.setAdapter(leftAdapter);

        leftAdapter.setOnItemClickListener((category, position) -> {
            leftAdapter.setSelectedPosition(position);
            loadGoodsByCategory(category.getCategoryId(), category.getCategoryName());
        });

        // 右侧商品
        goodsAdapter = new GoodsAdapter(getActivity(), goodsList);
        gvGoods.setAdapter(goodsAdapter);

        gvGoods.setOnItemClickListener((parent, v, position, id) -> {
            Goods goods = goodsList.get(position);
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("goodsId", goods.getGoodsId());
            startActivity(intent);
        });

        loadCategories();
    }

    private void loadCategories() {
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
                            getActivity().runOnUiThread(() -> {
                                leftAdapter.notifyDataSetChanged();
                                if (!categoryList.isEmpty()) {
                                    leftAdapter.setSelectedPosition(0);
                                    Category first = categoryList.get(0);
                                    loadGoodsByCategory(first.getCategoryId(), first.getCategoryName());
                                }
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

    // ========== 当前版本：单关键词搜索 ==========
    private void loadGoodsByCategory(Long categoryId, String categoryName) {
        // API的goodsCategoryId参数不生效，用不同关键词区分不同分类
        String keyword = "a";
        if (categoryName != null && !categoryName.isEmpty()) {
            String firstWord = categoryName.split("\\s+")[0];
            switch (firstWord) {
                case "家电": keyword = "手机"; break;      // 手机数码
                case "女装": keyword = "T恤"; break;      // 无印良品衣服
                case "家具": keyword = "座椅"; break;      // 电脑家具
                case "运动": keyword = "耳机"; break;      // 运动耳机
                case "游戏": keyword = "小米"; break;      // 小米产品
                case "美妆": keyword = "口红"; break;      // 口红化妆品
                case "工具": keyword = "橡皮"; break;      // 橡皮工具
                case "鞋靴": keyword = "苹果"; break;      // 苹果产品
                case "玩具": keyword = "MAC"; break;       // MAC化妆品
                default: keyword = "a"; break;
            }
        }
        String url = HttpUtil.BASE_URL + "/search?pageNumber=1&keyword=" + keyword + "&orderBy=";
        HttpUtil.get(url, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result<HomeFragment.SearchResult>>(){}.getType();
                    Result<HomeFragment.SearchResult> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null
                            && result.getData().getList() != null) {
                        goodsList.clear();
                        goodsList.addAll(result.getData().getList());
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> goodsAdapter.notifyDataSetChanged());
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

    /*
    // ========== 合并版本：双关键词搜索并合并结果 ==========
    // 如果需要同时显示多个关键词的商品，可以使用这个版本
    // 例如：美妆分类同时显示"口红"和"唇膏"的商品
    private void loadGoodsByCategory(Long categoryId, String categoryName) {
        // API的goodsCategoryId参数不生效，用不同关键词区分不同分类
        String keyword1 = "a";
        String keyword2Temp = null;
        if (categoryName != null && !categoryName.isEmpty()) {
            String firstWord = categoryName.split("\\s+")[0];
            switch (firstWord) {
                case "家电": keyword1 = "手机"; break;
                case "女装": keyword1 = "MUJI"; break;
                case "家具": keyword1 = "电脑"; break;
                case "运动": keyword1 = "耳机"; break;
                case "游戏": keyword1 = "小米"; break;
                case "美妆": keyword1 = "口红"; keyword2Temp = "唇膏"; break;  // 同时搜索口红和唇膏
                case "工具": keyword1 = "华为"; break;
                case "鞋靴": keyword1 = "苹果"; break;
                case "玩具": keyword1 = "MAC"; keyword2Temp = "Dior"; break;  // 同时搜索MAC和Dior
                default: keyword1 = "a"; break;
            }
        }
        final String keyword2 = keyword2Temp;

        // 加载第一个关键词
        String url1 = HttpUtil.BASE_URL + "/search?pageNumber=1&keyword=" + keyword1 + "&orderBy=";
        HttpUtil.get(url1, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result<HomeFragment.SearchResult>>(){}.getType();
                    Result<HomeFragment.SearchResult> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null
                            && result.getData().getList() != null) {
                        goodsList.clear();
                        goodsList.addAll(result.getData().getList());

                        // 如果有第二个关键词，加载并合并
                        if (keyword2 != null) {
                            String url2 = HttpUtil.BASE_URL + "/search?pageNumber=1&keyword=" + keyword2 + "&orderBy=";
                            HttpUtil.get(url2, new HttpUtil.HttpCallback<String>() {
                                @Override
                                public void onSuccess(String data2) {
                                    if (data2 == null || data2.isEmpty()) return;
                                    try {
                                        Result<HomeFragment.SearchResult> result2 = HttpUtil.getGson().fromJson(data2, type);
                                        if (result2 != null && result2.isSuccess() && result2.getData() != null
                                                && result2.getData().getList() != null) {
                                            goodsList.addAll(result2.getData().getList());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(() -> goodsAdapter.notifyDataSetChanged());
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                }
                            });
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> goodsAdapter.notifyDataSetChanged());
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
    */
}
