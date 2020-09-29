package com.outfit.user;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import adapter.OrderListAdapter;


public class OrderListActivity extends AppCompatActivity {

    RecyclerView rcyOrder;
    ImageView back;
    OrderListAdapter orderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        back = findViewById(R.id.back);
        rcyOrder = findViewById(R.id.rcyOrder);
        orderListAdapter = new OrderListAdapter(this);
        rcyOrder.setAdapter(orderListAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
