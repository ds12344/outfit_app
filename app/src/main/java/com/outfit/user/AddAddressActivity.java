package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.outfit.user.R;


import java.util.Calendar;
import java.util.HashMap;


public class AddAddressActivity extends AppCompatActivity {

    Button btnSave;
    ImageView back;
    EditText edtName, edtMobile, edtAddress, edtStreet, edtCity, edtState, edtCountry, edtPinCode;
    String strName, strMobile, strAddress, strStreet, strCity, strState, strCountry, strPinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        btnSave = findViewById(R.id.btnSave);
        back = findViewById(R.id.back);
        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtAddress = findViewById(R.id.edtAddress);
        edtStreet = findViewById(R.id.edtStreet);
        edtCity = findViewById(R.id.edtCity);
        edtState = findViewById(R.id.edtState);
        edtCountry = findViewById(R.id.edtCountry);
        edtPinCode = findViewById(R.id.edtPinCode);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = edtName.getText().toString();
                strMobile = edtMobile.getText().toString();
                strAddress = edtAddress.getText().toString();
                strStreet = edtStreet.getText().toString();
                strCity = edtCity.getText().toString();
                strState = edtState.getText().toString();
                strCountry = edtCountry.getText().toString();
                strPinCode = edtPinCode.getText().toString();

                if (strName.isEmpty() && strMobile.isEmpty() && strAddress.isEmpty() && strStreet.isEmpty()
                        && strState.isEmpty() && strCity.isEmpty() && strCountry.isEmpty() && strPinCode.isEmpty()) {
                    showToast("Please fill all details");
                } else if (strName.isEmpty()) {
                    showToast("Please enter name");
                } else if (strMobile.isEmpty()) {
                    showToast("Please enter mobile");
                } else if (strAddress.isEmpty()) {
                    showToast("Please enter address");
                } else if (strStreet.isEmpty()) {
                    showToast("Please enter street/apartment");
                } else if (strCity.isEmpty()) {
                    showToast("Please enter city");
                } else if (strState.isEmpty()) {
                    showToast("Please enter province");
                } else if (strCountry.isEmpty()) {
                    showToast("Please enter country");
                } else if (strPinCode.isEmpty()) {
                    showToast("Please enter postal code");
                } else {
                    addAddressInfo();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addAddressInfo() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        final HashMap<String,Object> map = new HashMap<>();
        map.put("name", strName);
        map.put("mobile", strMobile);
        map.put("address", strAddress);
        map.put("street", strStreet);
        map.put("city", strCity);
        map.put("state", strState);
        map.put("country", strCountry);
        map.put("pincode", strPinCode);
        String addressId = "address_"+ Calendar.getInstance().getTimeInMillis();
        map.put("addressId", addressId);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Address")
                .child(user.getUid())
                .child(addressId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        showToast("Add Address Successfully");
                        finish();
                    }
                });
    }

    void showToast(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
}
