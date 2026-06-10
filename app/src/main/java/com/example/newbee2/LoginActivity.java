package com.example.newbee2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.example.newbee2.utils.MD5Util;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvTestLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvTestLogin = findViewById(R.id.tv_test_login);

        btnLogin.setOnClickListener(v -> doLogin());

        tvTestLogin.setOnClickListener(v -> {
            etUsername.setText("13900139000");
            etPassword.setText("12345678");
            doLogin();
        });

        tvRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("loginName", username);
        params.put("password", password);

        HttpUtil.postJson(HttpUtil.BASE_URL + "/user/register", params, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<String>>(){}.getType();
                Result<String> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess()) {
                    Toast.makeText(LoginActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            result != null ? result.getMessage() : "注册失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        String md5Password = MD5Util.md5(password);

        // 使用JSON格式发送请求
        Map<String, String> params = new HashMap<>();
        params.put("loginName", username);
        params.put("passwordMd5", md5Password);

        HttpUtil.postJson(HttpUtil.BASE_URL + "/user/login", params, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<String>>(){}.getType();
                Result<String> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess() && result.getData() != null) {
                    // 保存token
                    SharedPreferences info = getSharedPreferences("info", MODE_PRIVATE);
                    info.edit().putString("token", result.getData()).apply();

                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            result != null ? result.getMessage() : "登录失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
