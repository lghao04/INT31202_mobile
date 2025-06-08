package com.example.project1.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.activites.OpenHoursActivity;
import com.example.project1.activites.loginActivity;
import com.example.project1.adapter.SuggestionsAdapter;
import com.example.project1.database.Categories;
import com.example.project1.database.Order;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.*;
import android.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class AccountFragment extends Fragment {

    private EditText edtName, edtPhone, edtAddress;
    private TextView txtEmail, txtWallet, txtRating;
    private ImageView imageRestaurant;
    private MaterialButton btnSave;

    private FirebaseAuth mAuth;
    private DatabaseReference restaurantRef;
    private StorageReference storageRef;
    private Uri selectedImageUri;


    private RecyclerView rvSuggestions;

    private List<String> suggestionList = new ArrayList<>();
    private SuggestionsAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private boolean isUserTyping = true;
    ImageButton btnMenu;
    Spinner spinnerCategory ;
    ImageButton btnEditImage;
    private boolean hasImageChanged = false;
    private static final int REQUEST_CODE_PICK_IMAGE = 1;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

       btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            showPopupMenu(v);
        });

        initViews(view);
        initFirebase();
        loadRestaurantInfo();
        String restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadAndDisplayWallet(restaurantId);

        return view;
    }


    private void showPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        Menu menu = popup.getMenu();

        // Thêm các item menu động
        menu.add(Menu.NONE, 1, Menu.NONE, "Chỉnh sửa hồ sơ");
        menu.add(Menu.NONE, 2, Menu.NONE, "Cập nhật giờ mở/đóng");
        menu.add(Menu.NONE, 3, Menu.NONE, "Đăng xuất");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    Toast.makeText(getContext(), "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
                    edtName.setEnabled(true);
                    edtPhone.setEnabled(true);
                    edtAddress.setEnabled(true);
                    btnEditImage.setVisibility(View.VISIBLE);
                    spinnerCategory.setEnabled(true);
                    spinnerCategory.setVisibility(View.VISIBLE);
                    setupListeners();
                    loadCategoriesForEditing();
                    btnSave.setVisibility(View.VISIBLE);


                    return true;
                case 2:
                    restaurantRef.child("openingHours").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) return;

                            HashMap<String, String[]> openingHoursMap = new HashMap<>();

                            for (DataSnapshot daySnap : snapshot.getChildren()) {
                                String day = daySnap.getKey();
                                String open = daySnap.child("open").getValue(String.class);
                                String close = daySnap.child("close").getValue(String.class);

                                if (open != null && close != null) {
                                    openingHoursMap.put(day, new String[]{open, close});
                                }
                            }

                            // Tạo Intent để mở Activity mới
                            Intent intent = new Intent(getActivity(), OpenHoursActivity.class);

                            // Truyền dữ liệu openingHoursMap qua Bundle
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("opening_hours", openingHoursMap);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Không thể tải giờ mở cửa", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;


                case 3:
                    Toast.makeText(getContext(), "Đăng xuất", Toast.LENGTH_SHORT).show();
                    logout();
                    return true;


                default:
                    return false;
            }
        });

        popup.show();
    }


    private void initViews(View view) {
        edtName = view.findViewById(R.id.edtRestaurantName);
        txtEmail = view.findViewById(R.id.txtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtAddress = view.findViewById(R.id.edtAddress);
        txtWallet = view.findViewById(R.id.txtWallet);
        txtRating = view.findViewById(R.id.txtRating);
        imageRestaurant = view.findViewById(R.id.imageRestaurant);
        btnSave = view.findViewById(R.id.btnSave);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
         btnEditImage = view.findViewById(R.id.btnEditImage);

        // Disable input fields initially
        edtName.setEnabled(false);
        edtPhone.setEnabled(false);
        edtAddress.setEnabled(false);
        // Ẩn nút lưu
        btnSave.setVisibility(View.GONE);
        rvSuggestions = view.findViewById(R.id.recyclerSuggestions);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SuggestionsAdapter(suggestionList, address -> {
            isUserTyping = false;           // Tạm dừng trigger search
            edtAddress.setText(address);
            rvSuggestions.setVisibility(View.GONE);
            isUserTyping = true;            // Bật lại trigger search
        });
        rvSuggestions.setAdapter(adapter);
    }
    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants").child(uid);
        storageRef = FirebaseStorage.getInstance().getReference("restaurant_images");
    }
    private void loadRestaurantInfo() {
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                Double rating = snapshot.child("rating").getValue(Double.class);
                String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                String category = snapshot.child("category").getValue(String.class);

                edtName.setText(name);
                txtEmail.setText("Email: " + email);
                edtPhone.setText( phone);
                edtAddress.setText(address);
                if (rating != null) txtRating.setText("Đánh giá: " + rating + " ⭐");

                // Load ảnh từ Storage
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.img_default)
                            .into(imageRestaurant);
                }


                if (category != null && !category.isEmpty()) {
                    List<String> singleCategoryList = Collections.singletonList(category);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            singleCategoryList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                    spinnerCategory.setEnabled(false); // khóa không cho chọn
                    spinnerCategory.setVisibility(View.VISIBLE); // hiện spinner
                } else {
                    spinnerCategory.setVisibility(View.GONE); // ẩn nếu không có category
                }

        }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnSave.setVisibility(View.VISIBLE);
        edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isUserTyping) return;
                if (s.length() == 0) {
                    rvSuggestions.setVisibility(View.GONE);
                    suggestionList.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    if (s.length() > 2) searchAddress(s.toString());
                };
                handler.postDelayed(searchRunnable, 500);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnEditImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            // Chỉ Android 8.0 trở lên mới hỗ trợ setInitialUri
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Uri initialUri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Pictures/Screenshots");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri);
            }
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

        btnSave.setOnClickListener(v -> {
            if (hasImageChanged && selectedImageUri != null) {
                uploadImageThenSaveInfo();
            } else {
                saveRestaurantInfo();
            }
            edtName.setEnabled(false);
            edtPhone.setEnabled(false);
            edtAddress.setEnabled(false);
            btnEditImage.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
        });

    }
    private void loadCategoriesForEditing() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        List<String> categoryNameList = new ArrayList<>();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categoryNameList
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryNameList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String catName = data.child("name").getValue(String.class);
                    if (catName != null && !catName.isEmpty()) {
                        categoryNameList.add(catName);
                    }
                }
                categoryAdapter.notifyDataSetChanged();

                // Chọn đúng category hiện tại nếu có
                restaurantRef.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String currentCategory = snapshot.getValue(String.class);
                        if (currentCategory != null) {
                            int index = categoryNameList.indexOf(currentCategory);
                            if (index >= 0) {
                                spinnerCategory.setSelection(index);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Không làm gì
                    }
                });

                spinnerCategory.setEnabled(true); // Cho phép chỉnh sửa
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageRestaurant.setImageURI(selectedImageUri);
                hasImageChanged = true;
            }
        }
    }


    private void uploadImageThenSaveInfo() {
        if (selectedImageUri == null) {
            saveRestaurantInfo();
            return;
        }

        String fileName = "restaurant_" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Lưu URL ảnh mới vào Firebase Realtime Database
                    restaurantRef.child("imageUrl").setValue(uri.toString());
                    saveRestaurantInfo(); // Lưu các trường khác
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    hasImageChanged = false;
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }






    private void searchAddress(String keyword) {
        String apiKey = "mELQnnfN0HwjBnfnh8oRvNm90BEfCNazPAKxujMv";
        String urlStr = "https://rsapi.goong.io/Place/AutoComplete?input=" + Uri.encode(keyword) + "&api_key=" + apiKey;

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject json = new JSONObject(result.toString());
                JSONArray predictions = json.getJSONArray("predictions");

                List<String> newSuggestions = new ArrayList<>();
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject obj = predictions.getJSONObject(i);
                    String description = obj.getString("description");
                    newSuggestions.add(description);
                }

                requireActivity().runOnUiThread(() -> {
                    suggestionList.clear();
                    suggestionList.addAll(newSuggestions);
                    adapter.notifyDataSetChanged();
                    rvSuggestions.setVisibility(View.VISIBLE);
                });

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // Xử lý lỗi, ví dụ clear list, ẩn RecyclerView
                    requireActivity().runOnUiThread(() -> {
                        suggestionList.clear();
                        adapter.notifyDataSetChanged();
                        rvSuggestions.setVisibility(View.GONE);
                    });
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void saveRestaurantInfo() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);
        updates.put("address", address);  // Lưu trực tiếp chuỗi địa chỉ

        if (spinnerCategory.isEnabled() && spinnerCategory.getSelectedItem() != null) {
            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            updates.put("category", selectedCategory);
        }


        restaurantRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Đã lưu thông tin", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
            }
        });

        // Upload ảnh nếu có ảnh mới
        if (selectedImageUri != null) {
            storageRef.child(mAuth.getCurrentUser().getUid() + ".jpg")
                    .putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Đã cập nhật ảnh", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi tải ảnh", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(getContext(), loginActivity.class));
        requireActivity().finish();
    }



        private void loadAndDisplayWallet(String restaurantId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        ordersRef.orderByChild("restaurantId").equalTo(restaurantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double walletTotal = 0;
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Order order = orderSnapshot.getValue(Order.class);
                            if (order != null && "completed".equals(order.getStatus())) {
                                walletTotal += order.getTotal();
                            }
                        }

                        // Hiển thị vào TextView
                        txtWallet.setText("Ví: "+String.format("%,.0fđ", walletTotal));

                        // Cập nhật Firebase nếu muốn
                        FirebaseDatabase.getInstance()
                                .getReference("restaurants")
                                .child(restaurantId)
                                .child("wallet")
                                .setValue(walletTotal);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Không thể tải ví!", Toast.LENGTH_SHORT).show();
                    }});
            }


}
