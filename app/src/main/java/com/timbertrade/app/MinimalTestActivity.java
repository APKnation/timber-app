package com.timbertrade.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MinimalTestActivity extends Activity {
    
    private static final String TAG = "MinimalTestActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting MinimalTestActivity");
        
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "super.onCreate completed");
            
            TextView textView = new TextView(this);
            textView.setText("TIMBERTRADE APP\n\n✅ MINIMAL TEST\n\nIf you see this, the app framework works!\n\nNext step: Test LoginActivity");
            textView.setTextSize(20);
            textView.setPadding(50, 50, 50, 50);
            setContentView(textView);
            
            Log.d(TAG, "MinimalTestActivity setup complete");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in MinimalTestActivity: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
