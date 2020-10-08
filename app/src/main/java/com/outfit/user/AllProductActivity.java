package com.outfit.app.user.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.app.R;
import com.outfit.app.user.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class AllProductActivity extends AppCompatActivity {

    RecyclerView rcyProduct;
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
                //Log.e("data", dataSnapshot.toString());
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
