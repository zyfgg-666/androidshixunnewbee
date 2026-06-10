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
    private static final Map<String, String> CATEGORY_EMOJIS = new HashMap<>();
    static {
        CATEGORY_EMOJIS.put("家电", "📺");
        CATEGORY_EMOJIS.put("女装", "👗");
        CATEGORY_EMOJIS.put("家具", "🛋️");
        CATEGORY_EMOJIS.put("运动", "⚽");
        CATEGORY_EMOJIS.put("游戏", "🎮");
        CATEGORY_EMOJIS.put("美妆", "💄");
        CATEGORY_EMOJIS.put("工具", "🔧");
        CATEGORY_EMOJIS.put("鞋靴", "👟");
        CATEGORY_EMOJIS.put("玩具", "🧸");
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
            holder.tvEmoji = convertView.findViewById(R.id.tv_emoji);
            holder.tvName = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);

        // 根据分类名称设置对应的emoji图标
        String emoji = getEmojiForCategory(category.getCategoryName());
        holder.tvEmoji.setText(emoji);
        holder.tvName.setText(category.getCategoryName());

        return convertView;
    }

    private String getEmojiForCategory(String categoryName) {
        if (categoryName == null) return "📦";

        // 获取第一个词（按空格分割）
        String firstWord = categoryName.split("\\s+")[0];

        // 查找匹配的emoji
        if (CATEGORY_EMOJIS.containsKey(firstWord)) {
            return CATEGORY_EMOJIS.get(firstWord);
        }
        return "📦"; // 默认图标
    }

    static class ViewHolder {
        TextView tvEmoji;
        TextView tvName;
    }
}
