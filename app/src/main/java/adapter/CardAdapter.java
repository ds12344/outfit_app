package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.outfit.user.R;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    Context context;

    public CardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
