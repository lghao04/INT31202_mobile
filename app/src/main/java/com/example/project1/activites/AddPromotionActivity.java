package com.example.project1.activites;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.InputType;
import java.util.Calendar;

public class AddPromotionActivity extends AppCompatActivity {

    private RadioGroup rgDiscountType;
    private RadioButton rbFixedAmount, rbPercentage;
    private EditText etDiscountAmount, etMinimumOrder, etTotalUsage, etUsagePerUser, etStartDate, etEndDate, etPromoCode,etMaxDiscountAmount;
    ;
    private Button btnMinusTotalUsage, btnPlusTotalUsage, btnMinusUsagePerUser, btnPlusUsagePerUser, btnConfirm, btnPreview;

    private ProgressDialog progressDialog;

    private String restaurantId;
    private LinearLayout layoutMaxDiscount;
    private  TextView tvDiscountUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        // Lấy restaurantId từ FirebaseAuth
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();

        // Mặc định chọn "Mức giảm" (tính theo tiền)
        rbFixedAmount.setChecked(true);

        etStartDate.setInputType(InputType.TYPE_NULL);  // tắt gõ tay
        etEndDate.setInputType(InputType.TYPE_NULL);

        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate));


    }

    private void initViews() {
        rgDiscountType = findViewById(R.id.rgDiscountType);
        rbFixedAmount = findViewById(R.id.rbFixedAmount);
        rbPercentage = findViewById(R.id.rbPercentage);

        etDiscountAmount = findViewById(R.id.etDiscountAmountValue);
        etMinimumOrder = findViewById(R.id.etMinimumOrderValue);
         tvDiscountUnit = findViewById(R.id.tvDiscountUnit);
        etMaxDiscountAmount = findViewById(R.id.etMaxDiscountAmount);

        etTotalUsage = findViewById(R.id.etTotalUsage);
        etUsagePerUser = findViewById(R.id.etUsagePerUser);

        btnMinusTotalUsage = findViewById(R.id.btnMinusTotalUsage);
        btnPlusTotalUsage = findViewById(R.id.btnPlusTotalUsage);

        btnMinusUsagePerUser = findViewById(R.id.btnMinusUsagePerUser);
        btnPlusUsagePerUser = findViewById(R.id.btnPlusUsagePerUser);

        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);

        etPromoCode = findViewById(R.id.etPromoCode);

        btnConfirm = findViewById(R.id.btnConfirm);
        btnPreview = findViewById(R.id.btnPreview);

        layoutMaxDiscount = findViewById(R.id.layoutMaxDiscount);



        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang lưu...");
        progressDialog.setCancelable(false);
    }


    private void showDateTimePicker(EditText targetEditText) {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    date.set(Calendar.YEAR, year);
                    date.set(Calendar.MONTH, month);
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                date.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                targetEditText.setText(sdf.format(date.getTime()));

                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);

                    timePickerDialog.show();
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void setupListeners() {
        rgDiscountType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFixedAmount) {
                tvDiscountUnit.setText("đ");
                layoutMaxDiscount.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbPercentage) {
                tvDiscountUnit.setText("%");
                layoutMaxDiscount.setVisibility(View.VISIBLE);
            }
        });




        btnMinusTotalUsage.setOnClickListener(v -> decrementEditTextValue(etTotalUsage));
        btnPlusTotalUsage.setOnClickListener(v -> incrementEditTextValue(etTotalUsage));

        btnMinusUsagePerUser.setOnClickListener(v -> decrementEditTextValue(etUsagePerUser));
        btnPlusUsagePerUser.setOnClickListener(v -> incrementEditTextValue(etUsagePerUser));

        btnConfirm.setOnClickListener(v -> savePromotion());

    }

    private void decrementEditTextValue(EditText editText) {
        int current = 0;
        try {
            String text = editText.getText().toString().trim();
            current = text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (NumberFormatException ignored) {}
        if (current > 0) current--;
        editText.setText(String.valueOf(current));
    }

    private void incrementEditTextValue(EditText editText) {
        int current = 0;
        try {
            String text = editText.getText().toString().trim();
            current = text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (NumberFormatException ignored) {}
        current++;
        editText.setText(String.valueOf(current));
    }

    private void savePromotion() {
        String discountType = rbFixedAmount.isChecked() ? "fixed_amount" : "percentage";
        String discountAmountStr = etDiscountAmount.getText().toString().trim();
        String minimumOrderStr = etMinimumOrder.getText().toString().trim();
        String totalUsageStr = etTotalUsage.getText().toString().trim();
        String usagePerUserStr = etUsagePerUser.getText().toString().trim();
        String startDateStr = etStartDate.getText().toString().trim();
        String endDateStr = etEndDate.getText().toString().trim();
        String promoCode = etPromoCode.getText().toString().trim();

        if (TextUtils.isEmpty(discountAmountStr) || TextUtils.isEmpty(minimumOrderStr)
                || TextUtils.isEmpty(totalUsageStr) || TextUtils.isEmpty(usagePerUserStr)
                || TextUtils.isEmpty(startDateStr) || TextUtils.isEmpty(endDateStr)
                || TextUtils.isEmpty(promoCode)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int discountAmount;
        int totalUsage;
        int usagePerUser;
        double maxDiscountAmount = 0;

        try {
            discountAmount = Integer.parseInt(discountAmountStr);
            totalUsage = Integer.parseInt(totalUsageStr);
            usagePerUser = Integer.parseInt(usagePerUserStr);

            if (discountType.equals("percentage")) {
                if (discountAmount < 0 || discountAmount > 100) {
                    Toast.makeText(this, "Mức giảm phần trăm phải từ 0 đến 100", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            String maxDiscountAmountStr = etMaxDiscountAmount.getText().toString().trim();
            if (TextUtils.isEmpty(maxDiscountAmountStr)) {
                Toast.makeText(this, "Vui lòng nhập giảm tối đa", Toast.LENGTH_SHORT).show();
                return;
            }
            maxDiscountAmount = Double.parseDouble(maxDiscountAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.show();

        // Tạo đối tượng promotion
        HashMap<String, Object> promotionMap = new HashMap<>();
        promotionMap.put("restaurantId", restaurantId);
        promotionMap.put("discountType", discountType);
        promotionMap.put("discountAmount", discountAmount);
        promotionMap.put("minimumOrder", minimumOrderStr);
        promotionMap.put("totalUsage", totalUsage);
        promotionMap.put("usagePerUser", usagePerUser);
        promotionMap.put("startDate", startDateStr);
        promotionMap.put("endDate", endDateStr);
        promotionMap.put("promoCode", promoCode);

        if (discountType.equals("percentage")) {
            promotionMap.put("maxDiscountAmount", maxDiscountAmount); // ✅ chỉ khi là phần trăm
        }

        // Lấy reference database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("promotions");

        // Tạo key mới cho promotion
        String promotionId = ref.push().getKey();

        if (promotionId == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi hệ thống, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            return;
        }

        ref.child(promotionId)
                .setValue(promotionMap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(AddPromotionActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Quay về Fragment quản lý khuyến mãi
                    } else {
                        Toast.makeText(AddPromotionActivity.this, "Lưu thất bại: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
