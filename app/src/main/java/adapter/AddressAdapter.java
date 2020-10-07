package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.outfit.user.R;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    Context context;
    ArrayList<HashMap> list;
    Boolean isChoose;

    public AddressAdapter(Context context, Boolean isChoose) {
        this.context = context;
        list = new ArrayList<>();
        this.isChoose = isChoose;
    }

    public void updateAdapter(ArrayList<HashMap> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_address_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.txtName.setText(list.get(position).get("name").toString());
        holder.txtAddress.setText(list.get(position).get("address").toString());
        holder.txtStreet.setText(list.get(position).get("street").toString());
        holder.txtCity.setText(list.get(position).get("city").toString());
        holder.txtStatePinCode.setText(list.get(position).get("state").toString() + " - " + list.get(position).get("pincode").toString());
        holder.txtMobile.setText(list.get(position).get("mobile").toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtMobile, txtAddress, txtStreet, txtCity, txtStatePinCode, txtCountry;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtMobile = itemView.findViewById(R.id.txtMobile);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtStreet = itemView.findViewById(R.id.txtStreet);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtStatePinCode = itemView.findViewById(R.id.txtStatePinCode);
            txtCountry = itemView.findViewById(R.id.txtCountry);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isChoose) {
                        Intent intent = new Intent();
                        intent.putExtra("data", list.get(getAdapterPosition()));
                        ((Activity) context).setResult(Activity.RESULT_OK, intent);
                        ((Activity) context).finish();
                    }
                }
            });
        }
    }
}
