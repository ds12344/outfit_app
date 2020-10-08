package com.outfit.user.seller;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.outfit.user.R;
import com.outfit.user.seller.adapter.MyOrderAdapter;

public class MyOrderActivity extends AppCompatActivity {

    RecyclerView rcyOrder;
    MyOrderAdapter myOrderAdapter;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rcyOrder = findViewById(R.id.rcyMyOrder);
        myOrderAdapter = new MyOrderAdapter(this);
        rcyOrder.setAdapter(myOrderAdapter);
    }
}
