package com.outfit.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyAccountActivity extends AppCompatActivity {

    ImageView back, edit;
    TextView txtChangePassword, txtMyAddress, txtMyCard, txtLogout, txtUsername, txtEmail;
    CircleImageView imgUser;
    String userEmail,image, firstName, lastName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        txtChangePassword = findViewById(R.id.txtChangePassword);
        txtMyAddress = findViewById(R.id.txtMyAddress);
        txtMyCard = findViewById(R.id.txtMyCard);
        txtLogout = findViewById(R.id.txtLogout);
        imgUser = findViewById(R.id.imgUser);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, EditProfileActivity.class);
                intent.putExtra("email", userEmail);
                intent.putExtra("firstName", firstName);
                intent.putExtra("lastName", lastName);
                intent.putExtra("image", image);
                startActivity(intent);
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        txtMyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, AddressListActivity.class);
                startActivity(intent);
            }
        });

        txtMyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyAccountActivity.this, CardListActivity.class);
                startActivity(intent);
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }

    private void getUserData() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String uid = user.getUid();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDbRef = mDatabase.getReference().child("User").child(uid);


            mDbRef.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                    mDialog.dismiss();
                    HashMap<String, String> keyId = (HashMap<String, String>) dataSnapshot.getValue();
                    userEmail = keyId.get("email");
                    firstName = keyId.get("firstName");
                    lastName = keyId.get("lastName");
                    image = keyId.get("image");

                    txtEmail.setText(userEmail);
                    txtUsername.setText(firstName + " " + lastName);

                    Glide.with(MyAccountActivity.this)
                            .load(image)
                            .placeholder(R.drawable.logo)
                            .into(imgUser);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mDialog.dismiss();
                    Toast.makeText(MyAccountActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
