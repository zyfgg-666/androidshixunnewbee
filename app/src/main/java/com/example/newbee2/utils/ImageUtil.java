package com.example.newbee2.utils;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ImageUtil {

    private static final String IMAGE_BASE = "http://172.30.130.131:28019/mallapi";

    public static void loadImage(ImageView imageView, String url) {
        if (imageView == null || imageView.getContext() == null) return;
        if (url == null || url.isEmpty()) return;

        String fullUrl = url;
        // 如果是相对路径，拼接服务器地址
        if (!url.startsWith("http")) {
            fullUrl = IMAGE_BASE + url;
        }

        Log.d("ImageUtil", "Loading image: " + fullUrl);
        Glide.with(imageView.getContext())
                .load(fullUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void loadImage(ImageView imageView, int resId) {
        if (imageView == null || imageView.getContext() == null) return;
        Glide.with(imageView.getContext())
                .load(resId)
                .into(imageView);
    }
}
