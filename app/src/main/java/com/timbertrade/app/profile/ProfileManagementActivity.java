package com.timbertrade.app.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.auth.LoginActivity;
import com.timbertrade.app.models.User;
import com.timbertrade.app.services.FirebaseAuthService;

public class ProfileManagementActivity extends AppCompatActivity {
    
    private EditText etFullName, etPhoneNumber, etLocation;
    private TextInputLayout tilFullName, tilPhoneNumber, tilLocation;
    private TextView tvEmail, tvRole, tvVerificationStatus;
    private Button btnUpdateProfile, btnLogout, btnVerifyEmail;
    private ProgressBar progressBar;
    
    private FirebaseAuthService authService;
    private User currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);
        
        // Initialize Firebase Auth
        authService = FirebaseAuthService.getInstance();
        authService.setContext(this);
        
        // Check if user is logged in
        if (!authService.isUserLoggedIn()) {
            startActivity(new Intent(ProfileManagementActivity.this, LoginActivity.class));
            finish();
            return;
        }
        
        initViews();
        loadUserProfile();
        setupClickListeners();
    }
    
    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etLocation = findViewById(R.id.etLocation);
        
        tilFullName = findViewById(R.id.tilFullName);
        tilPhoneNumber = findViewById(R.id.tilPhoneNumber);
        tilLocation = findViewById(R.id.tilLocation);
        
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvVerificationStatus = findViewById(R.id.tvVerificationStatus);
        
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnVerifyEmail = findViewById(R.id.btnVerifyEmail);
        
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void loadUserProfile() {
        showProgress(true);
        
        authService.getCurrentUser(new FirebaseAuthService.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                showProgress(false);
                currentUser = user;
                populateProfileFields(user);
            }
            
            @Override
            public void onError(String error) {
                showProgress(false);
                Toast.makeText(ProfileManagementActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void populateProfileFields(User user) {
        etFullName.setText(user.getFullName());
        etPhoneNumber.setText(user.getPhoneNumber());
        etLocation.setText(user.getLocation() != null ? user.getLocation() : "");
        
        tvEmail.setText(user.getEmail());
        tvRole.setText("Role: " + user.getRole());
        
        // Update verification status
        if (authService.isEmailVerified()) {
            tvVerificationStatus.setText("Email Verified ✓");
            tvVerificationStatus.setTextColor(getResources().getColor(R.color.timber_primary));
            btnVerifyEmail.setVisibility(View.GONE);
        } else {
            tvVerificationStatus.setText("Email Not Verified");
            tvVerificationStatus.setTextColor(getResources().getColor(R.color.timber_error));
            btnVerifyEmail.setVisibility(View.VISIBLE);
        }
    }
    
    private void setupClickListeners() {
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnLogout.setOnClickListener(v -> logout());
        btnVerifyEmail.setOnClickListener(v -> resendVerificationEmail());
    }
    
    private void updateProfile() {
        // Reset errors
        tilFullName.setError(null);
        tilPhoneNumber.setError(null);
        tilLocation.setError(null);
        
        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        
        // Validate inputs
        if (!validateInputs(fullName, phoneNumber, location)) {
            return;
        }
        
        // Update user object
        if (currentUser != null) {
            currentUser.setFullName(fullName);
            currentUser.setPhoneNumber(phoneNumber);
            currentUser.setLocation(location);
            
            showProgress(true);
            
            // Update profile in Firebase
            authService.updateUserProfile(currentUser, new FirebaseAuthService.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    showProgress(false);
                    Toast.makeText(ProfileManagementActivity.this, 
                            "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onError(String error) {
                    showProgress(false);
                    Toast.makeText(ProfileManagementActivity.this, 
                            "Failed to update profile: " + error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    
    private boolean validateInputs(String fullName, String phoneNumber, String location) {
        boolean isValid = true;
        
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (fullName.length() < 2) {
            tilFullName.setError("Full name must be at least 2 characters");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(phoneNumber)) {
            tilPhoneNumber.setError("Phone number is required");
            isValid = false;
        } else if (phoneNumber.length() < 10) {
            tilPhoneNumber.setError("Please enter a valid phone number");
            isValid = false;
        }
        
        // Location is optional
        
        return isValid;
    }
    
    private void resendVerificationEmail() {
        showProgress(true);
        
        authService.sendEmailVerification(new FirebaseAuthService.EmailVerificationCallback() {
            @Override
            public void onSuccess() {
                showProgress(false);
                Toast.makeText(ProfileManagementActivity.this, 
                        "Verification email sent! Please check your inbox.", 
                        Toast.LENGTH_LONG).show();
                
                // Update UI
                btnVerifyEmail.setEnabled(false);
                btnVerifyEmail.setText("Email Sent");
            }
            
            @Override
            public void onError(String error) {
                showProgress(false);
                Toast.makeText(ProfileManagementActivity.this, 
                        "Failed to send verification email: " + error, 
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void logout() {
        authService.logoutUser();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(ProfileManagementActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnUpdateProfile.setEnabled(!show);
        btnLogout.setEnabled(!show);
        btnVerifyEmail.setEnabled(!show);
        etFullName.setEnabled(!show);
        etPhoneNumber.setEnabled(!show);
        etLocation.setEnabled(!show);
    }
}
