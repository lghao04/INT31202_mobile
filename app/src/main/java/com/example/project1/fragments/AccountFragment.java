package com.example.project1.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project1.R;
import com.example.project1.activites.loginActivity;
import com.example.project1.database.Restaurants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountFragment extends Fragment {
    private EditText edtName, edtEmail, edtPhone, edtAddress;
    private TextView txtWallet, txtRating;
    private ImageView imageRestaurant;
    private Button btnSave, btnLogout, btnChangeImage;

    private TextView txtUpdateOpeningHours;

    private DatabaseReference restaurantRef;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private Uri selectedImageUri;

    private RecyclerView recyclerSuggestions;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> suggestionList = new ArrayList<>();

    private HashMap<String, String[]> savedOpeningHours;
    private boolean isProgrammaticAddressChange = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Khởi tạo các view
        edtName = view.findViewById(R.id.edtRestaurantName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtAddress = view.findViewById(R.id.edtAddress);
        txtWallet = view.findViewById(R.id.txtWallet);
        txtRating = view.findViewById(R.id.txtRating);
        imageRestaurant = view.findViewById(R.id.imageRestaurant);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangeImage = view.findViewById(R.id.btnChangeImage);
        recyclerSuggestions = view.findViewById(R.id.recyclerSuggestions);
        txtUpdateOpeningHours = view.findViewById(R.id.txtUpdateOpeningHours);

        loadRestaurantInfo();


        //address
        setupRecyclerView();
        edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isProgrammaticAddressChange) {
                    return; // Bỏ qua nếu đang gán địa chỉ từ Firebase
                }
                if (s.length() >= 3) {
                    fetchAddressSuggestions(s.toString());
                }
                else {
                    recyclerSuggestions.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isProgrammaticAddressChange = false;
            }
        });


        //sđt
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String phone = edtPhone.getText().toString().trim();
                if (phone.length() > 0) {
                    if (!phone.startsWith("0")) {
                        edtPhone.setError("Số điện thoại phải bắt đầu bằng số 0");
                    } else if (phone.length() > 10) {
                        edtPhone.setError("Số điện thoại không được quá 10 số");
                    } else if (phone.length() == 10) {
                        edtPhone.setError(null);  // Xóa lỗi khi đủ 10 số và bắt đầu bằng 0
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants").child(uid);
        storageRef = FirebaseStorage.getInstance().getReference("restaurant_images");



        btnChangeImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1001);
        });

// xử lí địa chỉ



        suggestionAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, suggestionList);

        recyclerSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSuggestions.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
                return new RecyclerView.ViewHolder(textView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView textView = (TextView) holder.itemView;
                textView.setText(suggestionList.get(position));
                textView.setOnClickListener(v -> {
                    edtAddress.setText(suggestionList.get(position));
                    isAddressSelected = true;
                    recyclerSuggestions.setVisibility(View.GONE);
                });
            }

            @Override
            public int getItemCount() {
                return suggestionList.size();
            }
        });



        // xử lí thời gian đóng/ mở cửa

        getParentFragmentManager().setFragmentResultListener("opening_hours_result", getViewLifecycleOwner(), (requestKey, result) -> {
            savedOpeningHours = (HashMap<String, String[]>) result.getSerializable("opening_hours");
            if (savedOpeningHours != null) {
                txtUpdateOpeningHours.setText("Đã cập nhật thời gian mở/đóng cửa");
            }
        });

        txtUpdateOpeningHours.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new OpeningHoursFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });



// lưu thông tin
        btnSave.setOnClickListener(v -> saveRestaurantInfo());

        // đăng xuất
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageRestaurant.setImageURI(selectedImageUri);
        }
    }


    private void loadRestaurantInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance()
                .getReference("restaurants")
                .child(user.getUid());

        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tự tạo model từ dữ liệu snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    Double wallet = snapshot.child("wallet").getValue(Double.class);
                    Double rating = snapshot.child("rating").getValue(Double.class);

                    edtName.setText(name);
                    edtEmail.setText(email); // readonly
                    if (phone != null) edtPhone.setText(phone);
                    if (address != null)
                    {
                        isProgrammaticAddressChange = true;
                        edtAddress.setText(address);
                    }
                    if (wallet != null) txtWallet.setText("Số dư ví: " + wallet + " đ");
                    if (rating != null) txtRating.setText("Đánh giá: " + rating + " ⭐");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // gọi api address
    private boolean isAddressSelected = false;
    private void fetchAddressSuggestions(String query) {

        if (isAddressSelected) {
            return; // Nếu đã chọn địa chỉ, không gọi API nữa
        }
        OkHttpClient client = new OkHttpClient();

        String url = "https://photon.komoot.io/api/?q=" + Uri.encode(query) + "&limit=5";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    parseAddressSuggestions(responseBody);
                }

            }

        });
    }

    private void parseAddressSuggestions(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray features = obj.getJSONArray("features");

            List<String> results = new ArrayList<>();
            for (int i = 0; i < features.length(); i++) {
                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                String name = properties.optString("name", "");
                String city = properties.optString("city", "");
                String country = properties.optString("country", "");
                results.add(name + ", " + city + ", " + country);
            }

            requireActivity().runOnUiThread(() -> {
                suggestionList.clear();
                suggestionList.addAll(results);
                recyclerSuggestions.setVisibility(suggestionList.isEmpty() ? View.GONE : View.VISIBLE);
                recyclerSuggestions.getAdapter().notifyDataSetChanged();
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        suggestionAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, suggestionList);

        recyclerSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSuggestions.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_1, parent, false);
                return new RecyclerView.ViewHolder(textView) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView textView = (TextView) holder.itemView;
                textView.setText(suggestionList.get(position));
                textView.setOnClickListener(v -> {
                    // Khi người dùng chọn một địa chỉ
                    edtAddress.setText(suggestionList.get(position)); // Điền vào ô địa chỉ
                    isAddressSelected = true; // Đặt flag là đã chọn địa chỉ
                    recyclerSuggestions.setVisibility(View.GONE); // Ẩn danh sách gợi ý
                });
            }

            @Override
            public int getItemCount() {
                return suggestionList.size();
            }
        });
    }






    private void saveRestaurantInfo() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() != 10 || !phone.startsWith("0")) {
            Toast.makeText(getContext(), "Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", name);
        updateData.put("phone", phone);
        updateData.put("address", address);

        // Lưu thời gian mở/đóng cửa nếu đã chọn
        if (savedOpeningHours != null) {
            Map<String, Object> openingHoursMap = new HashMap<>();
            for (Map.Entry<String, String[]> entry : savedOpeningHours.entrySet()) {
                Map<String, String> hours = new HashMap<>();
                hours.put("open", entry.getValue()[0]);
                hours.put("close", entry.getValue()[1]);
                openingHoursMap.put(entry.getKey(), hours);
            }
            updateData.put("openingHours", openingHoursMap);
        }

        if (selectedImageUri != null) {
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateData.put("imageUrl", uri.toString());
                        restaurantRef.updateChildren(updateData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                    });
        } else {
            restaurantRef.updateChildren(updateData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), loginActivity.class));
        getActivity().finish();
    }
}
