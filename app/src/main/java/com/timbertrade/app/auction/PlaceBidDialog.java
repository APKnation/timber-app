package com.timbertrade.app.auction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.models.Auction;

import java.text.NumberFormat;
import java.util.Locale;

public class PlaceBidDialog extends DialogFragment {
    
    private static final String ARG_AUCTION = "auction";
    
    private Auction auction;
    private TextView tvAuctionTitle, tvCurrentBid, tvMinNextBid;
    private TextInputLayout tilBidAmount;
    private TextInputEditText etBidAmount;
    private CheckBox cbTerms;
    private MaterialButton btnCancel, btnPlaceBid;
    private MaterialButton btnQuickBid1, btnQuickBid2, btnQuickBid3;
    
    private OnBidPlacedListener listener;
    
    public interface OnBidPlacedListener {
        void onBidPlaced(Auction auction, double bidAmount);
    }
    
    public static PlaceBidDialog newInstance(Auction auction) {
        PlaceBidDialog dialog = new PlaceBidDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_AUCTION, auction);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            auction = (Auction) getArguments().getSerializable(ARG_AUCTION);
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Place Bid");
        return dialog;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_place_bid, container, false);
        
        initViews(view);
        setupAuctionInfo();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        tvAuctionTitle = view.findViewById(R.id.tvAuctionTitle);
        tvCurrentBid = view.findViewById(R.id.tvCurrentBid);
        tvMinNextBid = view.findViewById(R.id.tvMinNextBid);
        tilBidAmount = view.findViewById(R.id.tilBidAmount);
        etBidAmount = view.findViewById(R.id.etBidAmount);
        cbTerms = view.findViewById(R.id.cbTerms);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnPlaceBid = view.findViewById(R.id.btnPlaceBid);
        btnQuickBid1 = view.findViewById(R.id.btnQuickBid1);
        btnQuickBid2 = view.findViewById(R.id.btnQuickBid2);
        btnQuickBid3 = view.findViewById(R.id.btnQuickBid3);
    }
    
    private void setupAuctionInfo() {
        if (auction != null) {
            tvAuctionTitle.setText(auction.getProduct().getTitle());
            
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            tvCurrentBid.setText(String.format("TZS %s", formatter.format(auction.getCurrentBid())));
            
            // Calculate minimum next bid (current bid + minimum increment)
            double minIncrement = 50000; // TZS 50,000 minimum increment
            double minNextBid = auction.getCurrentBid() + minIncrement;
            tvMinNextBid.setText(String.format("TZS %s", formatter.format(minNextBid)));
            
            // Set hint with minimum amount
            etBidAmount.setHint(String.format("Minimum: TZS %s", formatter.format(minNextBid)));
        }
    }
    
    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnPlaceBid.setOnClickListener(v -> attemptPlaceBid());
        
        // Quick bid buttons
        btnQuickBid1.setOnClickListener(v -> addQuickBid(50000));
        btnQuickBid2.setOnClickListener(v -> addQuickBid(100000));
        btnQuickBid3.setOnClickListener(v -> addQuickBid(250000));
    }
    
    private void addQuickBid(double amount) {
        if (auction != null) {
            double minNextBid = auction.getCurrentBid() + 50000;
            double newBid = Math.max(minNextBid, auction.getCurrentBid() + amount);
            etBidAmount.setText(String.format(Locale.US, "%.0f", newBid));
        }
    }
    
    private void attemptPlaceBid() {
        String bidAmountStr = etBidAmount.getText().toString().trim();
        
        if (bidAmountStr.isEmpty()) {
            tilBidAmount.setError("Please enter a bid amount");
            return;
        }
        
        try {
            double bidAmount = Double.parseDouble(bidAmountStr);
            
            if (auction != null) {
                double minNextBid = auction.getCurrentBid() + 50000; // Minimum increment
                
                if (bidAmount < minNextBid) {
                    tilBidAmount.setError(String.format("Minimum bid is TZS %,.0f", minNextBid));
                    return;
                }
            }
            
            if (!cbTerms.isChecked()) {
                Toast.makeText(getContext(), "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Place the bid
            if (listener != null) {
                listener.onBidPlaced(auction, bidAmount);
            }
            
            dismiss();
            
        } catch (NumberFormatException e) {
            tilBidAmount.setError("Please enter a valid amount");
        }
    }
    
    public void setOnBidPlacedListener(OnBidPlacedListener listener) {
        this.listener = listener;
    }
}
