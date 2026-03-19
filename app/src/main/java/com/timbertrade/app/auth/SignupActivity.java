package com.timbertrade.app.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.timbertrade.app.R;
import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.models.User;
import android.os.Handler;
import android.os.Looper;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    
    private TextInputEditText etFullName, etEmail, etPhoneNumber, etPassword, etConfirmPassword;
    private RadioGroup rgRole;
    private MaterialButton btnSignUp;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        // Initialize SharedPreferences for demo session management
        sharedPreferences = getSharedPreferences("TimberTradePrefs", MODE_PRIVATE);
        
        // Check if user is already logged in (demo mode)
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(SignupActivity.this, RealDashboardActivity.class));
            finish();
            return;
        }
        
        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgRole = findViewById(R.id.rgRole);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
        
        // Set default role to Buyer
        rgRole.check(R.id.rbBuyer);
        
        // Set click listeners
        btnSignUp.setOnClickListener(v -> attemptSignup());
        tvLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }
    
    private void attemptSignup() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // Validate input
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("Phone number is required");
            etPhoneNumber.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }
        
        // Get selected role
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        User.UserRole role;
        if (selectedRoleId == R.id.rbSeller) {
            role = User.UserRole.SELLER;
        } else {
            role = User.UserRole.BUYER;
        }
        
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);
        
        // Simulate signup process (demo mode)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnSignUp.setEnabled(true);
            
            // Create user object with demo ID
            User user = new User(
                    UUID.randomUUID().toString(),
                    fullName,
                    email,
                    phoneNumber,
                    role
            );
            
            // Save login session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userId", user.getUserId());
            editor.putString("userEmail", user.getEmail());
            editor.putString("userName", user.getFullName());
            editor.putString("userRole", user.getRole().toString());
            editor.apply();
            
            Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, RealDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            
        }, 1500); // Simulate network delay
    }
}
