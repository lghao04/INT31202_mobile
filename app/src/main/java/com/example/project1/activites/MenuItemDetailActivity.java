package com.example.project1.activites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.database.MenuItems;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MenuItemDetailActivity extends AppCompatActivity {

    private ImageView imgDish;
    private EditText etName, etDescription, etCategory, etPrice, etPrepTime;
    private Button btnEditSave;

    private String menuItemId;
    private boolean isEditing = false;
    private MenuItems currentItem;
    private Uri newImageUri = null;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("imgItems");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_detail);

        imgDish = findViewById(R.id.imgDishDetail);
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etPrepTime = findViewById(R.id.etPrepTime);
        btnEditSave = findViewById(R.id.btnEditSave);

        menuItemId = getIntent().getStringExtra("menuItemId");

        if (menuItemId == null) {
            Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        newImageUri = result.getData().getData();
                        imgDish.setImageURI(newImageUri);
                    }
                });

        imgDish.setOnClickListener(v -> {
            if (isEditing) {
                Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
                pick.setType("image/*");
                imagePickerLauncher.launch(Intent.createChooser(pick, "Chọn ảnh món mới"));
            }
        });

        loadMenuItem();

        btnEditSave.setOnClickListener(v -> {
            if (isEditing) {
                saveUpdatedMenuItem();
            } else {
                enableEditing(true);
                btnEditSave.setText("Lưu");
                isEditing = true;
            }
        });
    }

    private void loadMenuItem() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menuItems").child(menuItemId);
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                currentItem = snapshot.getValue(MenuItems.class);
                if (currentItem != null) {
                    etName.setText(currentItem.getName());
                    etDescription.setText(currentItem.getDescription());
                    etCategory.setText(currentItem.getCategory());
                    etPrice.setText(String.valueOf(currentItem.getPrice()));
                    etPrepTime.setText(String.valueOf(currentItem.getPreparationTime()));
                    Glide.with(this).load(currentItem.getImageUrl()).into(imgDish);
                }
            } else {
                Toast.makeText(this, "Không tìm thấy dữ liệu", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void enableEditing(boolean enable) {
        etName.setEnabled(enable);
        etDescription.setEnabled(enable);
        etCategory.setEnabled(enable);
        etPrice.setEnabled(enable);
        etPrepTime.setEnabled(enable);
    }

    private void saveUpdatedMenuItem() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String prepTimeStr = etPrepTime.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Tên và giá không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int prepTime = Integer.parseInt(prepTimeStr);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("menuItems").child(menuItemId);

        ref.child("name").setValue(name);
        ref.child("description").setValue(desc);
        ref.child("category").setValue(category);
        ref.child("price").setValue(price);
        ref.child("preparationTime").setValue(prepTime);

        if (newImageUri != null) {
            // 👉 Nếu có ảnh mới → upload lên Storage
            StorageReference fileRef = storageRef.child(menuItemId + ".jpg");
            fileRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        ref.child("imageUrl").setValue(uri.toString());
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        afterSave();
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(this, "Upload ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            afterSave();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

    private void afterSave() {
        enableEditing(false);
        btnEditSave.setText("Chỉnh sửa");
        isEditing = false;
        newImageUri = null;
    }
}
