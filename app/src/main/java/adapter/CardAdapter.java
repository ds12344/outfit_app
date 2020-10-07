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

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    Context context;
    ArrayList<HashMap> list;
    Boolean isChoose;

    public CardAdapter(Context context, Boolean isChoose) {
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
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.txtCardNumber.setText("XXXX-XXXX-XXXX-"+list.get(position).get("last4Digit").toString());
        holder.txtCardName.setText(list.get(position).get("name").toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView txtCardName, txtCardNumber;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCardName = itemView.findViewById(R.id.txtCardName);
            txtCardNumber = itemView.findViewById(R.id.txtCardNumber);

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
