package com.example.newbee2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private static final Map<String, Integer> CATEGORY_BG = new HashMap<>();
    static {
        CATEGORY_BG.put("家电", R.drawable.bg_cat_elec);      // 橙色
        CATEGORY_BG.put("女装", R.drawable.bg_cat_clothes);    // 粉色
        CATEGORY_BG.put("家具", R.drawable.bg_cat_furniture);  // 棕色
        CATEGORY_BG.put("运动", R.drawable.bg_cat_sport);      // 绿色
        CATEGORY_BG.put("游戏", R.drawable.bg_cat_game);       // 紫色
        CATEGORY_BG.put("美妆", R.drawable.bg_cat_beauty);     // 玫红
        CATEGORY_BG.put("工具", R.drawable.bg_cat_tool);       // 蓝灰
        CATEGORY_BG.put("鞋靴", R.drawable.bg_cat_shoes);      // 蓝色
        CATEGORY_BG.put("玩具", R.drawable.bg_cat_toy);        // 橙色
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
            holder.vBg = convertView.findViewById(R.id.v_bg);
            holder.tvChar = convertView.findViewById(R.id.tv_char);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);

        String firstWord = getFirstWord(category.getCategoryName());
        int bgRes = CATEGORY_BG.containsKey(firstWord) ? CATEGORY_BG.get(firstWord) : R.drawable.bg_badge;
        holder.vBg.setBackgroundResource(bgRes);

        // 显示第一个汉字
        String ch = (firstWord != null && !firstWord.isEmpty()) ? String.valueOf(firstWord.charAt(0)) : "?";
        holder.tvChar.setText(ch);
        holder.tvName.setText(category.getCategoryName());

        return convertView;
    }

    private String getFirstWord(String name) {
        if (name == null) return "其他";
        return name.split("\\s+")[0];
    }

    static class ViewHolder {
        View vBg;
        TextView tvChar;
        TextView tvName;
    }
}
