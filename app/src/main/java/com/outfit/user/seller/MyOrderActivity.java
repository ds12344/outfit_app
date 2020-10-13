package com.outfit.user.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.outfit.user.seller.adapter.MyOrderAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MyOrderActivity extends AppCompatActivity {

    RecyclerView rcyOrder;
    MyOrderAdapter myOrderAdapter;
    ImageView back;
    ArrayList<HashMap> list = new ArrayList<>();

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

    @Override
    protected void onResume() {
        super.onResume();
        getOrderList();
    }

    void getOrderList() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("OrderSeller").child(user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mDialog.dismiss();
                if (dataSnapshot.getValue() == null)
                    return;

                list.clear();

                HashMap chats = (HashMap) dataSnapshot.getValue();

                for (final Object entry : chats.keySet()) {
                    HashMap order = (HashMap) chats.get(entry);
                    list.add(order);
                }

                myOrderAdapter.updateAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
    }
}
