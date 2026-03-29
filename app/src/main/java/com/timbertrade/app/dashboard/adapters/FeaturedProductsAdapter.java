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

public class FeaturedProductsAdapter extends RecyclerView.Adapter<FeaturedProductsAdapter.FeaturedProductViewHolder> {
    
    private List<Product> productList;
    
    public FeaturedProductsAdapter() {
        // Initialize with empty list for now
    }
    
    @NonNull
    @Override
    public FeaturedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_featured_product_small, parent, false);
        return new FeaturedProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull FeaturedProductViewHolder holder, int position) {
        // TODO: Bind product data
        holder.tvTitle.setText("Premium Mahogany");
        holder.tvPrice.setText("TZS 850,000");
    }
    
    @Override
    public int getItemCount() {
        return 5; // Return demo count for now
    }
    
    static class FeaturedProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice;
        
        public FeaturedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
