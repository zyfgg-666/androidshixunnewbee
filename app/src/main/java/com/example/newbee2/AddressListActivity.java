package com.example.newbee2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newbee2.adapter.AddressAdapter;
import com.example.newbee2.model.Address;
import com.example.newbee2.model.Result;
import com.example.newbee2.utils.HttpUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvAdd;
    private RecyclerView rvAddress;
    private AddressAdapter adapter;
    private List<Address> addressList = new ArrayList<>();
    private boolean selectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        selectMode = getIntent().getBooleanExtra("selectMode", false);

        ivBack = findViewById(R.id.iv_back);
        tvAdd = findViewById(R.id.tv_add);
        rvAddress = findViewById(R.id.rv_address);

        ivBack.setOnClickListener(v -> finish());

        tvAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddressEditActivity.class));
        });

        adapter = new AddressAdapter(this, addressList);
        adapter.setSelectMode(selectMode);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));
        rvAddress.setAdapter(adapter);

        adapter.setOnAddressListener(new AddressAdapter.OnAddressListener() {
            @Override
            public void onEdit(Address address) {
                Intent intent = new Intent(AddressListActivity.this, AddressEditActivity.class);
                intent.putExtra("addressId", address.getAddressId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Address address) {
                deleteAddress(address);
            }

            @Override
            public void onSelect(Address address) {
                if (selectMode) {
                    Intent intent = new Intent();
                    intent.putExtra("addressId", address.getAddressId());
                    intent.putExtra("name", address.getUserName());
                    intent.putExtra("detail", address.getFullAddress());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        HttpUtil.get(HttpUtil.BASE_URL + "/address", new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result<List<Address>>>(){}.getType();
                    Result<List<Address>> result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess() && result.getData() != null) {
                        addressList.clear();
                        addressList.addAll(result.getData());
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void deleteAddress(Address address) {
        HttpUtil.delete(HttpUtil.BASE_URL + "/address/" + address.getAddressId(), new HttpUtil.HttpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (data == null || data.isEmpty()) return;
                try {
                    Type type = new TypeToken<Result>(){}.getType();
                    Result result = HttpUtil.getGson().fromJson(data, type);
                    if (result != null && result.isSuccess()) {
                        addressList.remove(address);
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(AddressListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }
}
