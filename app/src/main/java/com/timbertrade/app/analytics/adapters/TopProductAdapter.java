package com.timbertrade.app.analytics.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;
import com.timbertrade.app.models.Product;

import java.util.List;

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.TopProductViewHolder> {
    
    private List<Product> productList;
    
    public TopProductAdapter(List<Product> productList) {
        this.productList = productList;
    }
    
    @NonNull
    @Override
    public TopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_product, parent, false);
        return new TopProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TopProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, position + 1);
    }
    
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    static class TopProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRank, tvProductName, tvSalesCount, tvRevenue;
        
        public TopProductViewHolder(@NonNull View itemView) {
            super(itemView);
            
            tvRank = itemView.findViewById(R.id.tvRank);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSalesCount = itemView.findViewById(R.id.tvSalesCount);
            tvRevenue = itemView.findViewById(R.id.tvRevenue);
        }
        
        public void bind(Product product, int rank) {
            // Set rank
            tvRank.setText(String.valueOf(rank));
            
            // Set product name
            tvProductName.setText(product.getTitle());
            
            // Set sales count (demo data)
            tvSalesCount.setText("25 sales");
            
            // Set revenue (demo data)
            tvRevenue.setText(String.format("TZS %.1fM", product.getPrice() * 25 / 1000000));
            
            // Set rank color based on position
            int colorRes;
            switch (rank) {
                case 1:
                    colorRes = R.color.timber_accent; // Gold
                    break;
                case 2:
                    colorRes = R.color.timber_text_secondary; // Silver
                    break;
                case 3:
                    colorRes = R.color.mahogany; // Bronze
                    break;
                default:
                    colorRes = R.color.timber_primary;
                    break;
            }
            tvRank.setBackgroundColor(itemView.getContext().getResources().getColor(colorRes));
        }
    }
}
