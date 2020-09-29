package com.outfit.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import adapter.AddressAdapter;


public class AddressListActivity extends AppCompatActivity {

    ImageView back, add;
    RecyclerView rcyAddress;
    AddressAdapter addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        rcyAddress = findViewById(R.id.rcyAddress);

        addressAdapter = new AddressAdapter(this);
        rcyAddress.setAdapter(addressAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }
}
