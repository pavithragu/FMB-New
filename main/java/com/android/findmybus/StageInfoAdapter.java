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

import java.util.ArrayList;

public class StageInfoAdapter extends RecyclerView.Adapter<StageInfoAdapter.ViewHolder> {

    ArrayList<StageInfoModel> list;
    Context context;

    public StageInfoAdapter(ArrayList<StageInfoModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public StageInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StageInfoAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.stage_info_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StageInfoAdapter.ViewHolder viewHolder, int i) {
        final StageInfoModel model = list.get(i);
        viewHolder.time.setText(model.timeAndBusName.substring(0,5));
        viewHolder.busName.setText(model.timeAndBusName.substring(6));
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusInfo.class);
                intent.putExtra("busName", model.timeAndBusName.substring(6));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView time, busName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_stageInfoItem);
            time = itemView.findViewById(R.id.time_stageInfoItem);
            busName = itemView.findViewById(R.id.busName_stageInfoItem);
        }
    }
}
