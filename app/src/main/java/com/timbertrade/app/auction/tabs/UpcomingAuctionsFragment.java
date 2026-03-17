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

public class UpcomingAuctionsFragment extends Fragment {
    
    private RecyclerView rvAuctions;
    private AuctionAdapter auctionAdapter;
    private List<Auction> auctionList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_auctions, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadUpcomingAuctions();
        
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
    
    private void loadUpcomingAuctions() {
        auctionList.clear();
        // Filter only upcoming auctions
        List<com.timbertrade.app.models.Auction> allAuctions = com.timbertrade.app.utils.DemoDataGenerator.generateDemoAuctions();
        for (com.timbertrade.app.models.Auction auction : allAuctions) {
            if (auction.getStatus() == com.timbertrade.app.models.Auction.AuctionStatus.UPCOMING) {
                auctionList.add(auction);
            }
        }
        auctionAdapter.notifyDataSetChanged();
    }
}
