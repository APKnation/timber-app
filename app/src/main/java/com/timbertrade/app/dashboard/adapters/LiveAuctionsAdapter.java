package com.timbertrade.app.dashboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;
import com.timbertrade.app.models.Auction;

import java.util.List;

public class LiveAuctionsAdapter extends RecyclerView.Adapter<LiveAuctionsAdapter.LiveAuctionViewHolder> {
    
    private List<Auction> auctionList;
    
    public LiveAuctionsAdapter() {
        // Initialize with empty list for now
    }
    
    @NonNull
    @Override
    public LiveAuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_auction_card_small, parent, false);
        return new LiveAuctionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LiveAuctionViewHolder holder, int position) {
        // TODO: Bind auction data
        holder.tvTitle.setText("Premium Teak Auction");
        holder.tvCurrentBid.setText("TZS 1.2M");
        holder.tvTimeLeft.setText("2h 15m");
    }
    
    @Override
    public int getItemCount() {
        return 3; // Return demo count for now
    }
    
    static class LiveAuctionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCurrentBid, tvTimeLeft;
        
        public LiveAuctionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCurrentBid = itemView.findViewById(R.id.tvCurrentBid);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
        }
    }
}
