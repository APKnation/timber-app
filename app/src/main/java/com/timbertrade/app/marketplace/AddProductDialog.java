package com.timbertrade.app.marketplace;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.models.Product;
import com.timbertrade.app.services.FirebaseStorageService;
import com.timbertrade.app.utils.ImagePicker;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddProductDialog extends DialogFragment {
    
    private static final String TAG = "AddProductDialog";
    private static final String ARG_PRODUCT = "product";
    
    private Product editingProduct;
    private List<Uri> selectedImages = new ArrayList<>();
    private List<String> uploadedImageUrls = new ArrayList<>();
    
    // UI Components
    private ImageButton btnAddImage, btnRemoveImage;
    private ImageView ivProductImage;
    private TextView tvImageCount;
    private ProgressBar progressBar;
    private TextInputLayout tilTitle, tilDescription, tilPrice, tilQuantity, tilLocation;
    private EditText etTitle, etDescription, etPrice, etQuantity, etLocation;
    private Spinner spCategory, spQuantityUnit;
    private Button btnCancel, btnSave;
    
    private ImagePicker imagePicker;
    private FirebaseStorageService storageService;
    
    public interface OnProductSavedListener {
        void onProductSaved(Product product);
    }
    
    private OnProductSavedListener listener;
    
    public static AddProductDialog newInstance(Product product) {
        AddProductDialog dialog = new AddProductDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT, product);
        dialog.setArguments(args);
        return dialog;
    }
    
    public void setOnProductSavedListener(OnProductSavedListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            editingProduct = (Product) getArguments().getSerializable(ARG_PRODUCT);
        }
        
        imagePicker = new ImagePicker(requireContext(), new ImagePicker.ImagePickerCallback() {
            @Override
            public void onImageSelected(Uri imageUri) {
                addImageToList(imageUri);
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
        
        storageService = FirebaseStorageService.getInstance();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_product, container, false);
        
        initViews(view);
        setupClickListeners();
        
        if (editingProduct != null) {
            populateFields();
        }
        
        return view;
    }
    
    private void initViews(View view) {
        btnAddImage = view.findViewById(R.id.btnAddImage);
        btnRemoveImage = view.findViewById(R.id.btnRemoveImage);
        ivProductImage = view.findViewById(R.id.ivProductImage);
        tvImageCount = view.findViewById(R.id.tvImageCount);
        progressBar = view.findViewById(R.id.progressBar);
        
        tilTitle = view.findViewById(R.id.tilTitle);
        tilDescription = view.findViewById(R.id.tilDescription);
        tilPrice = view.findViewById(R.id.tilPrice);
        tilQuantity = view.findViewById(R.id.tilQuantity);
        tilLocation = view.findViewById(R.id.tilLocation);
        
        etTitle = tilTitle.getEditText();
        etDescription = tilDescription.getEditText();
        etPrice = tilPrice.getEditText();
        etQuantity = tilQuantity.getEditText();
        etLocation = tilLocation.getEditText();
        
        spCategory = view.findViewById(R.id.spCategory);
        spQuantityUnit = view.findViewById(R.id.spQuantityUnit);
        
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSave = view.findViewById(R.id.btnSave);
        
        updateImageDisplay();
    }
    
    private void setupClickListeners() {
        btnAddImage.setOnClickListener(v -> {
            if (selectedImages.size() < 5) {
                imagePicker.checkPermissionsAndShowOptions();
            } else {
                Toast.makeText(getContext(), "Maximum 5 images allowed", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnRemoveImage.setOnClickListener(v -> {
            if (!selectedImages.isEmpty()) {
                selectedImages.remove(selectedImages.size() - 1);
                updateImageDisplay();
            }
        });
        
        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> dismiss());
    }
    
    private void addImageToList(Uri imageUri) {
        selectedImages.add(imageUri);
        updateImageDisplay();
    }
    
    private void updateImageDisplay() {
        if (selectedImages.isEmpty()) {
            ivProductImage.setImageResource(R.drawable.ic_add_photo);
            btnRemoveImage.setVisibility(View.GONE);
            tvImageCount.setText("No images selected");
        } else {
            // Display the last selected image
            Uri lastImage = selectedImages.get(selectedImages.size() - 1);
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(lastImage));
                ivProductImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image", e);
                ivProductImage.setImageResource(R.drawable.ic_error);
            }
            
            btnRemoveImage.setVisibility(View.VISIBLE);
            tvImageCount.setText(selectedImages.size() + " image(s) selected");
        }
    }
    
    private void populateFields() {
        if (editingProduct != null) {
            etTitle.setText(editingProduct.getTitle());
            etDescription.setText(editingProduct.getDescription());
            etPrice.setText(String.valueOf(editingProduct.getPrice()));
            etQuantity.setText(String.valueOf(editingProduct.getQuantity()));
            etLocation.setText(editingProduct.getLocation());
            
            // Set category spinner
            if (editingProduct.getCategory() != null) {
                String[] categories = getResources().getStringArray(R.array.timber_categories);
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equals(editingProduct.getCategory().toString())) {
                        spCategory.setSelection(i);
                        break;
                    }
                }
            }
            
            // Load existing images
            if (editingProduct.getImageUrls() != null) {
                uploadedImageUrls = new ArrayList<>(editingProduct.getImageUrls());
                tvImageCount.setText(uploadedImageUrls.size() + " existing image(s)");
            }
        }
    }
    
    private void saveProduct() {
        if (!validateFields()) {
            return;
        }
        
        String productId = editingProduct != null ? editingProduct.getProductId() : UUID.randomUUID().toString();
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        
        if (selectedImages.isEmpty()) {
            // No new images to upload, save directly
            saveProductWithImages(productId, uploadedImageUrls);
        } else {
            // Upload new images first
            uploadImages(productId);
        }
    }
    
    private void uploadImages(String productId) {
        uploadedImageUrls.clear();
        
        FirebaseStorageService.ImageUploadCallback callback = new FirebaseStorageService.ImageUploadCallback() {
            private int uploadedCount = 0;
            private int totalCount = selectedImages.size();
            
            @Override
            public void onSuccess(String imageUrl) {
                uploadedImageUrls.add(imageUrl);
                uploadedCount++;
                
                if (uploadedCount == totalCount) {
                    // All images uploaded, save product
                    saveProductWithImages(productId, uploadedImageUrls);
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Image upload failed", new Exception(error));
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(getContext(), "Failed to upload images: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onProgress(int progress) {
                // Update progress if needed
            }
        };
        
        // Upload each image
        for (Uri imageUri : selectedImages) {
            storageService.uploadTimberImage(requireContext(), imageUri, productId, callback);
        }
    }
    
    private void saveProductWithImages(String productId, List<String> imageUrls) {
        try {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            double price = Double.parseDouble(etPrice.getText().toString().trim());
            int quantity = Integer.parseInt(etQuantity.getText().toString().trim());
            String location = etLocation.getText().toString().trim();
            
            // Get category
            Product.TimberCategory category = Product.TimberCategory.MAHOGANY; // Default
            String selectedCategory = (String) spCategory.getSelectedItem();
            for (Product.TimberCategory tc : Product.TimberCategory.values()) {
                if (tc.toString().equals(selectedCategory)) {
                    category = tc;
                    break;
                }
            }
            
            Product product;
            if (editingProduct != null) {
                // Update existing product
                editingProduct.setTitle(title);
                editingProduct.setDescription(description);
                editingProduct.setPrice(price);
                editingProduct.setQuantity(quantity);
                editingProduct.setLocation(location);
                editingProduct.setCategory(category);
                editingProduct.setImageUrls(imageUrls);
                product = editingProduct;
            } else {
                // Create new product
                product = new Product(
                    productId,
                    "current_user_id", // TODO: Get from auth
                    "Current User", // TODO: Get from auth
                    title,
                    description,
                    category,
                    price,
                    "per unit", // TODO: Get from spinner
                    quantity,
                    "units", // TODO: Get from spinner
                    location,
                    imageUrls
                );
            }
            
            // Hide progress
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            
            // Notify listener
            if (listener != null) {
                listener.onProductSaved(product);
            }
            
            dismiss();
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving product", e);
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            Toast.makeText(getContext(), "Error saving product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean validateFields() {
        boolean isValid = true;
        
        if (etTitle.getText().toString().trim().isEmpty()) {
            tilTitle.setError("Title is required");
            isValid = false;
        } else {
            tilTitle.setError(null);
        }
        
        if (etDescription.getText().toString().trim().isEmpty()) {
            tilDescription.setError("Description is required");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }
        
        String priceStr = etPrice.getText().toString().trim();
        if (priceStr.isEmpty()) {
            tilPrice.setError("Price is required");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    tilPrice.setError("Price must be greater than 0");
                    isValid = false;
                } else {
                    tilPrice.setError(null);
                }
            } catch (NumberFormatException e) {
                tilPrice.setError("Invalid price format");
                isValid = false;
            }
        }
        
        String quantityStr = etQuantity.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            tilQuantity.setError("Quantity is required");
            isValid = false;
        } else {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    isValid = false;
                } else {
                    tilQuantity.setError(null);
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Invalid quantity format");
                isValid = false;
            }
        }
        
        if (etLocation.getText().toString().trim().isEmpty()) {
            tilLocation.setError("Location is required");
            isValid = false;
        } else {
            tilLocation.setError(null);
        }
        
        return isValid;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermissionResult(requestCode, grantResults);
    }
}
