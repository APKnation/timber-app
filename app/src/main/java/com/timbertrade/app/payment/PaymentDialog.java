package com.timbertrade.app.payment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.models.Payment;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentDialog extends DialogFragment {
    
    private static final String ARG_PAYMENT_METHOD = "payment_method";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_DESCRIPTION = "description";
    
    private Payment.PaymentMethod paymentMethod;
    private double amount;
    private String description;
    
    private ImageView ivPaymentMethod;
    private TextView tvPaymentMethod, tvAmount;
    private TextInputLayout tilPhoneNumber, tilPin, tilDescription;
    private TextInputEditText etPhoneNumber, etPin, etDescription;
    private CheckBox cbTerms;
    private MaterialButton btnCancel, btnPay;
    
    private OnPaymentCompleteListener listener;
    
    public interface OnPaymentCompleteListener {
        void onPaymentComplete(Payment.PaymentMethod method, String phoneNumber, double amount, String description);
    }
    
    public static PaymentDialog newInstance(Payment.PaymentMethod method, double amount, String description) {
        PaymentDialog dialog = new PaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAYMENT_METHOD, method);
        args.putDouble(ARG_AMOUNT, amount);
        args.putString(ARG_DESCRIPTION, description);
        dialog.setArguments(args);
        return dialog;
    }
    
    public static PaymentDialog newInstance(Payment.PaymentMethod method) {
        return newInstance(method, 0, "");
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paymentMethod = (Payment.PaymentMethod) getArguments().getSerializable(ARG_PAYMENT_METHOD);
            amount = getArguments().getDouble(ARG_AMOUNT);
            description = getArguments().getString(ARG_DESCRIPTION);
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Complete Payment");
        return dialog;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_payment, container, false);
        
        initViews(view);
        setupPaymentMethod();
        setupValidation();
        setupClickListeners();
        
        return view;
    }
    
    private void initViews(View view) {
        ivPaymentMethod = view.findViewById(R.id.ivPaymentMethod);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        tvAmount = view.findViewById(R.id.tvAmount);
        tilPhoneNumber = view.findViewById(R.id.tilPhoneNumber);
        tilPin = view.findViewById(R.id.tilPin);
        tilDescription = view.findViewById(R.id.tilDescription);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPin = view.findViewById(R.id.etPin);
        etDescription = view.findViewById(R.id.etDescription);
        cbTerms = view.findViewById(R.id.cbTerms);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnPay = view.findViewById(R.id.btnPay);
        
        // Set amount if provided
        if (amount > 0) {
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            tvAmount.setText(String.format("TZS %s", formatter.format(amount)));
        }
        
        // Set description if provided
        if (description != null && !description.isEmpty()) {
            etDescription.setText(description);
        }
    }
    
    private void setupPaymentMethod() {
        if (paymentMethod != null) {
            tvPaymentMethod.setText(paymentMethod.getDisplayName());
            
            int iconRes;
            switch (paymentMethod) {
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
    }
    
    private void setupValidation() {
        // Phone number validation
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPhoneNumber.setError(null);
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // PIN validation
        etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPin.setError(null);
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnPay.setOnClickListener(v -> attemptPayment());
    }
    
    private void attemptPayment() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String pin = etPin.getText().toString().trim();
        String paymentDescription = etDescription.getText().toString().trim();
        
        // Validate phone number
        if (phoneNumber.isEmpty()) {
            tilPhoneNumber.setError("Phone number is required");
            return;
        }
        
        if (phoneNumber.length() < 9 || phoneNumber.length() > 10) {
            tilPhoneNumber.setError("Enter a valid phone number");
            return;
        }
        
        // Validate PIN
        if (pin.isEmpty()) {
            tilPin.setError("PIN is required");
            return;
        }
        
        if (pin.length() < 4) {
            tilPin.setError("PIN must be at least 4 digits");
            return;
        }
        
        // Check terms
        if (!cbTerms.isChecked()) {
            Toast.makeText(getContext(), "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Process payment
        processPayment(phoneNumber, paymentDescription);
    }
    
    private void processPayment(String phoneNumber, String paymentDescription) {
        // TODO: Implement actual payment processing
        // This would integrate with M-Pesa, Airtel Money, or Tigo Pesa APIs
        
        // For demo purposes, simulate successful payment
        Toast.makeText(getContext(), "Processing payment via " + paymentMethod.getDisplayName() + "...", Toast.LENGTH_SHORT).show();
        
        // Simulate processing delay
        btnPay.setEnabled(false);
        btnCancel.setEnabled(false);
        
        // Simulate payment completion
        if (listener != null) {
            listener.onPaymentComplete(paymentMethod, phoneNumber, amount, paymentDescription);
        }
        
        dismiss();
    }
    
    public void setOnPaymentCompleteListener(OnPaymentCompleteListener listener) {
        this.listener = listener;
    }
}
