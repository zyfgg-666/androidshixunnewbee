package com.example.newbee2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * ========== 原始 OkHttp 实现（已注释保留） ==========
 * import java.util.concurrent.TimeUnit;
 * import okhttp3.Call;
 * import okhttp3.Callback;
 * import okhttp3.FormBody;
 * import okhttp3.MediaType;
 * import okhttp3.OkHttpClient;
 * import okhttp3.Request;
 * import okhttp3.RequestBody;
 * import okhttp3.Response;
 */

public class HttpUtil {

    public static final String BASE_URL = "http://172.30.130.131:28019/mallapi/api/v1";

    /*
     * ========== OkHttp 原始常量（已注释） ==========
     * private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
     */

    private static final Gson gson = new Gson();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Context appContext;

    // 线程池：用于在后台线程执行同步的 HttpURLConnection 请求
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    /*
     * ========== OkHttp 原始客户端（已注释） ==========
     * private static OkHttpClient client = new OkHttpClient.Builder()
     *         .connectTimeout(30, TimeUnit.SECONDS)
     *         .readTimeout(30, TimeUnit.SECONDS)
     *         .writeTimeout(30, TimeUnit.SECONDS)
     *         .build();
     */

    public static void init(Context context) {
        // 使用 ApplicationContext 避免 Activity 泄漏
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

    // ==================== GET 请求 ====================

    public static void get(String url, HttpCallback<String> callback) {
        /*
         * ========== OkHttp 原始 GET 实现（已注释） ==========
         * Request request = new Request.Builder()
         *         .url(url)
         *         .addHeader("token", getToken())
         *         .build();
         *
         * client.newCall(request).enqueue(new Callback() {
         *     @Override
         *     public void onFailure(Call call, IOException e) {
         *         mainHandler.post(() -> callback.onError(e.getMessage()));
         *     }
         *
         *     @Override
         *     public void onResponse(Call call, Response response) throws IOException {
         *         String body = response.body() != null ? response.body().string() : "";
         *         mainHandler.post(() -> callback.onSuccess(body));
         *     }
         * });
         */

        // ========== HttpURLConnection GET 实现 ==========
        threadPool.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();

                // 设置请求方法
                conn.setRequestMethod("GET");

                // 设置请求头
                conn.setRequestProperty("token", getToken());
                conn.setRequestProperty("Accept", "application/json");

                // 设置超时
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);

                // 连接
                conn.connect();

                // 读取响应
                int responseCode = conn.getResponseCode();
                String body = readResponse(conn, responseCode);

                // 切回主线程回调
                mainHandler.post(() -> callback.onSuccess(body));

            } catch (IOException e) {
                Log.e("HttpUtil", "GET Error: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // ==================== POST 表单请求 ====================

    public static void postForm(String url, Map<String, String> params, HttpCallback<String> callback) {
        /*
         * ========== OkHttp 原始 postForm 实现（已注释） ==========
         * FormBody.Builder builder = new FormBody.Builder();
         * if (params != null) {
         *     for (Map.Entry<String, String> entry : params.entrySet()) {
         *         builder.add(entry.getKey(), entry.getValue());
         *     }
         * }
         *
         * Request request = new Request.Builder()
         *         .url(url)
         *         .addHeader("token", getToken())
         *         .post(builder.build())
         *         .build();
         *
         * client.newCall(request).enqueue(new Callback() {
         *     @Override
         *     public void onFailure(Call call, IOException e) {
         *         mainHandler.post(() -> callback.onError(e.getMessage()));
         *     }
         *
         *     @Override
         *     public void onResponse(Call call, Response response) throws IOException {
         *         String body = response.body() != null ? response.body().string() : "";
         *         mainHandler.post(() -> callback.onSuccess(body));
         *     }
         * });
         */

        // ========== HttpURLConnection POST 表单实现 ==========
        threadPool.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("token", getToken());
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);

                // 构建表单参数
                StringBuilder formBody = new StringBuilder();
                if (params != null) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (formBody.length() > 0) {
                            formBody.append("&");
                        }
                        formBody.append(entry.getKey());
                        formBody.append("=");
                        formBody.append(entry.getValue());
                    }
                }

