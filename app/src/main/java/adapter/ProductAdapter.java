package adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.outfit.user.CartModel;
import com.outfit.user.ProductDetailActivity;
import com.outfit.user.ProductModel;
import com.outfit.user.R;
import com.outfit.user.ShareStorage;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    ArrayList<HashMap> list;
    public ProductAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }
    public void updateAdapter(ArrayList<HashMap> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_product_list_item, parent, false);
        return new ProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
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

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice;
        TextView txtAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAddToCart = itemView.findViewById(R.id.txtAddToCart);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("data", list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            txtAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap product = list.get(getAdapterPosition());
                    if (ShareStorage.getCartData(context) == null) {

                        ProductModel productModel = new ProductModel(product.get("productId").toString(),
                                product.get("name").toString(), product.get("description").toString(),
                                product.get("type").toString(), product.get("price").toString(),
                                product.get("image").toString(), 1);

                        ArrayList<ProductModel> productList = new ArrayList<>();
                        productList.add(productModel);

                        getShippingPrice(product.get("sellerId").toString(), productList);
                    } else {
                        boolean isSameExist = false;
                        CartModel cartModel = ShareStorage.getCartData(context);
                        if (cartModel.getSellerId().equals(product.get("sellerId").toString())) {
                            for (int i=0; i<cartModel.getProductList().size(); i++) {
                                if (cartModel.getProductList().get(i).getProductId().equals(product.get("productId").toString())) {
                                    isSameExist = true;
                                    int k = cartModel.getProductList().get(i).getQuantity();
                                    k++;
                                    cartModel.getProductList().get(i).setQuantity(k);
                                    break;
                                }
                            }

                            if (!isSameExist) {
                                ProductModel productModel = new ProductModel(product.get("productId").toString(),
                                        product.get("name").toString(), product.get("description").toString(),
                                        product.get("type").toString(), product.get("price").toString(),
                                        product.get("image").toString(), 1);
                                cartModel.getProductList().add(productModel);
                            }
                            ShareStorage.setCartData(context, cartModel);
                        } else {
                            ProductModel productModel = new ProductModel(product.get("productId").toString(),
                                    product.get("name").toString(), product.get("description").toString(),
                                    product.get("type").toString(), product.get("price").toString(),
                                    product.get("image").toString(), 1);

                            ArrayList<ProductModel> productList = new ArrayList<>();
                            productList.add(productModel);

                            getShippingPrice(product.get("sellerId").toString(), productList);
                        }
                    }


                    Toast.makeText(context, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getShippingPrice(final String sellerId, final ArrayList<ProductModel> productList) {
        final ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDbRef = mDatabase.getReference().child("Seller").child(sellerId).child("shippingPrice");


        mDbRef.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)  {
                mDialog.dismiss();

                String shippingPrice = dataSnapshot.getValue().toString();

                CartModel cartModel = new CartModel(sellerId, shippingPrice,  productList);
                ShareStorage.setCartData(context, cartModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mDialog.dismiss();
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
