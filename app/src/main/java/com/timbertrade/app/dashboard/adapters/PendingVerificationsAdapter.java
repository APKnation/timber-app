package com.timbertrade.app.dashboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;

import java.util.List;

public class PendingVerificationsAdapter extends RecyclerView.Adapter<PendingVerificationsAdapter.PendingVerificationViewHolder> {
    
    private List<String> verificationList; // Using String for demo
    
    public PendingVerificationsAdapter() {
        // Initialize with empty list for now
    }
    
    @NonNull
    @Override
    public PendingVerificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_verification_card, parent, false);
        return new PendingVerificationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PendingVerificationViewHolder holder, int position) {
        // TODO: Bind verification data
        holder.tvTitle.setText("Premium Mahogany Listing");
        holder.tvSeller.setText("John's Timber");
        holder.tvSubmittedDate.setText("2 hours ago");
    }
    
    @Override
    public int getItemCount() {
        return 5; // Return demo count for now
    }
    
    static class PendingVerificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSeller, tvSubmittedDate;
        
        public PendingVerificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSeller = itemView.findViewById(R.id.tvSeller);
            tvSubmittedDate = itemView.findViewById(R.id.tvSubmittedDate);
        }
    }
}
