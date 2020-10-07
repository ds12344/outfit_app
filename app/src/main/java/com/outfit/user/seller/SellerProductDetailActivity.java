package com.outfit.user.seller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.outfit.user.R;


import java.util.HashMap;

public class SellerProductDetailActivity extends AppCompatActivity {

    ImageView back, edit, imgProduct;
    TextView txtProductName, txtProductPrice, txtProductType, txtProductDescription;
    HashMap data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_detail);

        data = (HashMap) getIntent().getSerializableExtra("data");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProductDetailActivity.this, AddProductActivity.class);
                intent.putExtra("isEdit", true);
                intent.putExtra("data", data);
                startActivityForResult(intent, 101);
            }
        });

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtProductType = findViewById(R.id.txtProductType);
        txtProductDescription = findViewById(R.id.txtProductDescription);

        setData();
    }

    private void setData() {
        Glide.with(this)
                .load(data.get("image").toString())
                .placeholder(R.drawable.logo)
                .into(imgProduct);

        txtProductName.setText(data.get("name").toString());
        txtProductPrice.setText("$"+data.get("price").toString());
        txtProductType.setText(data.get("type").toString());
        txtProductDescription.setText(data.get("description").toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 101 && data != null) {
            this.data = (HashMap) data.getSerializableExtra("data");
            setData();
        }
    }
}
