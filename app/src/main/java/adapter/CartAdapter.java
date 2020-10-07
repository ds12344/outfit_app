package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.outfit.app.user.activity.CartActivity;
import com.outfit.user.CartModel;
import com.outfit.user.ProductModel;
import com.outfit.user.R;
import com.outfit.user.ShareStorage;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;


    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel data = ShareStorage.getCartData(context).getProductList().get(position);
        Glide.with(context)
                .load(data.getImage())
                .placeholder(R.drawable.logo)
                .into(holder.imgProduct);

        holder.name.setText(data.getName());
        holder.quantity.setText(""+data.getQuantity());
        holder.price.setText("$"+data.getPrice());
    }

    @Override
    public int getItemCount() {
        if (ShareStorage.getCartData(context) == null) {
            return 0;
        } else {
            return ShareStorage.getCartData(context).getProductList().size();
        }
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, minus, plus;
        TextView name, quantity, price, remove;


        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            name = itemView.findViewById(R.id.txtProductName);
            quantity = itemView.findViewById(R.id.txtQuantity);
            price = itemView.findViewById(R.id.txtPrice);
            remove = itemView.findViewById(R.id.txRemove);

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartModel cartModel = ShareStorage.getCartData(context);
                    if (cartModel.getProductList().get(getAdapterPosition()).getQuantity() > 1) {
                        int k = cartModel.getProductList().get(getAdapterPosition()).getQuantity();
                        k--;
                        cartModel.getProductList().get(getAdapterPosition()).setQuantity(k);
                        ShareStorage.setCartData(context, cartModel);
                        ((CartActivity) context).updatePrice();
                        //notifyDataSetChanged();
                    }
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartModel cartModel = ShareStorage.getCartData(context);
                    int k = cartModel.getProductList().get(getAdapterPosition()).getQuantity();
                    k++;
                    cartModel.getProductList().get(getAdapterPosition()).setQuantity(k);
                    ShareStorage.setCartData(context, cartModel);
                    ((CartActivity) context).updatePrice();
                    //notifyDataSetChanged();
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CartModel cartModel = ShareStorage.getCartData(context);
                    cartModel.getProductList().remove(getAdapterPosition());
                    ShareStorage.setCartData(context, cartModel);
                    ((CartActivity) context).updatePrice();
                    //notifyDataSetChanged();
                }
            });

        }
    }
}
