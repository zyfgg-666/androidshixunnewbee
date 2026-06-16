package com.example.newbee2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbee2.model.Result;
import com.example.newbee2.model.User;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AccountManageActivity extends AppCompatActivity {

    private TextView tvLoginName;
    private LinearLayout llChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_account_manage);

            ImageView ivBack = findViewById(R.id.iv_back);
            tvLoginName = findViewById(R.id.tv_login_name);
            llChangePwd = findViewById(R.id.ll_change_pwd);

            ivBack.setOnClickListener(v -> finish());
            llChangePwd.setOnClickListener(v -> showChangePwdDialog());

            loadUserInfo();
        } catch (Exception e) {
            Toast.makeText(this, "初始化错误：" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void loadUserInfo() {
        HttpUtil.get(HttpUtil.BASE_URL + "/user/info",
                new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null) return;
                try {
                    Type type = new TypeToken<Result<User>>(){}.getType();
                    Result<User> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        String loginName = result.getData().getLoginName();
                        runOnUiThread(() ->
                            tvLoginName.setText(loginName != null ? loginName : "--"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override public void onError(String error) {}
        });
    }

    private void showChangePwdDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 0);

        EditText etOld = new EditText(this);
        etOld.setHint("请输入原密码");
        layout.addView(etOld);

        EditText etNew = new EditText(this);
        etNew.setHint("请输入新密码");
        layout.addView(etNew);

        new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(layout)
                .setPositiveButton("确定", (d, w) -> {
                    String oldPwd = etOld.getText().toString().trim();
                    String newPwd = etNew.getText().toString().trim();
                    if (oldPwd.isEmpty() || newPwd.isEmpty()) {
                        Toast.makeText(this, "请填写完整", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    changePassword(oldPwd, newPwd);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void changePassword(String oldPwd, String newPwd) {
        Map<String, Object> params = new HashMap<>();
        params.put("originalPassword", oldPwd);
        params.put("newPassword", newPwd);

        HttpUtil.put(HttpUtil.BASE_URL + "/user/password", params,
                new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                try {
                    Result result = HttpUtil.getGson().fromJson(data, Result.class);
                    if (result != null && result.isSuccess()) {
                        runOnUiThread(() -> {
                            Toast.makeText(AccountManageActivity.this,
                                    "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
                            getSharedPreferences("info", MODE_PRIVATE)
                                    .edit().clear().apply();
                            Intent intent = new Intent(AccountManageActivity.this,
                                    LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        String msg = result != null ? result.getMessage() : "修改失败";
                        runOnUiThread(() -> Toast.makeText(AccountManageActivity.this,
                                msg, Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(AccountManageActivity.this,
                        "网络错误", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
