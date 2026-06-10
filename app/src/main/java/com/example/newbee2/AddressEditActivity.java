package com.example.newbee2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbee2.model.Address;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AddressEditActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTitle, tvRegion;
    private EditText etName, etPhone, etDetail;
    private Button btnSave;
    private Long addressId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvRegion = findViewById(R.id.tv_region);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etDetail = findViewById(R.id.et_detail);
        btnSave = findViewById(R.id.btn_save);

        ivBack.setOnClickListener(v -> finish());

        addressId = getIntent().getLongExtra("addressId", -1);
        if (addressId == -1) {
            addressId = null;
        }

        if (addressId != null) {
            tvTitle.setText("编辑地址");
            loadAddress();
        }

        // 将tvRegion改为可编辑
        tvRegion.setOnClickListener(v -> {
            // 使用简单的输入对话框
            final EditText input = new EditText(this);
            input.setHint("请输入省/市/区");
            new android.app.AlertDialog.Builder(this)
                    .setTitle("选择地区")
                    .setView(input)
                    .setPositiveButton("确定", (dialog, which) -> {
                        tvRegion.setText(input.getText().toString());
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void loadAddress() {
        HttpUtil.get(HttpUtil.BASE_URL + "/address/" + addressId, new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Type type = new TypeToken<Result<Address>>(){}.getType();
                Result<Address> result = HttpUtil.getGson().fromJson(data, type);
                if (result != null && result.isSuccess() && result.getData() != null) {
                    Address address = result.getData();
                    etName.setText(address.getUserName());
                    etPhone.setText(address.getUserPhone());
                    etDetail.setText(address.getDetailAddress());
                    tvRegion.setText(address.getProvinceName() +
                            address.getCityName() +
                            address.getRegionName());
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void saveAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String region = tvRegion.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || region.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("userName", name);
        params.put("userPhone", phone);
        params.put("provinceName", region);
        params.put("cityName", "");
        params.put("regionName", "");
        params.put("detailAddress", detail);
        params.put("defaultFlag", "1");

        if (addressId != null) {
            params.put("addressId", String.valueOf(addressId));
            HttpUtil.put(HttpUtil.BASE_URL + "/address", params, new HttpUtil.HttpCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddressEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddressEditActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            HttpUtil.postJson(HttpUtil.BASE_URL + "/address", params, new HttpUtil.HttpCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddressEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddressEditActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}
