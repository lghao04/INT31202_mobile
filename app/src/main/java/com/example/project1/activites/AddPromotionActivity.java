package com.example.project1.activites;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.example.project1.database.promotions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.*;

public class AddPromotionActivity extends AppCompatActivity {

    private RadioGroup rgDiscountType;
    private RadioButton rbFixedAmount, rbPercentage;
    private EditText etDiscountAmount, etMinimumOrder, etTotalUsage, etUsagePerUser,
            etStartDate, etEndDate, etPromoCode, etMaxDiscountAmount;
    private Button btnMinusTotalUsage, btnPlusTotalUsage, btnMinusUsagePerUser,
            btnPlusUsagePerUser, btnConfirm;
    private ProgressDialog progressDialog;
    private String restaurantId, promotionId = null;
    private LinearLayout layoutMaxDiscount;
    private TextView tvDiscountUnit;

    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();

        rbFixedAmount.setChecked(true);
        etStartDate.setInputType(InputType.TYPE_NULL);
        etEndDate.setInputType(InputType.TYPE_NULL);
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate));

        // Check edit mode và nhận đối tượng promotions
        if (getIntent() != null && getIntent().hasExtra("promotionObject")) {
            isEditMode = true;

            promotions promotion = (promotions) getIntent().getSerializableExtra("promotionObject");
            if (promotion != null) {
                promotionId = promotion.getId();
                populateFields(promotion);
                btnConfirm.setText("Cập nhật");
            }
        }
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
        layoutMaxDiscount = findViewById(R.id.layoutMaxDiscount);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        rgDiscountType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFixedAmount) {
                tvDiscountUnit.setText("đ");
                layoutMaxDiscount.setVisibility(View.GONE);
            } else {
                tvDiscountUnit.setText("%");
                layoutMaxDiscount.setVisibility(View.VISIBLE);
            }
        });

        btnMinusTotalUsage.setOnClickListener(v -> decrementEditTextValue(etTotalUsage));
        btnPlusTotalUsage.setOnClickListener(v -> incrementEditTextValue(etTotalUsage));
        btnMinusUsagePerUser.setOnClickListener(v -> decrementEditTextValue(etUsagePerUser));
        btnPlusUsagePerUser.setOnClickListener(v -> incrementEditTextValue(etUsagePerUser));

        btnConfirm.setOnClickListener(v -> saveOrUpdatePromotion());
    }

    // Chuyển từ Map sang nhận đối tượng promotions
    private void populateFields(promotions promotion) {
        etPromoCode.setText(promotion.getPromoCode());
        etMinimumOrder.setText(promotion.getMinimumOrder());
        etDiscountAmount.setText(String.valueOf(promotion.getDiscountAmount()));
        etTotalUsage.setText(String.valueOf(promotion.getTotalUsage()));
        etUsagePerUser.setText(String.valueOf(promotion.getUsagePerUser()));
        etStartDate.setText(promotion.getStartDate());
        etEndDate.setText(promotion.getEndDate());

        String type = promotion.getDiscountType();
        if ("percentage".equals(type)) {
            rbPercentage.setChecked(true);
            etMaxDiscountAmount.setText(String.valueOf(promotion.getMaxDiscountAmount()));
        } else {
            rbFixedAmount.setChecked(true);
        }
    }

    private void showDateTimePicker(EditText target) {
        final Calendar current = Calendar.getInstance();
        final Calendar selected = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selected.set(Calendar.YEAR, year);
                    selected.set(Calendar.MONTH, month);
                    selected.set(Calendar.DAY_OF_MONTH, day);

                    TimePickerDialog timePicker = new TimePickerDialog(this,
                            (timeView, hour, minute) -> {
                                selected.set(Calendar.HOUR_OF_DAY, hour);
                                selected.set(Calendar.MINUTE, minute);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                target.setText(sdf.format(selected.getTime()));
                            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), true);

                    timePicker.show();
                },
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH),
                current.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void decrementEditTextValue(EditText et) {
        int current = parseIntSafe(et.getText().toString().trim(), 0);
        if (current > 0) current--;
        et.setText(String.valueOf(current));
    }

    private void incrementEditTextValue(EditText et) {
        int current = parseIntSafe(et.getText().toString().trim(), 0);
        et.setText(String.valueOf(++current));
    }

    private int parseIntSafe(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private void saveOrUpdatePromotion() {
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

        int discountAmount = parseIntSafe(discountAmountStr, -1);
        int totalUsage = parseIntSafe(totalUsageStr, -1);
        int usagePerUser = parseIntSafe(usagePerUserStr, -1);
        double maxDiscountAmount = 0;

        if ("percentage".equals(discountType)) {
            if (discountAmount < 0 || discountAmount > 100) {
                Toast.makeText(this, "Phần trăm giảm không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            String maxDiscountStr = etMaxDiscountAmount.getText().toString().trim();
            if (TextUtils.isEmpty(maxDiscountStr)) {
                Toast.makeText(this, "Vui lòng nhập giảm tối đa", Toast.LENGTH_SHORT).show();
                return;
            }
            maxDiscountAmount = Double.parseDouble(maxDiscountStr);
        }

        progressDialog.show();

        HashMap<String, Object> map = new HashMap<>();
        map.put("restaurantId", restaurantId);
        map.put("discountType", discountType);
        map.put("discountAmount", discountAmount);
        map.put("minimumOrder", minimumOrderStr);
        map.put("totalUsage", totalUsage);
        map.put("usagePerUser", usagePerUser);
        map.put("startDate", startDateStr);
        map.put("endDate", endDateStr);
        map.put("promoCode", promoCode);

        if ("percentage".equals(discountType)) {
            map.put("maxDiscountAmount", maxDiscountAmount);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("promotions");
        String key = isEditMode && promotionId != null ? promotionId : ref.push().getKey();

        if (key == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi hệ thống", Toast.LENGTH_SHORT).show();
            return;
        }

        ref.child(key).setValue(map)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, isEditMode ? "Cập nhật thành công" : "Tạo thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Thất bại: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
