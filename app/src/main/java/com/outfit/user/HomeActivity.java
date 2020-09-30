package com.outfit.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.outfit.user.seller.SellerRegisterActivity;

import adapter.SellerListAdapter;


public class HomeActivity extends AppCompatActivity {

    ImageView menu;
    DrawerLayout drawer;
    TextView txtHome, txtMyAccount, txtMyOrder, txtMyCart, txtLogout, txtBecomeSeller;


    RecyclerView rcySeller;
    SellerListAdapter sellerListAdapter;

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
                Intent intent = new Intent(HomeActivity.this, SellerRegisterActivity.class);
                startActivity(intent);
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
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

    @SuppressLint("WrongConstant")
    private void closeDrawer() {
        drawer.closeDrawer(Gravity.START);
    }
}
