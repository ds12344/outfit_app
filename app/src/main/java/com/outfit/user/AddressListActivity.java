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


import java.util.ArrayList;
import java.util.HashMap;

import adapter.AddressAdapter;


public class AddressListActivity extends AppCompatActivity {

    ImageView back, add;
    RecyclerView rcyAddress;
    AddressAdapter addressAdapter;
    ArrayList<HashMap> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        rcyAddress = findViewById(R.id.rcyAddress);

        Boolean isChoose = getIntent().getBooleanExtra("isSelect", false);

        addressAdapter = new AddressAdapter(this, isChoose);
        rcyAddress.setAdapter(addressAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddressList();
    }

    void getAddressList() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Address").child(user.getUid());
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
                addressAdapter.updateAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
    }
}
