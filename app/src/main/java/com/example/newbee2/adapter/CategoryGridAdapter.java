package com.example.newbee2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newbee2.R;
import com.example.newbee2.model.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryGridAdapter extends BaseAdapter {

    private Context context;
    private List<Category> categoryList;
    private LayoutInflater inflater;
    private static final Map<String, Integer> CATEGORY_ICONS = new HashMap<>();
    static {
        CATEGORY_ICONS.put("家电", R.drawable.ic_cat_elec);      // 电视机
        CATEGORY_ICONS.put("女装", R.drawable.ic_cat_clothes);    // T恤/衣服
        CATEGORY_ICONS.put("家具", R.drawable.ic_cat_furniture);  // 椅子
        CATEGORY_ICONS.put("运动", R.drawable.ic_cat_sport);      // 运动人物
        CATEGORY_ICONS.put("游戏", R.drawable.ic_cat_game);       // 游戏手柄
        CATEGORY_ICONS.put("美妆", R.drawable.ic_cat_beauty);     // 人脸/妆容
        CATEGORY_ICONS.put("工具", R.drawable.ic_cat_tool);       // 扳手
        CATEGORY_ICONS.put("鞋靴", R.drawable.ic_cat_shoes);      // 鞋子
        CATEGORY_ICONS.put("玩具", R.drawable.ic_cat_toy);        // 笑脸/玩偶
    }

    public CategoryGridAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category_grid, parent, false);
            holder = new ViewHolder();
            holder.ivIcon = convertView.findViewById(R.id.iv_icon);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);

        int iconRes = getIconForCategory(category.getCategoryName());
        holder.ivIcon.setImageResource(iconRes);
        holder.tvName.setText(category.getCategoryName());

        return convertView;
    }

    private int getIconForCategory(String categoryName) {
        if (categoryName == null) return R.drawable.ic_category;
        String firstWord = categoryName.split("\\s+")[0];
        if (CATEGORY_ICONS.containsKey(firstWord)) {
            return CATEGORY_ICONS.get(firstWord);
        }
        return R.drawable.ic_category;
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
    }
}
