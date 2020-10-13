package com.outfit.user.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.user.R;


import java.util.HashMap;

public class SellerProfileActivity extends AppCompatActivity {

    ImageView back, edit, imgShowroom;
    TextView txtShowroomName, txtAddress, txtPhone, txtGst, txtShippingPrice, txtStatus;
    Switch switchStatus;
    String name, address, phone, showroomImage, gst, shippingPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        imgShowroom = findViewById(R.id.imgShowroom);
        txtShowroomName = findViewById(R.id.txtShowroomName);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtGst = findViewById(R.id.txtGst);
        txtShippingPrice = findViewById(R.id.txtShippingPrice);
        txtStatus = findViewById(R.id.txtStatus);
        switchStatus = findViewById(R.id.switchStatus);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerProfileActivity.this, EditSellerProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("phone", phone);
                intent.putExtra("showroomImage", showroomImage);
                intent.putExtra("gst", gst);
                intent.putExtra("shippingPrice", shippingPrice);
                startActivity(intent);
            }
        });

        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isStatusChecked) {
                    changeShowroomStatus(isChecked);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStatusChecked = false;
        getShowroomData();
    }

    Boolean isStatusChecked = false;
    private void getShowroomData() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDbRef = mDatabase.getReference().child("Seller").child(uid);


            mDbRef.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                    mDialog.dismiss();
                    HashMap<String, Object> keyId = (HashMap<String, Object>) dataSnapshot.getValue();
                    name = keyId.get("showroomName").toString();
                    address = keyId.get("address").toString();
                    phone = keyId.get("phone").toString();
                    showroomImage = keyId.get("showroomImage").toString();
                    gst = keyId.get("gst").toString();
                    shippingPrice = keyId.get("shippingPrice").toString();
                    Boolean status = (Boolean) keyId.get("status");

                    txtShowroomName.setText(name);
                    txtAddress.setText(address);
                    txtPhone.setText(phone);
                    txtGst.setText(gst);
                    txtShippingPrice.setText("$"+shippingPrice);

                    switchStatus.setChecked(status);
                    if (status) {
                        txtStatus.setText("Open");
                    } else {
                        txtStatus.setText("Close");
                    }

                    Glide.with(SellerProfileActivity.this)
                            .load(showroomImage)
                            .placeholder(R.drawable.logo)
                            .into(imgShowroom);

                    isStatusChecked = true;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mDialog.dismiss();
                    Toast.makeText(SellerProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void changeShowroomStatus(final Boolean status) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        final HashMap<String,Object> map = new HashMap<>();
        map.put("status", status);

        FirebaseDatabase.getInstance().getReference("Seller")
                .child(FirebaseAuth.getInstance().getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (status) {
                                txtStatus.setText("Open");
                            } else {
                                txtStatus.setText("Close");
                            }
                        } else {

                        }
                    }
                });
    }
}
