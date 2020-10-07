package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.outfit.user.R;
import com.outfit.user.ShowroomDetailActivity;


import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SellerListAdapter extends RecyclerView.Adapter<SellerListAdapter.SellerListViewHolder> {

    private Context context;
    ArrayList<HashMap> list;

    public SellerListAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public SellerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_seller_list_item, parent, false);
        return new SellerListViewHolder(view);
    }

    public void updateAdapter(ArrayList<HashMap> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SellerListViewHolder holder, int position) {
        Glide.with(context)
                .load(list.get(position).get("showroomImage").toString())
                .placeholder(R.drawable.logo)
                .into(holder.imgShowroom);

        holder.txtName.setText(list.get(position).get("showroomName").toString());
        holder.txtAddress.setText(list.get(position).get("address").toString());
        Boolean status = (Boolean) list.get(position).get("status");
        if (status)
            holder.txtStatus.setText("Open");
        else
            holder.txtStatus.setText("Close");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SellerListViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgShowroom;
        TextView txtName, txtAddress, txtStatus;

        public SellerListViewHolder(@NonNull View itemView) {

            super(itemView);
            imgShowroom = itemView.findViewById(R.id.imgShowroom);
            txtName = itemView.findViewById(R.id.txtShowroomName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean status = (Boolean) list.get(getAdapterPosition()).get("status");
                    if (status) {
                        Intent intent = new Intent(context, ShowroomDetailActivity.class);
                        intent.putExtra("data", list.get(getAdapterPosition()));
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Showroom closed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
