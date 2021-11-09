package com.android.findmybus;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder> {

    private final ArrayList<BusModel> list;
    private final Context context;

    public BusAdapter(ArrayList<BusModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BusAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.bus_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusAdapter.ViewHolder viewHolder, int i) {

        final BusModel model = list.get(i);
        viewHolder.name.setText(model.getBusName());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusInfo.class);
                intent.putExtra("busName", model.getBusName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView_busName_busActivity);
            cardView = itemView.findViewById(R.id.cardView_busItem);
        }
    }
}
