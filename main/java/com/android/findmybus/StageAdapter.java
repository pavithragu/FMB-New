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

public class StageAdapter extends RecyclerView.Adapter<StageAdapter.ViewHolder> {

    ArrayList<StageModel> list;
    Context context;

    public StageAdapter(ArrayList<StageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public StageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StageAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.stage_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StageAdapter.ViewHolder viewHolder, int i) {
        final StageModel model = list.get(i);
        viewHolder.stageName.setText(model.getStageName());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StageInfoActivity.class);
                intent.putExtra("stageName", model.getStageName());
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
        TextView stageName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_stageActivity);
            stageName = itemView.findViewById(R.id.textView_stageName_stageActivity);
        }
    }
}
