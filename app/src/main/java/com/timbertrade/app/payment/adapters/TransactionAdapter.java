package com.timbertrade.app.payment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;
import com.timbertrade.app.models.Payment;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    
    private List<Payment> paymentList;
    private OnPaymentClickListener listener;
    
    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }
    
    public TransactionAdapter(List<Payment> paymentList, OnPaymentClickListener listener) {
        this.paymentList = paymentList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_card, parent, false);
        return new TransactionViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.bind(payment);
    }
    
    @Override
    public int getItemCount() {
        return paymentList.size();
    }
    
    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPaymentMethod;
        private TextView tvTransactionId, tvAmount, tvDescription, tvStatus, tvDate;
        
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivPaymentMethod = itemView.findViewById(R.id.ivPaymentMethod);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
        
        public void bind(Payment payment) {
            // Set transaction details
            tvTransactionId.setText(payment.getPaymentId());
            tvAmount.setText(String.format("TZS %,.0f", payment.getAmount()));
            tvDescription.setText(payment.getDescription());
            tvStatus.setText(payment.getStatus().getDisplayName());
            
            // Format date
            String timeAgo = formatTimeAgo(payment.getCreatedAt().getTime());
            tvDate.setText(timeAgo);
            
            // Set payment method icon
            setPaymentMethodIcon(payment.getPaymentMethod());
            
            // Set status color
            setStatusColor(payment.getStatus());
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPaymentClick(payment);
                }
            });
        }
        
        private void setPaymentMethodIcon(Payment.PaymentMethod method) {
            int iconRes;
            switch (method) {
                case MPESA:
                    iconRes = R.drawable.ic_mpesa;
                    break;
                case AIRTEL_MONEY:
                    iconRes = R.drawable.ic_airtel_money;
                    break;
                case TIGO_PESA:
                    iconRes = R.drawable.ic_tigo_pesa;
                    break;
                default:
                    iconRes = R.drawable.ic_payment;
                    break;
            }
            ivPaymentMethod.setImageResource(iconRes);
        }
        
        private void setStatusColor(Payment.PaymentStatus status) {
            int colorRes;
            switch (status) {
                case COMPLETED:
                    colorRes = R.color.timber_success;
                    break;
                case PENDING:
                case PROCESSING:
                    colorRes = R.color.timber_warning;
                    break;
                case FAILED:
                case CANCELLED:
                    colorRes = R.color.timber_error;
                    break;
                default:
                    colorRes = R.color.timber_text_secondary;
                    break;
            }
            tvStatus.setTextColor(itemView.getContext().getResources().getColor(colorRes));
        }
        
        private String formatTimeAgo(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
            
            long minutes = diff / (1000 * 60);
            long hours = diff / (1000 * 60 * 60);
            long days = diff / (1000 * 60 * 60 * 24);
            
            if (days > 0) {
                return days + " days ago";
            } else if (hours > 0) {
                return hours + " hours ago";
            } else if (minutes > 0) {
                return minutes + " minutes ago";
            } else {
                return "Just now";
            }
        }
    }
}
