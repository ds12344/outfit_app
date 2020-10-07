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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.Calendar;
import java.util.HashMap;


public class AddCardActivity extends AppCompatActivity {

    Button btnSave;
    ImageView back;
    EditText edtCreditCardNumber, edtCvc, edtOwnerName;
    TextView txtExpirationDate;
    String cardNumber, expiryDate, cvc, owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        btnSave = findViewById(R.id.btnSave);
        back = findViewById(R.id.back);
        edtCreditCardNumber = findViewById(R.id.edtCreditCardNumber);
        txtExpirationDate = findViewById(R.id.txtExpirationDate);
        edtCvc = findViewById(R.id.edtCvc);
        edtOwnerName = findViewById(R.id.edtOwnerName);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNumber = edtCreditCardNumber.getText().toString();
                expiryDate = txtExpirationDate.getText().toString();
                cvc = edtCvc.getText().toString();
                owner = edtOwnerName.getText().toString();
                if (cardNumber.isEmpty() && expiryDate.isEmpty() && cvc.isEmpty() && owner.isEmpty()) {
                    showToast("Please fill all details");
                } else if (cardNumber.isEmpty()) {
                    showToast("Please enter card number");
                } else if (cardNumber.length() < 16) {
                    showToast("Please enter 16 digit card number");
                } else if (expiryDate.isEmpty()) {
                    showToast("Please select expiry date");
                } else if (cvc.isEmpty()) {
                    showToast("Please enter cvv number");
                } else if (owner.isEmpty()) {
                    showToast("Please enter owner name");
                } else {
                    addCardInfo();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txtExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardExpiryDateClick();
            }
        });

    }

    private void onCardExpiryDateClick() {
        Calendar mCurrentTime = Calendar.getInstance();
        int year = mCurrentTime.get(Calendar.YEAR);
        int month = mCurrentTime.get(Calendar.MONTH);

        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(this, new MonthPickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                txtExpirationDate.setText(""+(selectedMonth+1)+"/"+selectedYear);
            }
        }, year, month);
        builder.setMinYear(year);
        builder.setMaxYear(year+10);
        builder.build().show();
    }

    private void addCardInfo() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        final HashMap<String,Object> map = new HashMap<>();
        map.put("name", owner);
        map.put("cardNumber", cardNumber);
        map.put("expiryDate", expiryDate);
        map.put("cvv", cvc);
        map.put("last4Digit", cardNumber.substring(12, 16));
        String cardId = "card_"+ Calendar.getInstance().getTimeInMillis();
        map.put("cardId", cardId);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference("Card")
                .child(user.getUid())
                .child(cardId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        showToast("Add Card Successfully");
                        finish();
                    }
                });
    }

    void showToast(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
}
