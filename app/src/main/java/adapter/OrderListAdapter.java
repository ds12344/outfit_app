package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.outfit.user.OrderDetailActivity;
import com.outfit.user.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {

    Context context;
    ArrayList<HashMap> list;

    public OrderListAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void updateAdapter(ArrayList<HashMap> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_order_item, parent, false);
        return new OrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        HashMap sellerData = (HashMap) list.get(position).get("sellerData");
        Glide.with(context)
                .load(sellerData.get("showroomImage"))
                .placeholder(R.drawable.logo)
                .into(holder.imgProduct);

        holder.txtName.setText(sellerData.get("showroomName").toString());
        holder.txtLocation.setText(sellerData.get("address").toString());
        holder.txtItem.setText(list.get(position).get("items").toString());
        if (list.get(position).get("status").toString().equals("Delivered")) {
            holder.txtStatus.setText("Delivered on "+convertTimeStampDateTime((Long) list.get(position).get("deliverAt")));
        } else {
            holder.txtStatus.setText("Status: "+ list.get(position).get("status").toString());
        }
        holder.txtDate.setText(convertTimeStampDateTime((Long) list.get(position).get("createdAt")));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OrderListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtItem, txtStatus, txtLocation, txtDate;
        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtName);
            txtItem = itemView.findViewById(R.id.txtItem);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtDate = itemView.findViewById(R.id.txtDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("orderId", list.get(getAdapterPosition()).get("orderId").toString());
                    intent.putExtra("data", list.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

    public String convertTimeStampDateTime(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date currenTimeZone = new Date(timestamp);
        return sdf.format(currenTimeZone);
    }
}
