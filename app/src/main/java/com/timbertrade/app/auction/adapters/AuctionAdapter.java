package com.timbertrade.app.auction.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.timbertrade.app.R;
import com.timbertrade.app.models.Auction;

import java.util.List;

public class AuctionAdapter extends RecyclerView.Adapter<AuctionAdapter.AuctionViewHolder> {
    
    private List<Auction> auctionList;
    private OnAuctionClickListener listener;
    
    public interface OnAuctionClickListener {
        void onAuctionClick(Auction auction);
        void onQuickBidClick(Auction auction);
    }
    
    public AuctionAdapter(List<Auction> auctionList, OnAuctionClickListener listener) {
        this.auctionList = auctionList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AuctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_auction_card, parent, false);
        return new AuctionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AuctionViewHolder holder, int position) {
        Auction auction = auctionList.get(position);
        holder.bind(auction);
    }
    
    @Override
    public int getItemCount() {
        return auctionList.size();
    }
    
    class AuctionViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private LinearLayout layoutLiveBadge;
        private TextView tvTimeLeft, tvTitle, tvCategory, tvCurrentBid, tvReservePrice;
        private TextView tvBidCount, tvCurrentBidder;
        private com.google.android.material.button.MaterialButton btnQuickBid;
        
        public AuctionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            layoutLiveBadge = itemView.findViewById(R.id.layoutLiveBadge);
            tvTimeLeft = itemView.findViewById(R.id.tvTimeLeft);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvCurrentBid = itemView.findViewById(R.id.tvCurrentBid);
            tvReservePrice = itemView.findViewById(R.id.tvReservePrice);
            tvBidCount = itemView.findViewById(R.id.tvBidCount);
            tvCurrentBidder = itemView.findViewById(R.id.tvCurrentBidder);
            btnQuickBid = itemView.findViewById(R.id.btnQuickBid);
        }
        
        public void bind(Auction auction) {
            // Set basic info
            tvTitle.setText(auction.getProduct().getTitle());
            tvCategory.setText(auction.getProduct().getCategory().getDisplayName());
            
            // Set bidding info
            tvCurrentBid.setText(String.format("TZS %,.0f", auction.getCurrentBid()));
            tvReservePrice.setText(String.format("TZS %,.0f", auction.getReservePrice()));
            tvBidCount.setText(String.format("%d bids", auction.getBidCount()));
            
            // Set current bidder
            if (auction.getCurrentBidderName() != null) {
                tvCurrentBidder.setText(String.format("Highest: %s", auction.getCurrentBidderName()));
            } else {
                tvCurrentBidder.setText("No bids yet");
            }
            
            // Set time left
            long timeLeft = auction.getEndTime().getTime() - System.currentTimeMillis();
            tvTimeLeft.setText(formatTimeLeft(timeLeft));
            
            // Show/hide live badge based on status
            layoutLiveBadge.setVisibility(
                    auction.getStatus() == Auction.AuctionStatus.ACTIVE ? View.VISIBLE : View.GONE
            );
            
            // Load product image
            if (auction.getProduct().getImageUrls() != null && !auction.getProduct().getImageUrls().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(auction.getProduct().getImageUrls().get(0))
                        .placeholder(R.drawable.ic_timber_logo)
                        .error(R.drawable.ic_timber_logo)
                        .centerCrop()
                        .into(ivProductImage);
            } else {
                // Use demo image
                String[] demoImages = com.timbertrade.app.utils.DemoDataGenerator.getDemoImages();
                int imageIndex = Math.abs(auction.getProduct().getTitle().hashCode()) % demoImages.length;
                Glide.with(itemView.getContext())
                        .load(demoImages[imageIndex])
                        .placeholder(R.drawable.ic_timber_logo)
                        .error(R.drawable.ic_timber_logo)
                        .centerCrop()
                        .into(ivProductImage);
            }
            
            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAuctionClick(auction);
                }
            });
            
            btnQuickBid.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuickBidClick(auction);
                }
            });
        }
        
        private String formatTimeLeft(long timeLeftMs) {
            long hours = timeLeftMs / (1000 * 60 * 60);
            long minutes = (timeLeftMs % (1000 * 60 * 60)) / (1000 * 60);
            
            if (hours > 0) {
                return String.format("%dh %dm", hours, minutes);
            } else {
                return String.format("%dm", minutes);
            }
        }
    }
}
