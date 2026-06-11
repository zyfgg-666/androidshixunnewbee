package com.example.newbee2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newbee2.model.Address;
import com.example.newbee2.model.RegionData;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // 地区选择 - 三级联动
        tvRegion.setOnClickListener(v -> showRegionPicker());

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private int selectedProvince = 0;
    private int selectedCity = 0;
    private int selectedDistrict = 0;

    private void showRegionPicker() {
        View view = getLayoutInflater().inflate(R.layout.dialog_region_picker, null);

        ListView lvProvince = view.findViewById(R.id.lv_province);
        ListView lvCity = view.findViewById(R.id.lv_city);
        ListView lvDistrict = view.findViewById(R.id.lv_district);

        // 省份列表
        List<String> provinceNames = new ArrayList<>();
        for (RegionData.Province p : RegionData.getProvinces()) {
            provinceNames.add(p.name);
        }
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, provinceNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextSize(14);
                tv.setPadding(20, 16, 20, 16);
                if (position == selectedProvince) {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.parseColor("#E53E30"));
                } else {
                    tv.setBackgroundColor(Color.TRANSPARENT);
                    tv.setTextColor(Color.DKGRAY);
                }
                return tv;
            }
        };
        lvProvince.setAdapter(provinceAdapter);

        // 城市列表
        final List<String>[] cityNames = new List[]{new ArrayList<>()};
        final ArrayAdapter<String>[] cityAdapter = new ArrayAdapter[1];
        refreshCityList(cityNames, cityAdapter, lvCity);

        // 区县列表
        final List<String>[] districtNames = new List[]{new ArrayList<>()};
        final ArrayAdapter<String>[] districtAdapter = new ArrayAdapter[1];
        refreshDistrictList(districtNames, districtAdapter, lvDistrict);

        lvProvince.setOnItemClickListener((parent, view1, position, id) -> {
            selectedProvince = position;
            selectedCity = 0;
            selectedDistrict = 0;
            provinceAdapter.notifyDataSetChanged();
            refreshCityList(cityNames, cityAdapter, lvCity);
            refreshDistrictList(districtNames, districtAdapter, lvDistrict);
        });

        lvCity.setOnItemClickListener((parent, view12, position, id) -> {
            selectedCity = position;
            selectedDistrict = 0;
            cityAdapter[0].notifyDataSetChanged();
            refreshDistrictList(districtNames, districtAdapter, lvDistrict);
        });

        lvDistrict.setOnItemClickListener((parent, view13, position, id) -> {
            selectedDistrict = position;
            districtAdapter[0].notifyDataSetChanged();
        });

        new android.app.AlertDialog.Builder(this)
                .setTitle("选择地区")
                .setView(view)
                .setPositiveButton("确定", (d, which) -> confirmRegion())
                .setNegativeButton("取消", null)
                .show();
    }

    private void refreshCityList(List<String>[] cityNames, ArrayAdapter<String>[] cityAdapter, ListView lvCity) {
        cityNames[0].clear();
        List<RegionData.City> cities = RegionData.getCities(selectedProvince);
        for (RegionData.City c : cities) {
            cityNames[0].add(c.name);
        }
        if (cityAdapter[0] == null) {
            cityAdapter[0] = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, cityNames[0]) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView tv = (TextView) super.getView(position, convertView, parent);
                    tv.setTextSize(14);
                    tv.setPadding(20, 16, 20, 16);
                    if (position == selectedCity) {
                        tv.setBackgroundColor(Color.WHITE);
                        tv.setTextColor(Color.parseColor("#E53E30"));
                    } else {
                        tv.setBackgroundColor(Color.TRANSPARENT);
                        tv.setTextColor(Color.DKGRAY);
                    }
                    return tv;
                }
            };
            lvCity.setAdapter(cityAdapter[0]);
        } else {
            cityAdapter[0].notifyDataSetChanged();
        }
        lvCity.setItemChecked(selectedCity, true);
    }

    private void refreshDistrictList(List<String>[] districtNames, ArrayAdapter<String>[] districtAdapter, ListView lvDistrict) {
        districtNames[0].clear();
        List<String> districts = RegionData.getDistricts(selectedProvince, selectedCity);
        districtNames[0].addAll(districts);
        if (districtAdapter[0] == null) {
            districtAdapter[0] = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, districtNames[0]) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView tv = (TextView) super.getView(position, convertView, parent);
                    tv.setTextSize(14);
                    tv.setPadding(20, 16, 20, 16);
                    if (position == selectedDistrict) {
                        tv.setBackgroundColor(Color.WHITE);
                        tv.setTextColor(Color.parseColor("#E53E30"));
                    } else {
                        tv.setBackgroundColor(Color.TRANSPARENT);
                        tv.setTextColor(Color.DKGRAY);
                    }
                    return tv;
                }
            };
            lvDistrict.setAdapter(districtAdapter[0]);
        } else {
            districtAdapter[0].notifyDataSetChanged();
        }
    }

    private void confirmRegion() {
        try {
            String province = RegionData.getProvinces().get(selectedProvince).name;
            String city = RegionData.getCities(selectedProvince).get(selectedCity).name;
            String district = RegionData.getDistricts(selectedProvince, selectedCity).get(selectedDistrict);
            tvRegion.setText(province + " " + city + " " + district);
        } catch (Exception e) {
            // fallback
            String province = RegionData.getProvinces().get(0).name;
            String city = RegionData.getCities(0).get(0).name;
            String district = RegionData.getDistricts(0, 0).get(0);
            tvRegion.setText(province + " " + city + " " + district);
        }
    }

    private void handleSaveResult(String data) {
        try {
            Type type = new TypeToken<Result>(){}.getType();
            Result result = HttpUtil.getGson().fromJson(data, type);
            if (result != null && result.isSuccess()) {
                runOnUiThread(() -> {
                    Toast.makeText(AddressEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } else {
                String msg = result != null ? result.getMessage() : "保存失败";
                runOnUiThread(() -> Toast.makeText(AddressEditActivity.this, msg, Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(AddressEditActivity.this, "解析错误: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
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

        // 拆分省/市/区
        String[] parts = region.split("\\s+");
        String provinceName = parts.length > 0 ? parts[0] : region;
        String cityName = parts.length > 1 ? parts[1] : "";
        String regionName = parts.length > 2 ? parts[2] : "";

        Map<String, Object> params = new HashMap<>();
        params.put("userName", name);
        params.put("userPhone", phone);
        params.put("provinceName", provinceName);
        params.put("cityName", cityName);
        params.put("regionName", regionName);
        params.put("detailAddress", detail);
        params.put("defaultFlag", 1);

        if (addressId != null) {
            params.put("addressId", addressId);
            HttpUtil.put(HttpUtil.BASE_URL + "/address", params, new HttpUtil.HttpCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    handleSaveResult(data);
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
                    handleSaveResult(data);
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(AddressEditActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}
