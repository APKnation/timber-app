package com.timbertrade.app.auction.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.timbertrade.app.auction.AuctionsFragment;
import com.timbertrade.app.auction.tabs.EndedAuctionsFragment;
import com.timbertrade.app.auction.tabs.LiveAuctionsFragment;
import com.timbertrade.app.auction.tabs.MyBidsFragment;
import com.timbertrade.app.auction.tabs.UpcomingAuctionsFragment;

public class AuctionViewPagerAdapter extends FragmentStateAdapter {
    
    public AuctionViewPagerAdapter(@NonNull AuctionsFragment fragment) {
        super(fragment);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LiveAuctionsFragment();
            case 1:
                return new UpcomingAuctionsFragment();
            case 2:
                return new EndedAuctionsFragment();
            case 3:
                return new MyBidsFragment();
            default:
                return new LiveAuctionsFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 4; // 4 tabs
    }
}
