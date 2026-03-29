package com.timbertrade.app.dashboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;

import java.util.List;

public class RecentOrdersAdapter extends RecyclerView.Adapter<RecentOrdersAdapter.RecentOrderViewHolder> {
    
    private List<String> orderList; // Using String for demo
    
    public RecentOrdersAdapter() {
        // Initialize with empty list for now
    }
    
    @NonNull
    @Override
    public RecentOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_card_small, parent, false);
        return new RecentOrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecentOrderViewHolder holder, int position) {
        // TODO: Bind order data
        holder.tvOrderNumber.setText("#ORD-2024-001");
        holder.tvCustomer.setText("Customer Name");
        holder.tvAmount.setText("TZS 1.5M");
        holder.tvStatus.setText("Completed");
    }
    
    @Override
    public int getItemCount() {
        return 3; // Return demo count for now
    }
    
    static class RecentOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvCustomer, tvAmount, tvStatus;
        
        public RecentOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvCustomer = itemView.findViewById(R.id.tvCustomer);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
