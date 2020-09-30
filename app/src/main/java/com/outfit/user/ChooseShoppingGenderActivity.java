package com.outfit.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseShoppingGenderActivity extends AppCompatActivity {

    LinearLayout lnrMale, lnrFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shopping_gender);
        lnrMale = findViewById(R.id.lnrMale);
        lnrFemale = findViewById(R.id.lnrFemale);

        lnrMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseShoppingGenderActivity.this, AllProductActivity.class);
                startActivity(intent);
            }
        });

        lnrFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseShoppingGenderActivity.this, AllProductActivity.class);
                startActivity(intent);
            }
        });
    }
}
