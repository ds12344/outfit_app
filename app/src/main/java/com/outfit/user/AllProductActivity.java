package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.user.R;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.ProductAdapter;

public class AllProductActivity extends AppCompatActivity {

    RecyclerView rcyProduct;
    ImageView back,cart;
    ProductAdapter productAdapter;
    HashMap data;
    String type = "", sellerId = "";
    ArrayList<HashMap> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        data = (HashMap) getIntent().getSerializableExtra("data");
        type = getIntent().getStringExtra("type");
        sellerId = data.get("sellerId").toString();

        back=findViewById(R.id.back);
        cart=findViewById(R.id.cart);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AllProductActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
        rcyProduct = findViewById(R.id.rcyProduct);
        productAdapter = new ProductAdapter(this);
        rcyProduct.setAdapter(productAdapter);

        getProductList();

    }

    void getProductList() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product").child(sellerId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                if (dataSnapshot.getValue() == null)
                    return;
                list.clear();


                HashMap chats = (HashMap) dataSnapshot.getValue();

                for (final Object entry : chats.keySet()) {
                    HashMap product = (HashMap) chats.get(entry);
                    if (product.get("type").toString().equals(type))
                        list.add(product);
                }

                productAdapter.updateAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
    }
}
