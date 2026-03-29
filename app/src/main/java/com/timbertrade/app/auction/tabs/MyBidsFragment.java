package com.timbertrade.app.auction.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;
import com.timbertrade.app.auction.adapters.AuctionAdapter;
import com.timbertrade.app.models.Auction;

import java.util.ArrayList;
import java.util.List;

public class MyBidsFragment extends Fragment {
    
    private RecyclerView rvAuctions;
    private AuctionAdapter auctionAdapter;
    private List<Auction> auctionList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bids, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadMyBids();
        
        return view;
    }
    
    private void initViews(View view) {
        rvAuctions = view.findViewById(R.id.rvAuctions);
        auctionList = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        auctionAdapter = new AuctionAdapter(auctionList, new AuctionAdapter.OnAuctionClickListener() {
            @Override
            public void onAuctionClick(Auction auction) {
                // TODO: Navigate to auction details
            }
            
            @Override
            public void onQuickBidClick(Auction auction) {
                // TODO: Navigate to auction details
            }
        });
        
        rvAuctions.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvAuctions.setAdapter(auctionAdapter);
    }
    
    private void loadMyBids() {
        auctionList.clear();
        // Filter only auctions where current user has placed bids
        List<com.timbertrade.app.models.Auction> allAuctions = com.timbertrade.app.utils.DemoDataGenerator.generateDemoAuctions();
        for (com.timbertrade.app.models.Auction auction : allAuctions) {
            // For demo, show active and ended auctions where user has bid
            if (auction.getCurrentBidderId() != null && 
                (auction.getStatus() == com.timbertrade.app.models.Auction.AuctionStatus.ACTIVE || 
                 auction.getStatus() == com.timbertrade.app.models.Auction.AuctionStatus.ENDED)) {
                auctionList.add(auction);
            }
        }
        auctionAdapter.notifyDataSetChanged();
    }
}
