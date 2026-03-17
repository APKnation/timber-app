package com.timbertrade.app.marketplace.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.timbertrade.app.R;
import com.timbertrade.app.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Product> productList;
    private OnProductClickListener listener;
    
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }
    
    @Override
    public int getItemCount() {
        return productList.size();
    }
    
    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage, ivSellerImage, ivVerified;
        private TextView tvCategory, tvStatus, tvTitle, tvDescription, tvPrice, tvQuantity;
        private TextView tvLocation, tvRating, tvSellerName;
        
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivSellerImage = itemView.findViewById(R.id.ivSellerImage);
            ivVerified = itemView.findViewById(R.id.ivVerified);
            
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvSellerName = itemView.findViewById(R.id.tvSellerName);
        }
        
        public void bind(Product product) {
            // Set category with color
            tvCategory.setText(product.getCategory().getDisplayName());
            tvCategory.getBackground().setTint(
                    getColorFromString(product.getCategory().getColor())
            );
            
            // Set status
            tvStatus.setText(product.getStatus().getDisplayName());
            
            // Set basic info
            tvTitle.setText(product.getTitle());
            tvDescription.setText(product.getDescription());
            tvPrice.setText(String.format("TZS %,.0f", product.getPrice()));
            tvQuantity.setText(String.format("%d %s", product.getQuantity(), product.getQuantityUnit()));
            tvLocation.setText(product.getLocation());
            tvSellerName.setText(product.getSellerName());
            
            // Set rating
            tvRating.setText(String.format("%.1f (%d)", product.getRating(), product.getReviewCount()));
            
            // Set verification badge
            ivVerified.setVisibility(product.isVerified() ? View.VISIBLE : View.GONE);
            
            // Load product image
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrls().get(0))
                        .placeholder(R.drawable.ic_timber_logo)
                        .error(R.drawable.ic_timber_logo)
                        .centerCrop()
                        .into(ivProductImage);
            } else {
                // Use demo image
                String[] demoImages = com.timbertrade.app.utils.DemoDataGenerator.getDemoImages();
                int imageIndex = Math.abs(product.getTitle().hashCode()) % demoImages.length;
                Glide.with(itemView.getContext())
                        .load(demoImages[imageIndex])
                        .placeholder(R.drawable.ic_timber_logo)
                        .error(R.drawable.ic_timber_logo)
                        .centerCrop()
                        .into(ivProductImage);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
        
        private int getColorFromString(String colorString) {
            try {
                return android.graphics.Color.parseColor(colorString);
            } catch (IllegalArgumentException e) {
                return itemView.getContext().getResources().getColor(R.color.timber_primary);
            }
        }
    }
}
