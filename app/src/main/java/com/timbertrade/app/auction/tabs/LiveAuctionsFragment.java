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

public class LiveAuctionsFragment extends Fragment {
    
    private RecyclerView rvAuctions;
    private AuctionAdapter auctionAdapter;
    private List<Auction> auctionList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_auctions, container, false);
        
        initViews(view);
        setupRecyclerView();
        loadLiveAuctions();
        
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
    
    private void loadLiveAuctions() {
        // TODO: Load actual live auctions from backend
        // For now, load demo data
        loadDemoAuctions();
    }
    
    private void loadDemoAuctions() {
        auctionList.clear();
        auctionList.addAll(com.timbertrade.app.utils.DemoDataGenerator.generateDemoAuctions());
        auctionAdapter.notifyDataSetChanged();
    }
}