                // 写入请求体
                OutputStream os = conn.getOutputStream();
                os.write(formBody.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                String body = readResponse(conn, responseCode);

                mainHandler.post(() -> callback.onSuccess(body));

            } catch (IOException e) {
                Log.e("HttpUtil", "POST Form Error: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // ==================== POST JSON 请求 ====================

    public static void postJson(String url, Object obj, HttpCallback<String> callback) {
        /*
         * ========== OkHttp 原始 postJson 实现（已注释） ==========
         * String json = gson.toJson(obj);
         * Log.d("HttpUtil", "POST JSON URL: " + url);
         * Log.d("HttpUtil", "POST JSON Body: " + json);
         * RequestBody body = RequestBody.create(json, JSON);
         *
         * Request request = new Request.Builder()
         *         .url(url)
         *         .addHeader("token", getToken())
         *         .post(body)
         *         .build();
         *
         * client.newCall(request).enqueue(new Callback() {
         *     @Override
         *     public void onFailure(Call call, IOException e) {
         *         Log.e("HttpUtil", "POST JSON Error: " + e.getMessage());
         *         mainHandler.post(() -> callback.onError(e.getMessage()));
         *     }
         *
         *     @Override
         *     public void onResponse(Call call, Response response) throws IOException {
         *         String resBody = response.body() != null ? response.body().string() : "";
         *         Log.d("HttpUtil", "POST JSON Response: " + resBody);
         *         mainHandler.post(() -> callback.onSuccess(resBody));
         *     }
         * });
         */

        // ========== HttpURLConnection POST JSON 实现 ==========
        threadPool.execute(() -> {
            HttpURLConnection conn = null;
            try {
                String json = gson.toJson(obj);
                Log.d("HttpUtil", "POST JSON URL: " + url);
                Log.d("HttpUtil", "POST JSON Body: " + json);

                URL urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("token", getToken());
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);

                // 写入 JSON 请求体
                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                String resBody = readResponse(conn, responseCode);
                Log.d("HttpUtil", "POST JSON Response: " + resBody);

                mainHandler.post(() -> callback.onSuccess(resBody));

            } catch (IOException e) {
                Log.e("HttpUtil", "POST JSON Error: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // ==================== PUT 请求 ====================

    public static void put(String url, Object obj, HttpCallback<String> callback) {
        /*
         * ========== OkHttp 原始 put 实现（已注释） ==========
         * String json = gson.toJson(obj);
         * RequestBody body = RequestBody.create(json, JSON);
         *
         * Request request = new Request.Builder()
         *         .url(url)
         *         .addHeader("token", getToken())
         *         .put(body)
         *         .build();
         *
         * client.newCall(request).enqueue(new Callback() {
         *     @Override
         *     public void onFailure(Call call, IOException e) {
         *         mainHandler.post(() -> callback.onError(e.getMessage()));
         *     }
         *
         *     @Override
         *     public void onResponse(Call call, Response response) throws IOException {
         *         String resBody = response.body() != null ? response.body().string() : "";
         *         mainHandler.post(() -> callback.onSuccess(resBody));
         *     }
         * });
         */

        // ========== HttpURLConnection PUT 实现 ==========
        threadPool.execute(() -> {
            HttpURLConnection conn = null;
            try {
                String json = gson.toJson(obj);

                URL urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("token", getToken());
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);

                // 写入 JSON 请求体
                OutputStream os = conn.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                String resBody = readResponse(conn, responseCode);

                mainHandler.post(() -> callback.onSuccess(resBody));

            } catch (IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // ==================== DELETE 请求 ====================

    public static void delete(String url, HttpCallback<String> callback) {
        /*
         * ========== OkHttp 原始 delete 实现（已注释） ==========
         * Request request = new Request.Builder()
         *         .url(url)
         *         .addHeader("token", getToken())
         *         .delete()
         *         .build();
         *
         * client.newCall(request).enqueue(new Callback() {
         *     @Override
         *     public void onFailure(Call call, IOException e) {
         *         mainHandler.post(() -> callback.onError(e.getMessage()));
         *     }
         *
         *     @Override
         *     public void onResponse(Call call, Response response) throws IOException {
         *         String body = response.body() != null ? response.body().string() : "";
         *         mainHandler.post(() -> callback.onSuccess(body));
         *     }
         * });
         */

        // ========== HttpURLConnection DELETE 实现 ==========
        threadPool.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("token", getToken());
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);

                conn.connect();

                int responseCode = conn.getResponseCode();
                String body = readResponse(conn, responseCode);

                mainHandler.post(() -> callback.onSuccess(body));

            } catch (IOException e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    // ==================== 读取响应体 ====================

    /**
     * 读取 HttpURLConnection 的响应体
     * 注意：response.body().string() 只能读一次，这里先读到 String 再返回
     *
     * @param conn         HttpURLConnection 对象
     * @param responseCode HTTP 状态码
     * @return 响应体字符串
     */
    private static String readResponse(HttpURLConnection conn, int responseCode) throws IOException {
        InputStream inputStream;
        if (responseCode >= 200 && responseCode < 300) {
            // 2xx 成功：从 inputStream 读取
            inputStream = conn.getInputStream();
        } else {
            // 4xx/5xx 错误：从 errorStream 读取（服务器可能返回错误信息）
            inputStream = conn.getErrorStream();
        }

        if (inputStream == null) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        inputStream.close();

        return sb.toString();
    }

    public static Gson getGson() {
        return gson;
    }
}
