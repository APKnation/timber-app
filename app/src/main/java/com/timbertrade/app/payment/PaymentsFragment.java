package com.timbertrade.app.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.timbertrade.app.R;
import com.timbertrade.app.payment.adapters.TransactionAdapter;
import com.timbertrade.app.models.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentsFragment extends Fragment {
    
    private MaterialButton btnMakePayment, btnViewHistory;
    private LinearLayout layoutMpesa, layoutAirtelMoney, layoutTigoPesa;
    private TextView tvViewAll;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private List<Payment> transactionList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadTransactions();
        
        return view;
    }
    
    private void initViews(View view) {
        btnMakePayment = view.findViewById(R.id.btnMakePayment);
        btnViewHistory = view.findViewById(R.id.btnViewHistory);
        layoutMpesa = view.findViewById(R.id.layoutMpesa);
        layoutAirtelMoney = view.findViewById(R.id.layoutAirtelMoney);
        layoutTigoPesa = view.findViewById(R.id.layoutTigoPesa);
        tvViewAll = view.findViewById(R.id.tvViewAll);
        rvTransactions = view.findViewById(R.id.rvTransactions);
        
        transactionList = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(transactionList, payment -> {
            // TODO: Navigate to payment details
        });
        
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(transactionAdapter);
    }
    
    private void setupClickListeners() {
        btnMakePayment.setOnClickListener(v -> {
            // TODO: Navigate to make payment screen
        });
        
        btnViewHistory.setOnClickListener(v -> {
            // TODO: Navigate to payment history
        });
        
        layoutMpesa.setOnClickListener(v -> {
            // TODO: Initiate M-Pesa payment
            showPaymentDialog(Payment.PaymentMethod.MPESA);
        });
        
        layoutAirtelMoney.setOnClickListener(v -> {
            // TODO: Initiate Airtel Money payment
            showPaymentDialog(Payment.PaymentMethod.AIRTEL_MONEY);
        });
        
        layoutTigoPesa.setOnClickListener(v -> {
            // TODO: Initiate Tigo Pesa payment
            showPaymentDialog(Payment.PaymentMethod.TIGO_PESA);
        });
        
        tvViewAll.setOnClickListener(v -> {
            // TODO: Navigate to full transaction history
        });
    }
    
    private void loadTransactions() {
        // TODO: Load actual transactions from backend
        // For now, load demo data
        loadDemoTransactions();
    }
    
    private void loadDemoTransactions() {
        transactionList.clear();
        transactionList.addAll(com.timbertrade.app.utils.DemoDataGenerator.generateDemoPayments());
        transactionAdapter.notifyDataSetChanged();
    }
    
    private void showPaymentDialog(Payment.PaymentMethod paymentMethod) {
        // TODO: Show payment dialog with selected method
        PaymentDialog dialog = PaymentDialog.newInstance(paymentMethod);
        dialog.show(getChildFragmentManager(), "payment_dialog");
    }
}
