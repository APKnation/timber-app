package com.timbertrade.app.auth;

import android.content.Intent;
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
import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.models.User;
import com.timbertrade.app.services.FirebaseAuthService;

public class SignupActivity extends AppCompatActivity {
    
    private TextInputEditText etFullName, etEmail, etPhoneNumber, etPassword, etConfirmPassword;
    private TextInputLayout tilFullName, tilEmail, tilPhoneNumber, tilPassword, tilConfirmPassword;
    private RadioGroup rgRole;
    private MaterialButton btnSignUp;
    private TextView tvLogin;
    private ProgressBar progressBar;
    
    private FirebaseAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        // Initialize Firebase Auth
        authService = FirebaseAuthService.getInstance();
        authService.setContext(this);
        
        // Check if user is already logged in
        if (authService.isUserLoggedIn()) {
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
        
        tilFullName = findViewById(R.id.tilFullName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        rgRole = findViewById(R.id.rgRole);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
        
        btnSignUp.setOnClickListener(v -> attemptSignup());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
    
    private void attemptSignup() {
        // Reset errors
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPhoneNumber.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        
        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // Get selected role
        String role = "BUYER"; // Default
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        if (selectedRoleId == R.id.rbSeller) {
            role = "SELLER";
        }
        
        // Validate inputs
        if (!validateInputs(fullName, email, phoneNumber, password, confirmPassword)) {
            return;
        }
        
        // Show progress
        showProgress(true);
        
        // Attempt registration with Firebase
        authService.registerUser(email, password, fullName, phoneNumber, role, 
                new FirebaseAuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {
                        showProgress(false);
                        
                        Toast.makeText(SignupActivity.this, 
                                "Account created successfully! Please check your email for verification.", 
                                Toast.LENGTH_LONG).show();
                        
                        // Navigate to login
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    
                    @Override
                    public void onError(String error) {
                        showProgress(false);
                        Toast.makeText(SignupActivity.this, error, Toast.LENGTH_LONG).show();
                        
                        // Set specific field errors based on error message
                        if (error.toLowerCase().contains("email")) {
                            tilEmail.setError(error);
                        } else if (error.toLowerCase().contains("password")) {
                            tilPassword.setError(error);
                        }
                    }
                });
    }
    
    private boolean validateInputs(String fullName, String email, String phoneNumber, 
                                 String password, String confirmPassword) {
        boolean isValid = true;
        
        // Validate full name
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (fullName.length() < 2) {
            tilFullName.setError("Full name must be at least 2 characters");
            isValid = false;
        }
        
        // Validate email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        
        // Validate phone number
        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhoneNumber.setError("Phone number is required");
            isValid = false;
        } else if (phoneNumber.length() < 10) {
            tilPhoneNumber.setError("Please enter a valid phone number");
            isValid = false;
        }
        
        // Validate password
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        
        return isValid;
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!show);
        etFullName.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPhoneNumber.setEnabled(!show);
        etPassword.setEnabled(!show);
        etConfirmPassword.setEnabled(!show);
        rgRole.setEnabled(!show);
    }
}
