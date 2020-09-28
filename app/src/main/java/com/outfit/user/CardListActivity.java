package com.outfit.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import adapter.CardAdapter;


public class CardListActivity extends AppCompatActivity {

    ImageView back, add;
    RecyclerView rcyCard;
    CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        rcyCard = findViewById(R.id.rcyCard);
        cardAdapter = new CardAdapter(this);
        rcyCard.setAdapter(cardAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardListActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });
    }
}
