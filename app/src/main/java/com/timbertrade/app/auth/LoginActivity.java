package com.timbertrade.app.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.timbertrade.app.R;
import com.timbertrade.app.dashboard.RealDashboardActivity;

public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView registerLink, tvForgotPassword;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Use the brand new XML Layout
        setContentView(R.layout.activity_login);
        
        sharedPreferences = getSharedPreferences("TimberTradePrefs", MODE_PRIVATE);
        
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(LoginActivity.this, RealDashboardActivity.class));
            finish();
            return;
        }

        // Bind views
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginBtn);
        registerLink = findViewById(R.id.registerLink);
        
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }
    
    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password required (min 6 chars)");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Authenticating...");
        
        // Demo authentication delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btnLogin.setEnabled(true);
            btnLogin.setText("Authenticate");
            
            // Allow any email/password combo for demo
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userEmail", email);
            editor.apply();
            
            Toast.makeText(LoginActivity.this, "Welcome to TimberTrade!", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(LoginActivity.this, RealDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            
        }, 1500); 
    }
}
