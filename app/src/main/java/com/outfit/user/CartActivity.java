package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.outfit.user.R;

import adapter.CartAdapter;

public class CartActivity extends AppCompatActivity {

    CartAdapter cartAdapter;
    RecyclerView recyclerView;
    TextView txtTotalPrice, txtShippingCharges, txtGrandTotal;
    LinearLayout lnrCart, lnrShippingAddress, lnrCardDetail;
    Button btnCheckout;
    ImageView back;
    TextView txtName, txtMobile, txtAddress, txtStreet, txtCity, txtStatePinCode, txtCountry;
    TextView txtChangeAddress, txtChangeCard;
    TextView txtCardName, txtCardNumber;
    HashMap shippingAddressData, cardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.rcyCart);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtShippingCharges = findViewById(R.id.txtShippingCharges);
        txtGrandTotal = findViewById(R.id.txtGrandTotalPrice);
        lnrCart = findViewById(R.id.lnrCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        back = findViewById(R.id.back);
        lnrShippingAddress = findViewById(R.id.lnrShippingAddress);
        lnrCardDetail = findViewById(R.id.lnrCardDetail);
        txtName = findViewById(R.id.txtName);
        txtMobile = findViewById(R.id.txtMobile);
        txtAddress = findViewById(R.id.txtAddress);
        txtStreet = findViewById(R.id.txtStreet);
        txtCity = findViewById(R.id.txtCity);
        txtStatePinCode = findViewById(R.id.txtStatePinCode);
        txtCountry = findViewById(R.id.txtCountry);
        txtChangeAddress = findViewById(R.id.txtChangeAddress);
        txtCardName = findViewById(R.id.txtCardName);
        txtCardNumber = findViewById(R.id.txtCardNumber);
        txtChangeCard = findViewById(R.id.txtChangeCard);

        cartAdapter = new CartAdapter(this);
        recyclerView.setAdapter(cartAdapter);

        txtChangeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AddressListActivity.class);
                intent.putExtra("isSelect", true);
                startActivityForResult(intent, 101);
            }
        });

        txtChangeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CardListActivity.class);
                intent.putExtra("isSelect", true);
                startActivityForResult(intent, 102);
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingAddressData == null) {
                    Intent intent = new Intent(CartActivity.this, AddressListActivity.class);
                    intent.putExtra("isSelect", true);
                    startActivityForResult(intent, 101);
                } else if (cardData == null) {
                    Intent intent = new Intent(CartActivity.this, CardListActivity.class);
                    intent.putExtra("isSelect", true);
                    startActivityForResult(intent, 102);
                } else {

                    getSellerData(ShareStorage.getCartData(CartActivity.this).getSellerId());
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updatePrice();
    }

    double sum = 0, shipping = 0, grand = 0;
    public void updatePrice() {
        if ((ShareStorage.getCartData(this) == null)) {
            lnrCart.setVisibility(View.GONE);
        } else {
            if (ShareStorage.getCartData(this).getProductList().isEmpty()) {
                lnrCart.setVisibility(View.GONE);
            } else {
                lnrCart.setVisibility(View.VISIBLE);
                sum = 0;
                for (int i=0; i<ShareStorage.getCartData(this).getProductList().size(); i++) {
                    int quantity = ShareStorage.getCartData(this).getProductList().get(i).getQuantity();
                    double price = Double.parseDouble(ShareStorage.getCartData(this).getProductList().get(i).getPrice());
                    double tempSum = quantity * price;

                    sum = sum + tempSum;
                }

                txtTotalPrice.setText("$"+sum);
                shipping = Double.parseDouble(ShareStorage.getCartData(this).getShippingPrice());
                txtShippingCharges.setText("$"+shipping);

                grand = sum + shipping;
                txtGrandTotal.setText("$"+grand);

                cartAdapter.notifyDataSetChanged();
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 101 && data!=null) {
            lnrShippingAddress.setVisibility(View.VISIBLE);
            shippingAddressData = (HashMap) data.getSerializableExtra("data");
            txtName.setText(shippingAddressData.get("name").toString());
            txtAddress.setText(shippingAddressData.get("address").toString());
            txtStreet.setText(shippingAddressData.get("street").toString());
            txtCity.setText(shippingAddressData.get("city").toString());
            txtStatePinCode.setText(shippingAddressData.get("state").toString() + " - " + shippingAddressData.get("pincode").toString());
            txtMobile.setText(shippingAddressData.get("mobile").toString());

        } else if (resultCode == RESULT_OK && requestCode == 102 && data!=null) {
            lnrCardDetail.setVisibility(View.VISIBLE);
            cardData = (HashMap) data.getSerializableExtra("data");

            txtCardNumber.setText("XXXX-XXXX-XXXX-"+cardData.get("last4Digit").toString());
            txtCardName.setText(cardData.get("name").toString());

            btnCheckout.setText("Place Order");
        }
    }

    private void placeOrder() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final HashMap<String,Object> map = new HashMap<>();
        CartModel data = ShareStorage.getCartData(this);
        String orderId = "order_"+ Calendar.getInstance().getTimeInMillis();
        map.put("orderId", orderId);
        map.put("sellerId", data.getSellerId());
        map.put("total", sum);
        map.put("shippingCharge", shipping);
        map.put("grand", grand);
        map.put("shippingAddress", shippingAddressData);
        map.put("cardData", cardData);

        String item = "";
        HashMap<String, Object> product = new HashMap<>();
        for (int i=0; i<data.getProductList().size(); i++) {
            ProductModel productModel = data.getProductList().get(i);
            HashMap<String, Object> productData = new HashMap<>();
            productData.put("productId", productModel.getProductId());
            productData.put("name", productModel.getName());
            productData.put("price", productModel.getPrice());
            productData.put("image", productModel.getImage());
            productData.put("type", productModel.getType());
            productData.put("quantity", productModel.getQuantity());
            product.put(productModel.getProductId(), productData);

            if (item.isEmpty()) {
                item = productModel.getQuantity() + " x " + productModel.getName();
            } else {
                item = item + ", " + productModel.getQuantity() + " x " + productModel.getName();
            }

        }
        map.put("product", product);
        map.put("status", "pending");
        map.put("userId", user.getUid());
        map.put("sellerData", sellerData);
        map.put("userData", userData);
        map.put("createdAt", Calendar.getInstance().getTimeInMillis());
        map.put("items", item);


        FirebaseDatabase.getInstance().getReference("OrderBuyer")
                .child(user.getUid())
                .child(orderId)
                .setValue(map);

        FirebaseDatabase.getInstance().getReference("OrderSeller")
                .child(data.getSellerId())
                .child(orderId)
                .setValue(map);



        ShareStorage.clear(CartActivity.this);
        Toast.makeText(CartActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CartActivity.this, RatingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    HashMap sellerData = new HashMap();
    private void getSellerData(final String sellerId) {
        final ProgressDialog mDialog = new ProgressDialog(CartActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDbRef = mDatabase.getReference().child("Seller").child(sellerId);


        mDbRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                mDialog.dismiss();

                sellerData = (HashMap) dataSnapshot.getValue();
                getUserData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
                Toast.makeText(CartActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    HashMap userData = new HashMap();
    private void getUserData() {
        final ProgressDialog mDialog = new ProgressDialog(CartActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDbRef = mDatabase.getReference().child("User").child(user.getUid());


        mDbRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                mDialog.dismiss();

                userData = (HashMap) dataSnapshot.getValue();
                placeOrder();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
                Toast.makeText(CartActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
