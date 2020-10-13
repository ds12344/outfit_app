package com.outfit.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView back, cart, imgProduct;
    TextView txtProductName, txtProductPrice, txtProductType, txtProductDescription;
    HashMap data;
    Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        data = (HashMap) getIntent().getSerializableExtra("data");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
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

    private void addToCart() {
        HashMap product = data;
        if (ShareStorage.getCartData(this) == null) {

            ProductModel productModel = new ProductModel(product.get("productId").toString(),
                    product.get("name").toString(), product.get("description").toString(),
                    product.get("type").toString(), product.get("price").toString(),
                    product.get("image").toString(), 1);

            ArrayList<ProductModel> productList = new ArrayList<>();
            productList.add(productModel);

            getShippingPrice(product.get("sellerId").toString(), productList);
        } else {
            boolean isSameExist = false;
            CartModel cartModel = ShareStorage.getCartData(this);
            if (cartModel.getSellerId().equals(product.get("sellerId").toString())) {
                for (int i=0; i<cartModel.getProductList().size(); i++) {
                    if (cartModel.getProductList().get(i).getProductId().equals(product.get("productId").toString())) {
                        isSameExist = true;
                        int k = cartModel.getProductList().get(i).getQuantity();
                        k++;
                        cartModel.getProductList().get(i).setQuantity(k);
                        break;
                    }
                }

                if (!isSameExist) {
                    ProductModel productModel = new ProductModel(product.get("productId").toString(),
                            product.get("name").toString(), product.get("description").toString(),
                            product.get("type").toString(), product.get("price").toString(),
                            product.get("image").toString(), 1);
                    cartModel.getProductList().add(productModel);
                }
                ShareStorage.setCartData(this, cartModel);
            } else {
                ProductModel productModel = new ProductModel(product.get("productId").toString(),
                        product.get("name").toString(), product.get("description").toString(),
                        product.get("type").toString(), product.get("price").toString(),
                        product.get("image").toString(), 1);

                ArrayList<ProductModel> productList = new ArrayList<>();
                productList.add(productModel);

                getShippingPrice(product.get("sellerId").toString(), productList);
            }
        }

        Toast.makeText(this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
    }

    private void getShippingPrice(final String sellerId, final ArrayList<ProductModel> productList) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDbRef = mDatabase.getReference().child("Seller").child(sellerId).child("shippingPrice");


        mDbRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                mDialog.dismiss();

                String shippingPrice = dataSnapshot.getValue().toString();

                CartModel cartModel = new CartModel(sellerId, shippingPrice,  productList);
                ShareStorage.setCartData(ProductDetailActivity.this, cartModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
                Toast.makeText(ProductDetailActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
