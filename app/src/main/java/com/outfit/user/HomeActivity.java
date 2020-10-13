package com.outfit.user;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.user.seller.HomeSellerActivity;
import com.outfit.user.seller.SellerRegisterActivity;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import adapter.SellerListAdapter;

public class HomeActivity extends AppCompatActivity {

    ImageView menu;
    DrawerLayout drawer;
    TextView txtHome, txtMyAccount, txtMyOrder, txtMyCart, txtLogout, txtBecomeSeller;
    ViewPager viewPager;

    RecyclerView rcySeller;
    SellerListAdapter sellerListAdapter;
    boolean isPause = false;
    ArrayList<HashMap> list = new ArrayList<>();
    ArrayList<HashMap> closeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawer = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        txtHome = findViewById(R.id.txtHome);
        txtMyAccount = findViewById(R.id.txtMyAccount);
        txtMyOrder = findViewById(R.id.txtMyOrder);
        txtMyCart = findViewById(R.id.txtMyCart);
        txtLogout = findViewById(R.id.txtLogout);
        txtBecomeSeller = findViewById(R.id.txtBecomeSeller);



        rcySeller = findViewById(R.id.rcySellerList);
        sellerListAdapter = new SellerListAdapter(this);
        rcySeller.setAdapter(sellerListAdapter);


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

        txtMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeActivity.this, MyAccountActivity.class);
                startActivity(intent);
            }
        });

        txtMyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

        txtMyCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        txtBecomeSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();

                checkSellerRegisterOrNot();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
        getShowroomList();
    }

    @SuppressLint("WrongConstant")
    private void closeDrawer() {
        drawer.closeDrawer(Gravity.START);
    }

    private void checkSellerRegisterOrNot() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDbRef = mDatabase.getReference().child("User").child(uid).child("isSeller");


            mDbRef.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                    mDialog.dismiss();
                    if (!isPause) {
                        boolean isSeller = (boolean) dataSnapshot.getValue();
                        Intent intent;
                        if (isSeller) {
                            intent = new Intent(HomeActivity.this, HomeSellerActivity.class);
                        } else {
                            intent = new Intent(HomeActivity.this, SellerRegisterActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mDialog.dismiss();
                    Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void getShowroomList() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Seller");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                if (dataSnapshot.getValue() == null)
                    return;

                list.clear();
                closeList.clear();

                HashMap chats = (HashMap) dataSnapshot.getValue();

                for (final Object entry : chats.keySet()) {
                    HashMap product = (HashMap) chats.get(entry);
                    if (!product.get("sellerId").toString().equals(FirebaseAuth.getInstance().getUid())) {
                        if ((Boolean) product.get("status")) {
                            list.add(product);
                        } else {
                            closeList.add(product);
                        }
                    }
                }

                list.addAll(closeList);

                sellerListAdapter.updateAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
    }
}
