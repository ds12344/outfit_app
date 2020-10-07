package com.outfit.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.HashMap;

public class ShowroomDetailActivity extends AppCompatActivity {

    ImageView back, imgShowroom;
    TextView txtShowroomName, txtAddress, txtPhone, txtGst, txtShippingPrice, txtStatus;
    String name, address, phone, showroomImage, gst, shippingPrice;
    Button btnShop;

    HashMap data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom_detail);

        data = (HashMap) getIntent().getSerializableExtra("data");

        back = findViewById(R.id.back);
        imgShowroom = findViewById(R.id.imgShowroom);
        txtShowroomName = findViewById(R.id.txtShowroomName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtGst = findViewById(R.id.txtGst);
        txtShippingPrice = findViewById(R.id.txtShippingPrice);
        txtStatus = findViewById(R.id.txtStatus);
        btnShop = findViewById(R.id.btnShop);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowroomDetailActivity.this, ChooseShoppingGenderActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });

        setData();
    }

    private void setData() {
        name = data.get("showroomName").toString();
        address = data.get("address").toString();
        phone = data.get("phone").toString();
        showroomImage = data.get("showroomImage").toString();
        gst = data.get("gst").toString();
        shippingPrice = data.get("shippingPrice").toString();
        Boolean status = (Boolean) data.get("status");

        txtShowroomName.setText(name);
        txtAddress.setText(address);
        txtPhone.setText(phone);
        txtGst.setText(gst);
        txtShippingPrice.setText("$"+shippingPrice);

        if (status) {
            txtStatus.setText("Open");
        } else {
            txtStatus.setText("Close");
        }

        Glide.with(ShowroomDetailActivity.this)
                .load(showroomImage)
                .placeholder(R.drawable.logo)
                .into(imgShowroom);
    }
}
