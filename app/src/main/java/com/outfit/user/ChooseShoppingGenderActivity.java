package com.outfit.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.outfit.user.R;

import java.util.HashMap;

public class ChooseShoppingGenderActivity extends AppCompatActivity {

    LinearLayout lnrMale, lnrFemale;
    HashMap data;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shopping_gender);
        lnrMale = findViewById(R.id.lnrMale);
        lnrFemale = findViewById(R.id.lnrFemale);

        data = (HashMap) getIntent().getSerializableExtra("data");

        lnrMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseShoppingGenderActivity.this, AllProductActivity.class);
                intent.putExtra("data", data);
                intent.putExtra("type", "men");
                startActivity(intent);
            }
        });

        lnrFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseShoppingGenderActivity.this, AllProductActivity.class);
                intent.putExtra("data", data);
                intent.putExtra("type", "women");
                startActivity(intent);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseShoppingGenderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
