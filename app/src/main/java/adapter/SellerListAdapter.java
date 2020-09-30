package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.outfit.user.ChooseShoppingGenderActivity;
import com.outfit.user.R;


public class SellerListAdapter extends RecyclerView.Adapter<SellerListAdapter.SellerListViewHolder> {

    private Context context;

    public SellerListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SellerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_seller_list_item, parent, false);
        return new SellerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class SellerListViewHolder extends RecyclerView.ViewHolder {

        public SellerListViewHolder(@NonNull View itemView) {

            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChooseShoppingGenderActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    }
}
