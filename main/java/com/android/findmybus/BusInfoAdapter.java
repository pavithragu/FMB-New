package com.android.findmybus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class BusInfoAdapter extends RecyclerView.Adapter<BusInfoAdapter.ViewHolder> {

    ArrayList<BusInfoModel> list;
    Context context;

    public BusInfoAdapter(ArrayList<BusInfoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public BusInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BusInfoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.bus_info_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BusInfoAdapter.ViewHolder viewHolder, int i) {
        final BusInfoModel model = list.get(i);
        viewHolder.time.setText(model.getTime());
        viewHolder.stage.setText(model.getStage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView time, stage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time_busInfoItem);
            stage = itemView.findViewById(R.id.stageName_busInfoItem);
            cardView = itemView.findViewById(R.id.cardView_busInfoItem);
        }
    }
}
