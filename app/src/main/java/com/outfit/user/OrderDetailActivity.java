package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import adapter.OrderItemAdapter;

public class OrderDetailActivity extends AppCompatActivity {

    ImageView back;
    HashMap data;
    ArrayList<HashMap> itemList = new ArrayList();
    RecyclerView rcyItems;
    OrderItemAdapter orderItemAdapter;
    ImageView showroomImage;
    TextView txtName, txtLocation, txtDate, txtStatus, txtShippingName, txtAddress, txtStreet, txtCity, txtStatePinCode,
            txtCountry, txtMobile, txtTotalPrice, txtShippingCharges, txtGrandTotal;
    Button btnCancelOrder, btnAcceptedOrder, btnPackedOrder, btnShippedOrder, btnDeliveredOrder;
    LinearLayout lnrSeller;
    String orderId = "";
    String table = "OrderBuyer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        data = (HashMap) getIntent().getSerializableExtra("data");
        orderId = getIntent().getStringExtra("orderId");
        HashMap sellerData = (HashMap) data.get("sellerData");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (sellerData.get("sellerId").toString().equals(user.getUid())) {
            table = "OrderSeller";
        } else {
            table = "OrderBuyer";
        }

        back = findViewById(R.id.back);
        rcyItems = findViewById(R.id.rcyItems);
        showroomImage = findViewById(R.id.imgProduct);
        txtName = findViewById(R.id.txtName);
        txtLocation = findViewById(R.id.txtLocation);
        txtDate = findViewById(R.id.txtDate);
        txtStatus = findViewById(R.id.txtStatus);
        txtShippingName = findViewById(R.id.txtShippingName);
        txtAddress = findViewById(R.id.txtAddress);
        txtStreet = findViewById(R.id.txtStreet);
        txtCity = findViewById(R.id.txtCity);
        txtStatePinCode = findViewById(R.id.txtStatePinCode);
        txtCountry = findViewById(R.id.txtCountry);
        txtMobile = findViewById(R.id.txtMobile);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtShippingCharges = findViewById(R.id.txtShippingCharges);
        txtGrandTotal = findViewById(R.id.txtGrandTotal);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        lnrSeller = findViewById(R.id.lnrSeller);
        btnAcceptedOrder = findViewById(R.id.btnAcceptedOrder);
        btnPackedOrder = findViewById(R.id.btnPackedOrder);
        btnShippedOrder = findViewById(R.id.btnShippedOrder);
        btnDeliveredOrder = findViewById(R.id.btnDeliveredOrder);


        orderItemAdapter = new OrderItemAdapter(this);
        rcyItems.setAdapter(orderItemAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus("Cancelled");
            }
        });

        btnAcceptedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus("Accepted");
            }
        });

        btnPackedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus("Packed");
            }
        });

        btnShippedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus("Shipped");
            }
        });

        btnDeliveredOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrderStatus("Delivered");
            }
        });

        setData();

        getOrderData();

    }

    private void getItemList() {
        itemList.clear();
        HashMap productData = (HashMap) data.get("product");
        for (final Object entry : productData.keySet()) {
            HashMap item = (HashMap) productData.get(entry);
            itemList.add(item);
        }

        orderItemAdapter.updateAdapter(itemList);
    }

    private void setData() {
        HashMap sellerData = (HashMap) data.get("sellerData");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (sellerData.get("sellerId").toString().equals(user.getUid())) {
            lnrSeller.setVisibility(View.GONE);
            if (data.get("status").toString().equals("pending")) {
                btnAcceptedOrder.setVisibility(View.VISIBLE);
                btnPackedOrder.setVisibility(View.GONE);
                btnShippedOrder.setVisibility(View.GONE);
                btnDeliveredOrder.setVisibility(View.GONE);
            } else if (data.get("status").toString().equals("Accepted")) {
                btnAcceptedOrder.setVisibility(View.GONE);
                btnPackedOrder.setVisibility(View.VISIBLE);
                btnShippedOrder.setVisibility(View.GONE);
                btnDeliveredOrder.setVisibility(View.GONE);
            } else if (data.get("status").toString().equals("Packed")) {
                btnAcceptedOrder.setVisibility(View.GONE);
                btnPackedOrder.setVisibility(View.GONE);
                btnShippedOrder.setVisibility(View.VISIBLE);
                btnDeliveredOrder.setVisibility(View.GONE);
            } else if (data.get("status").toString().equals("Shipped")) {
                btnAcceptedOrder.setVisibility(View.GONE);
                btnPackedOrder.setVisibility(View.GONE);
                btnShippedOrder.setVisibility(View.GONE);
                btnDeliveredOrder.setVisibility(View.VISIBLE);
            } else {
                btnAcceptedOrder.setVisibility(View.GONE);
                btnPackedOrder.setVisibility(View.GONE);
                btnShippedOrder.setVisibility(View.GONE);
                btnDeliveredOrder.setVisibility(View.GONE);
            }
        } else {
            lnrSeller.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(sellerData.get("showroomImage").toString())
                    .placeholder(R.drawable.logo)
                    .into(showroomImage);

            txtName.setText(sellerData.get("showroomName").toString());
            txtLocation.setText(sellerData.get("address").toString());

            if (data.get("status").toString().equals("pending")) {
                btnCancelOrder.setVisibility(View.VISIBLE);
            } else {
                btnCancelOrder.setVisibility(View.GONE);
            }
        }

        txtStatus.setText(data.get("status").toString());

        getItemList();

        txtDate.setText(convertTimeStampDateTime((Long) data.get("createdAt")));

        HashMap shippingData = (HashMap) data.get("shippingAddress");

        txtShippingName.setText(shippingData.get("name").toString());
        txtAddress.setText(shippingData.get("address").toString());
        txtStreet.setText(shippingData.get("street").toString());
        txtCity.setText(shippingData.get("city").toString());
        txtStatePinCode.setText(shippingData.get("state").toString() + " - " + shippingData.get("pincode").toString());
        txtMobile.setText(shippingData.get("mobile").toString());

        txtTotalPrice.setText("$"+data.get("total").toString());
        txtShippingCharges.setText("$"+data.get("shippingCharge").toString());
        txtGrandTotal.setText("$"+data.get("grand").toString());
    }

    public String convertTimeStampDateTime(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date currenTimeZone = new Date(timestamp);
        return sdf.format(currenTimeZone);
    }

    private void getOrderData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDbRef = mDatabase.getReference().child(table).child(uid).child(orderId);

            mDbRef.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                    data = (HashMap) dataSnapshot.getValue();
                    setData();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(OrderDetailActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void changeOrderStatus(final String status) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        final HashMap<String,Object> map = new HashMap<>();
        map.put("status", status);
        if (status.equals("Delivered")) {
            map.put("deliverAt", Calendar.getInstance().getTimeInMillis());
        }

        FirebaseDatabase.getInstance().getReference("OrderBuyer")
                .child(data.get("userId").toString())
                .child(orderId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        HashMap sellerData = (HashMap) data.get("sellerData");
                        FirebaseDatabase.getInstance().getReference("OrderSeller")
                                .child(sellerData.get("sellerId").toString())
                                .child(orderId)
                                .updateChildren(map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDialog.dismiss();
                                        Toast.makeText(OrderDetailActivity.this, "Order "+ status + " Successfully", Toast.LENGTH_SHORT).show();
                                        btnCancelOrder.setVisibility(View.GONE);
                                    }
                                });
                    }
                });
    }
}
