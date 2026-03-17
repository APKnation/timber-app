package com.timbertrade.app.auth;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.timbertrade.app.R;
import com.timbertrade.app.dashboard.SimpleDashboardActivity;
import com.timbertrade.app.models.User;
import com.timbertrade.app.utils.DemoDataGenerator;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    
    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignup;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting LoginActivity - THIS IS THE REAL LOGIN ACTIVITY");
        Toast.makeText(this, "LoginActivity onCreate called!", Toast.LENGTH_LONG).show();
        
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "setContentView: Setting simple layout");
            
            // Create a simple layout programmatically to avoid XML issues
            createSimpleLayout();
            Log.d(TAG, "Simple layout created successfully");
            
            // Initialize SharedPreferences for demo session management
            sharedPreferences = getSharedPreferences("TimberTradePrefs", MODE_PRIVATE);
            Log.d(TAG, "SharedPreferences initialized");
            
            // Check if user is already logged in (demo mode)
            if (sharedPreferences.getBoolean("isLoggedIn", false)) {
                Log.d(TAG, "User already logged in, navigating to dashboard");
                startActivity(new Intent(LoginActivity.this, SimpleDashboardActivity.class));
                finish();
                return;
            }
            
            Log.d(TAG, "LoginActivity setup complete");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            
            // Show error message to user
            try {
                TextView errorText = new TextView(this);
                errorText.setText("Login Error: " + e.getMessage());
                errorText.setPadding(50, 50, 50, 50);
                setContentView(errorText);
            } catch (Exception ex) {
                Log.e(TAG, "Even error display failed: " + ex.getMessage(), ex);
            }
        }
    }
    
    private void createSimpleLayout() {
        Log.d(TAG, "createSimpleLayout: Creating programmatic layout");
        
        // Create main container
        android.widget.LinearLayout mainLayout = new android.widget.LinearLayout(this);
        mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);
        mainLayout.setBackgroundColor(getResources().getColor(R.color.timber_background));
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText("TimberTrade");
        titleText.setTextSize(28);
        titleText.setTextColor(getResources().getColor(R.color.timber_primary));
        titleText.setGravity(android.view.Gravity.CENTER);
        android.widget.LinearLayout.LayoutParams titleParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, 40);
        mainLayout.addView(titleText, titleParams);
        
        // Email input
        TextView emailLabel = new TextView(this);
        emailLabel.setText("Email:");
        emailLabel.setTextSize(16);
        emailLabel.setTextColor(getResources().getColor(R.color.timber_text_primary));
        mainLayout.addView(emailLabel);
        
        etEmail = new EditText(this);
        etEmail.setHint("Enter your email");
        etEmail.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etEmail.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
        etEmail.setPadding(20, 15, 20, 15);
        mainLayout.addView(etEmail);
        
        // Password input
        TextView passwordLabel = new TextView(this);
        passwordLabel.setText("Password:");
        passwordLabel.setTextSize(16);
        passwordLabel.setTextColor(getResources().getColor(R.color.timber_text_primary));
        android.widget.LinearLayout.LayoutParams passwordLabelParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        passwordLabelParams.setMargins(0, 20, 0, 0);
        mainLayout.addView(passwordLabel, passwordLabelParams);
        
        etPassword = new EditText(this);
        etPassword.setHint("Enter your password");
        etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
        etPassword.setPadding(20, 15, 20, 15);
        mainLayout.addView(etPassword);
        
        // Login button
        btnLogin = new Button(this);
        btnLogin.setText("Login");
        btnLogin.setBackgroundColor(getResources().getColor(R.color.timber_primary));
        btnLogin.setTextColor(getResources().getColor(R.color.white));
        btnLogin.setPadding(0, 20, 0, 20);
        android.widget.LinearLayout.LayoutParams loginParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        loginParams.setMargins(0, 30, 0, 0);
        btnLogin.setOnClickListener(v -> attemptLogin());
        mainLayout.addView(btnLogin, loginParams);
        
        // Signup button
        btnSignup = new Button(this);
        btnSignup.setText("Create Account");
        btnSignup.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        btnSignup.setTextColor(getResources().getColor(R.color.timber_primary));
        btnSignup.setPadding(0, 10, 0, 10);
        btnSignup.setOnClickListener(v -> {
            Log.d(TAG, "Signup clicked");
            Toast.makeText(LoginActivity.this, "Signup feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        mainLayout.addView(btnSignup);
        
        // Forgot password
        tvForgotPassword = new TextView(this);
        tvForgotPassword.setText("Forgot Password?");
        tvForgotPassword.setTextColor(getResources().getColor(R.color.timber_primary));
        tvForgotPassword.setGravity(android.view.Gravity.CENTER);
        tvForgotPassword.setClickable(true);
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
        android.widget.LinearLayout.LayoutParams forgotParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        forgotParams.setMargins(0, 20, 0, 0);
        mainLayout.addView(tvForgotPassword, forgotParams);
        
        // Progress bar
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        android.widget.LinearLayout.LayoutParams progressParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        progressParams.setMargins(0, 20, 0, 0);
        progressParams.gravity = android.view.Gravity.CENTER;
        mainLayout.addView(progressBar, progressParams);
        
        setContentView(mainLayout);
        Log.d(TAG, "Simple layout setup complete");
    }
    
    private void attemptLogin() {
        Log.d(TAG, "attemptLogin: Starting login attempt");
        
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        Log.d(TAG, "Email: " + email + ", Password length: " + password.length());
        
        // Validate input
        if (TextUtils.isEmpty(email)) {
            Log.w(TAG, "Email is empty");
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.w(TAG, "Invalid email format");
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            Log.w(TAG, "Password is empty");
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        
        if (password.length() < 6) {
            Log.w(TAG, "Password too short: " + password.length() + " chars");
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        
        // Show progress bar
        Log.d(TAG, "Showing progress bar");
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        
        // Simulate login process (demo mode)
        Log.d(TAG, "Starting demo authentication");
        new android.os.Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            
            // Demo authentication - accept any valid email/password
            if (isValidDemoCredentials(email, password)) {
                Log.d(TAG, "Demo authentication successful");
                
                // Find or create user
                User user = findDemoUser(email);
                
                // Save login session
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("userId", user != null ? user.getUserId() : "user_" + System.currentTimeMillis());
                editor.putString("userEmail", email);
                editor.putString("userName", user != null ? user.getFullName() : email.split("@")[0]);
                editor.putString("userRole", user != null ? user.getRole().toString() : User.UserRole.BUYER.toString());
                editor.apply();
                
                Log.d(TAG, "Login session saved");
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                
                // Navigate to dashboard
                try {
                    Log.d(TAG, "Starting DashboardActivity");
                    Intent intent = new Intent(LoginActivity.this, com.timbertrade.app.dashboard.DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Log.d(TAG, "DashboardActivity started");
                    finish();
                    Log.d(TAG, "LoginActivity finished");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting DashboardActivity: " + e.getMessage(), e);
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Log.w(TAG, "Invalid demo credentials");
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            }
        }, 1500); // Simulate network delay
    }
    
    private boolean isValidDemoCredentials(String email, String password) {
        // Demo mode: accept any valid email and password with 6+ characters
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 6;
    }
    
    private User findDemoUser(String email) {
        List<User> demoUsers = DemoDataGenerator.generateDemoUsers();
        for (User user : demoUsers) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }
    
    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email address");
            etEmail.requestFocus();
            return;
        }
        
        // Demo mode: simulate password reset
        Toast.makeText(LoginActivity.this, "Password reset instructions sent to " + email, Toast.LENGTH_SHORT).show();
    }
}
