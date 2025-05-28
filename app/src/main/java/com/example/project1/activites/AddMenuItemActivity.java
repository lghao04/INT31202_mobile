package com.example.project1.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddMenuItemActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView imgDishPreview;
    private EditText etName, etDescription, etCategory, etPrice, etPrepTime;
    private Button btnChooseImage, btnAdd;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private String restaurantId;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        imgDishPreview = findViewById(R.id.imgDishPreview);
        etName = findViewById(R.id.etDishName);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etPrepTime = findViewById(R.id.etPrepTime);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnAdd = findViewById(R.id.btnAddMenuItem);

        // Firebase
        storageRef = FirebaseStorage.getInstance().getReference("imgItems");
        databaseRef = FirebaseDatabase.getInstance().getReference("menuItems");
        restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imgDishPreview.setImageURI(imageUri);
                        getContentResolver().takePersistableUriPermission(imageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                });

        btnChooseImage.setOnClickListener(v -> openFileChooser());
        btnAdd.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageAndSaveMenu();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse("content://com.android.externalstorage.documents/document/primary:Pictures/Screenshots"));
        imagePickerLauncher.launch(intent);

    }

    private void uploadImageAndSaveMenu() {
        String dishId = databaseRef.push().getKey();
        StorageReference fileRef = storageRef.child(dishId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    Map<String, Object> menuItem = new HashMap<>();
                    menuItem.put("name", etName.getText().toString());
                    menuItem.put("description", etDescription.getText().toString());
                    menuItem.put("category", etCategory.getText().toString());
                    menuItem.put("imageUrl", imageUrl);
                    menuItem.put("price", Double.parseDouble(etPrice.getText().toString()));
                    menuItem.put("preparationTime", Integer.parseInt(etPrepTime.getText().toString()));
                    menuItem.put("rating", 0);
                    menuItem.put("sales", 0);
                    menuItem.put("favorites", 0);
                    menuItem.put("restaurantId", restaurantId);
                    menuItem.put("isAvailable", true);

                    databaseRef.child(dishId).setValue(menuItem)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Thêm món thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            })

                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    Log.d("DEBUG", "Tên món: " + etName.getText().toString());

                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Upload ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
