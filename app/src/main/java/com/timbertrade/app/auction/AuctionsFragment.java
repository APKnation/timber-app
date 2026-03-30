package com.timbertrade.app.auction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.timbertrade.app.R;
import com.timbertrade.app.auction.adapters.AuctionViewPagerAdapter;
import com.timbertrade.app.models.User;

public class AuctionsFragment extends Fragment {
    
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabCreateAuction;
    private AuctionViewPagerAdapter adapter;
    private User currentUser;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auctions, container, false);
        
        initViews(view);
        setupViewPager();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        fabCreateAuction = view.findViewById(R.id.fabCreateAuction);
        
        // TODO: Get current user from parent activity
        // currentUser = ((DashboardActivity) requireActivity()).getCurrentUser();
    }
    
    private void setupViewPager() {
        adapter = new AuctionViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Live Auctions");
                    break;
                case 1:
                    tab.setText("Upcoming");
                    break;
                case 2:
                    tab.setText("Ended");
                    break;
                case 3:
                    tab.setText("My Bids");
                    break;
            }
        }).attach();
    }
    
    private void setupClickListeners() {
        fabCreateAuction.setOnClickListener(v -> {
            // TODO: Navigate to create auction activity
            // Only show for sellers
            if (currentUser != null && currentUser.getRole().equals("SELLER")) {
                // startActivity(new Intent(requireContext(), CreateAuctionActivity.class));
            }
        });
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Show/hide FAB based on user role
        if (fabCreateAuction != null) {
            fabCreateAuction.setVisibility(
                    user != null && user.getRole().equals("SELLER") ? 
                    View.VISIBLE : View.GONE
            );
        }
    }
}
