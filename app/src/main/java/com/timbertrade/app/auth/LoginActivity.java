package com.timbertrade.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.models.User;
import com.timbertrade.app.services.FirebaseAuthService;

public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView registerLink;
    private ProgressBar progressBar;
    private TextInputLayout tilEmail, tilPassword;
    
    private FirebaseAuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use the brand new XML Layout
        setContentView(R.layout.activity_login);
        
        // Initialize Firebase Auth
        authService = FirebaseAuthService.getInstance();
        authService.setContext(this);
        
        // Check if user is already logged in
        if (authService.isUserLoggedIn()) {
            // Check if email is verified
            if (authService.isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, RealDashboardActivity.class));
                finish();
                return;
            } else {
                // Show email verification message
                Toast.makeText(this, "Please verify your email before logging in", Toast.LENGTH_LONG).show();
            }
        }

        // Bind views
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        
        btnLogin.setOnClickListener(v -> attemptLogin());
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
    
    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        // Reset errors
        tilEmail.setError(null);
        tilPassword.setError(null);
        
        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }
        
        // Show progress
        showProgress(true);
        
        // Attempt login with Firebase
        authService.loginUser(email, password, new FirebaseAuthService.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                showProgress(false);
                
                // Check email verification
                if (!authService.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, 
                            "Please verify your email. Check your inbox for verification link.", 
                            Toast.LENGTH_LONG).show();
                    return;
                }
                
                Toast.makeText(LoginActivity.this, "Welcome back, " + user.getFullName() + "!", 
                        Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(LoginActivity.this, RealDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            
            @Override
            public void onError(String error) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                
                // Set specific field errors based on error message
                if (error.toLowerCase().contains("email")) {
                    tilEmail.setError(error);
                } else if (error.toLowerCase().contains("password")) {
                    tilPassword.setError(error);
                }
            }
        });
    }
    
    private boolean validateInputs(String email, String password) {
        boolean isValid = true;
        
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            isValid = false;
        }
        
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }
        
        return isValid;
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
    }
}
