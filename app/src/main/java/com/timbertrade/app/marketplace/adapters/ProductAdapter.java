package com.timbertrade.app.marketplace.adapters;

import android.content.Context;
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
    private Context context;
    
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }
    
    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }
    
    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
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
            // Set category
            if (product.getCategory() != null) {
                tvCategory.setText(product.getCategory().toString());
            } else {
                tvCategory.setText("Other");
            }
            
            // Set status
            if (product.getStatus() != null) {
                tvStatus.setText(product.getStatus().toString());
            } else {
                tvStatus.setText("Available");
            }
            
            // Set basic info
            tvTitle.setText(product.getTitle() != null ? product.getTitle() : "Unknown Product");
            tvDescription.setText(product.getDescription() != null ? product.getDescription() : "No description available");
            tvPrice.setText(String.format("TZS %,.0f", product.getPrice()));
            tvQuantity.setText(String.format("%d %s", product.getQuantity(), 
                    product.getQuantityUnit() != null ? product.getQuantityUnit() : "units"));
            tvLocation.setText(product.getLocation() != null ? product.getLocation() : "Unknown Location");
            tvSellerName.setText(product.getSellerName() != null ? product.getSellerName() : "Unknown Seller");
            
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
                // Use placeholder
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_timber_logo)
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
