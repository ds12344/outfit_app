package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.outfit.user.R;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    Context context;
    ArrayList<HashMap> list;

    public OrderItemAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateAdapter(ArrayList<HashMap> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_order_item_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        Glide.with(context)
                .load(list.get(position).get("image"))
                .placeholder(R.drawable.logo)
                .into(holder.imgProduct);

        holder.txtName.setText(list.get(position).get("name").toString());
        holder.txtPrice.setText("Price: $"+list.get(position).get("price").toString());
        holder.txtQuantity.setText("Quantity: "+ list.get(position).get("quantity").toString());
        double grand = Double.parseDouble(list.get(position).get("price").toString()) * Double.parseDouble(list.get(position).get("quantity").toString());
        holder.txtTotalPrice.setText("Total Price: $"+ grand);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice, txtQuantity, txtTotalPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
        }
    }
}
