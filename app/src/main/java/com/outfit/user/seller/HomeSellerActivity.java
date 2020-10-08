package com.outfit.user.seller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.user.HomeActivity;
import com.outfit.user.LoginActivity;
import com.outfit.user.R;
import com.outfit.user.seller.adapter.MyProductAdapter;


import java.util.ArrayList;
import java.util.HashMap;

public class HomeSellerActivity extends AppCompatActivity {

    ImageView menu;
    DrawerLayout drawer;
    TextView txtHome, txtMyShowroom, txtMyProduct, txtAddProduct, txtBackToUser, txtLogout, txtMyOrder;
    RecyclerView rcyProduct;
    MyProductAdapter myProductAdapter;
    ArrayList<HashMap> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_seller);

        drawer = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        txtHome = findViewById(R.id.txtHome);
        txtMyShowroom = findViewById(R.id.txtMyShowroom);
        txtMyProduct = findViewById(R.id.txtMyProduct);
        txtAddProduct = findViewById(R.id.txtAddProduct);
        txtBackToUser = findViewById(R.id.txtBackToUser);
        txtLogout = findViewById(R.id.txtLogout);
        txtMyOrder = findViewById(R.id.txtMyOrder);

        menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        txtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        txtMyShowroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeSellerActivity.this, SellerProfileActivity.class);
                startActivity(intent);
            }
        });

        txtMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeSellerActivity.this, MyOrderActivity.class);
                startActivity(intent);
            }
        });

        txtMyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeSellerActivity.this, MyProductActivity.class);
                startActivity(intent);
            }
        });

        txtAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeSellerActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        txtBackToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeSellerActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeSellerActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        rcyProduct = findViewById(R.id.rcyProduct);
        myProductAdapter = new MyProductAdapter(this);
        rcyProduct.setAdapter(myProductAdapter);

        getMyProduct();
    }

    @SuppressLint("WrongConstant")
    private void closeDrawer() {
        drawer.closeDrawer(Gravity.START);
    }

    void getMyProduct() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product").child(user.getUid());
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

                    list.add(product);
                }

                myProductAdapter.updateAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
    }
}
