package com.outfit.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import adapter.ProductAdapter;


public class AllProductActivity extends AppCompatActivity {

    RecyclerView rcyProduct;
    ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        rcyProduct = findViewById(R.id.rcyProduct);
        productAdapter = new ProductAdapter(this);
        rcyProduct.setAdapter(productAdapter);

    }
}
