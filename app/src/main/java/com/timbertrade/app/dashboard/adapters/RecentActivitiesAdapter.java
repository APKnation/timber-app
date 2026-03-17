package com.timbertrade.app.dashboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;

import java.util.List;

public class RecentActivitiesAdapter extends RecyclerView.Adapter<RecentActivitiesAdapter.RecentActivityViewHolder> {
    
    private List<String> activityList; // Using String for demo
    
    public RecentActivitiesAdapter() {
        // Initialize with empty list for now
    }
    
    @NonNull
    @Override
    public RecentActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_card, parent, false);
        return new RecentActivityViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecentActivityViewHolder holder, int position) {
        // TODO: Bind activity data
        holder.tvActivity.setText("New user registered: Jane Doe");
        holder.tvTimestamp.setText("5 minutes ago");
    }
    
    @Override
    public int getItemCount() {
        return 8; // Return demo count for now
    }
    
    static class RecentActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivity, tvTimestamp;
        
        public RecentActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivity = itemView.findViewById(R.id.tvActivity);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
