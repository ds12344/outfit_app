package com.outfit.user.seller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.app.R;
import com.outfit.app.seller.adapter.MyProductAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProductActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView rcyProduct;
    MyProductAdapter myProductAdapter;
    ArrayList<HashMap> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rcyProduct = findViewById(R.id.rcyProduct);
        myProductAdapter = new MyProductAdapter(this);
        rcyProduct.setAdapter(myProductAdapter);

        getMyProduct();
    }

    void getMyProduct() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Product").child(user.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e("data", dataSnapshot.toString());
                mDialog.dismiss();
                if (dataSnapshot.getValue() == null)
                    return;
                list.clear();

                HashMap chats = (HashMap) dataSnapshot.getValue();

                for (final Object entry : chats.keySet()) {
                    HashMap product = (HashMap) chats.get(entry);

                    list.add(product);
                }

                myProductAdapter.updateAdapter(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mDialog.dismiss();
            }
        });
    }
}
