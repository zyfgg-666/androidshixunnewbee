package com.example.newbee2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static final String BASE_URL = "http://172.30.130.131:28019/mallapi/api/v1";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Context appContext;

    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public interface HttpCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    private static String getToken() {
        if (appContext == null) return "";
        SharedPreferences info = appContext.getSharedPreferences("info", Context.MODE_PRIVATE);
        return info.getString("token", "");
    }

    // GET请求
    public static void get(String url, HttpCallback<String> callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                mainHandler.post(() -> callback.onSuccess(body));
            }
        });
    }

    // POST表单请求
    public static void postForm(String url, Map<String, String> params, HttpCallback<String> callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", getToken())
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                mainHandler.post(() -> callback.onSuccess(body));
            }
        });
    }

    // POST JSON请求
    public static void postJson(String url, Object obj, HttpCallback<String> callback) {
        String json = gson.toJson(obj);
        Log.d("HttpUtil", "POST JSON URL: " + url);
        Log.d("HttpUtil", "POST JSON Body: " + json);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", getToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpUtil", "POST JSON Error: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resBody = response.body() != null ? response.body().string() : "";
                Log.d("HttpUtil", "POST JSON Response: " + resBody);
                mainHandler.post(() -> callback.onSuccess(resBody));
            }
        });
    }

    // PUT请求
    public static void put(String url, Object obj, HttpCallback<String> callback) {
        String json = gson.toJson(obj);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", getToken())
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resBody = response.body() != null ? response.body().string() : "";
                mainHandler.post(() -> callback.onSuccess(resBody));
            }
        });
    }

    // DELETE请求
    public static void delete(String url, HttpCallback<String> callback) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", getToken())
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                mainHandler.post(() -> callback.onSuccess(body));
            }
        });
    }

    public static Gson getGson() {
        return gson;
    }
}
