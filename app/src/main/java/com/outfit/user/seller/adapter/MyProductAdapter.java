package com.outfit.user.seller.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.outfit.user.R;
import com.outfit.user.seller.SellerProductDetailActivity;


import java.util.ArrayList;
import java.util.HashMap;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder> {

    Context context;
    ArrayList<HashMap> list;

    public MyProductAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateAdapter(ArrayList<HashMap> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_my_product_item, parent, false);
        return new MyProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductViewHolder holder, int position) {
        Glide.with(context)
                .load(list.get(position).get("image").toString())
                .placeholder(R.drawable.logo)
                .into(holder.imgProduct);

        holder.txtName.setText(list.get(position).get("name").toString());
        holder.txtPrice.setText("$"+list.get(position).get("price").toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView txtName, txtPrice;

        public MyProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SellerProductDetailActivity.class);
                    intent.putExtra("data", list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
