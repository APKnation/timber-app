package com.timbertrade.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImagePicker {
    private static final String TAG = "ImagePicker";
    private static final int REQUEST_CODE_CAMERA = 1001;
    private static final int REQUEST_CODE_GALLERY = 1002;
    
    public interface ImagePickerCallback {
        void onImageSelected(Uri imageUri);
        void onError(String error);
    }
    
    private final Context context;
    private final ImagePickerCallback callback;
    private Uri currentImageUri;
    
    public ImagePicker(Context context, ImagePickerCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    public void checkPermissionsAndShowOptions() {
        if (hasCameraPermission() && hasStoragePermission()) {
            showImageSourceDialog();
        } else {
            requestPermissions();
        }
    }
    
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) 
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    private void requestPermissions() {
        if (context instanceof Activity) {
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                };
            } else {
                permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
            }
            ActivityCompat.requestPermissions((Activity) context, permissions, 1000);
        }
    }
    
    private void showImageSourceDialog() {
        if (context instanceof Activity) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setTitle("Select Image Source")
                    .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                openCamera();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    })
                    .show();
        }
    }
    
    private void openCamera() {
        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
                currentImageUri = createImageUri();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                } else if (context instanceof Fragment) {
                    ((Fragment) context).startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
                }
            } else {
                callback.onError("Camera not available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening camera", e);
            callback.onError("Failed to open camera: " + e.getMessage());
        }
    }
    
    private void openGallery() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            } else if (context instanceof Fragment) {
                ((Fragment) context).startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening gallery", e);
            callback.onError("Failed to open gallery: " + e.getMessage());
        }
    }
    
    private Uri createImageUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "timber_" + timeStamp + ".jpg";
        
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TimberTrade");
        
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CAMERA:
                    if (currentImageUri != null) {
                        callback.onImageSelected(currentImageUri);
                    } else {
                        callback.onError("Failed to capture image");
                    }
                    break;
                    
                case REQUEST_CODE_GALLERY:
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        callback.onImageSelected(selectedImageUri);
                    } else {
                        callback.onError("Failed to select image");
                    }
                    break;
            }
        } else {
            callback.onError("Image selection cancelled");
        }
    }
    
    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSourceDialog();
            } else {
                callback.onError("Camera and storage permissions are required to upload timber images");
            }
        }
    }
}
