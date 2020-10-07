package com.outfit.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;

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
                    ShareStorage.clear(CartActivity.this);
                    Toast.makeText(CartActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CartActivity.this, RatingActivity.class);
                    startActivity(intent);
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

    public void updatePrice() {
        if ((ShareStorage.getCartData(this) == null)) {
            lnrCart.setVisibility(View.GONE);
        } else {
            if (ShareStorage.getCartData(this).getProductList().isEmpty()) {
                lnrCart.setVisibility(View.GONE);
            } else {
                lnrCart.setVisibility(View.VISIBLE);
                double sum = 0;

                for (int i=0; i<ShareStorage.getCartData(this).getProductList().size(); i++) {
                    int quantity = ShareStorage.getCartData(this).getProductList().get(i).getQuantity();
                    double price = Double.parseDouble(ShareStorage.getCartData(this).getProductList().get(i).getPrice());
                    double tempSum = quantity * price;

                    sum = sum + tempSum;
                }

                txtTotalPrice.setText("$"+sum);
                double shipping = Double.parseDouble(ShareStorage.getCartData(this).getShippingPrice());
                txtShippingCharges.setText("$"+shipping);

                double grand = sum + shipping;
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
}
