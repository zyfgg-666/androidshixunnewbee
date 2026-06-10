package com.example.newbee2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newbee2.fragment.CartFragment;
import com.example.newbee2.fragment.CategoryFragment;
import com.example.newbee2.fragment.HomeFragment;
import com.example.newbee2.fragment.MyFragment;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private BadgeDrawable cartBadge;
    private static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // 初始化网络工具
        HttpUtil.init(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) {
            instance = null;
        }
    }

    // 静态方法，供其他Activity调用刷新购物车红点
    public static void loadCartCountStatic(android.content.Context context) {
        if (instance != null) {
            instance.loadCartCount();
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_nav);

        // 设置ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 4;
            }

            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new HomeFragment();
                    case 1: return new CategoryFragment();
                    case 2: return new CartFragment();
                    case 3: return new MyFragment();
                    default: return new HomeFragment();
                }
            }
        });

        // 禁止滑动切换
        viewPager.setUserInputEnabled(false);

        // ViewPager2与BottomNav联动
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: bottomNav.setSelectedItemId(R.id.nav_home); break;
                    case 1: bottomNav.setSelectedItemId(R.id.nav_category); break;
                    case 2: bottomNav.setSelectedItemId(R.id.nav_cart); break;
                    case 3: bottomNav.setSelectedItemId(R.id.nav_my); break;
                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                viewPager.setCurrentItem(0, false);
            } else if (id == R.id.nav_category) {
                viewPager.setCurrentItem(1, false);
            } else if (id == R.id.nav_cart) {
                viewPager.setCurrentItem(2, false);
            } else if (id == R.id.nav_my) {
                viewPager.setCurrentItem(3, false);
            }
            return true;
        });
    }

    // 供Fragment调用来切换Tab
    public void switchToTab(int index) {
        viewPager.setCurrentItem(index, false);
    }

    // 加载购物车商品数量并显示红点
    public void loadCartCount() {
        HttpUtil.get(HttpUtil.BASE_URL + "/shop-cart", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result<List<Map<String, Object>>>>(){}.getType();
                    Result<List<Map<String, Object>>> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        int count = result.getData().size();
                        runOnUiThread(() -> updateCartBadge(count));
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

    private void updateCartBadge(int count) {
        if (cartBadge == null) {
            cartBadge = bottomNav.getOrCreateBadge(R.id.nav_cart);
        }
        if (count > 0) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(count);
        } else {
            cartBadge.setVisible(false);
        }
    }
}
