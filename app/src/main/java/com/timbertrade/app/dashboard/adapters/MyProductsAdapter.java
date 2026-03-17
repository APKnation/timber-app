package com.timbertrade.app.dashboard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;
import com.timbertrade.app.models.Product;

import java.util.List;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.MyProductViewHolder> {
    
    private List<Product> productList;
    
    public MyProductsAdapter() {
        // Initialize with empty list for now
    }
    
    @NonNull
    @Override
    public MyProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_product_small, parent, false);
        return new MyProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MyProductViewHolder holder, int position) {
        // TODO: Bind product data
        holder.tvTitle.setText("Oak Timber Batch");
        holder.tvStatus.setText("Available");
        holder.tvPrice.setText("TZS 650,000");
    }
    
    @Override
    public int getItemCount() {
        return 4; // Return demo count for now
    }
    
    static class MyProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvPrice;
        
        public MyProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
