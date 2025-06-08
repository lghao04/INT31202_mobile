package com.example.project1.activites;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OpenHoursActivity extends AppCompatActivity {
    private TextView tvMondayOpen, tvMondayClose,
            tvTuesdayOpen, tvTuesdayClose,
            tvWednesdayOpen, tvWednesdayClose,
            tvThursdayOpen, tvThursdayClose,
            tvFridayOpen, tvFridayClose,
            tvSaturdayOpen, tvSaturdayClose,
            tvSundayOpen, tvSundayClose;
    HashMap<String, String[]> openingHoursMap = null;

    private Button btnSaveHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_hours);
        tvMondayOpen = findViewById(R.id.tvMondayOpen);
        tvMondayClose = findViewById(R.id.tvMondayClose);
        tvTuesdayOpen = findViewById(R.id.tvTuesdayOpen);
        tvTuesdayClose = findViewById(R.id.tvTuesdayClose);
        tvWednesdayOpen = findViewById(R.id.tvWednesdayOpen);
        tvWednesdayClose = findViewById(R.id.tvWednesdayClose);
        tvThursdayOpen = findViewById(R.id.tvThursdayOpen);
        tvThursdayClose = findViewById(R.id.tvThursdayClose);
        tvFridayOpen = findViewById(R.id.tvFridayOpen);
        tvFridayClose = findViewById(R.id.tvFridayClose);
        tvSaturdayOpen = findViewById(R.id.tvSaturdayOpen);
        tvSaturdayClose = findViewById(R.id.tvSaturdayClose);
        tvSundayOpen = findViewById(R.id.tvSundayOpen);
        tvSundayClose = findViewById(R.id.tvSundayClose);

        btnSaveHours = findViewById(R.id.btnSaveHours);

        if (getIntent() != null && getIntent().hasExtra("opening_hours")) {
            openingHoursMap = (HashMap<String, String[]>) getIntent().getSerializableExtra("opening_hours");
        }

        if (openingHoursMap != null) {
            if (openingHoursMap.containsKey("monday")) {
                tvMondayOpen.setText(openingHoursMap.get("monday")[0]);
                tvMondayClose.setText(openingHoursMap.get("monday")[1]);
            }
            if (openingHoursMap.containsKey("tuesday")) {
                tvTuesdayOpen.setText(openingHoursMap.get("tuesday")[0]);
                tvTuesdayClose.setText(openingHoursMap.get("tuesday")[1]);
            }
            if (openingHoursMap.containsKey("wednesday")) {
                tvWednesdayOpen.setText(openingHoursMap.get("wednesday")[0]);
                tvWednesdayClose.setText(openingHoursMap.get("wednesday")[1]);
            }
            if (openingHoursMap.containsKey("thursday")) {
                tvThursdayOpen.setText(openingHoursMap.get("thursday")[0]);
                tvThursdayClose.setText(openingHoursMap.get("thursday")[1]);
            }
            if (openingHoursMap.containsKey("friday")) {
                tvFridayOpen.setText(openingHoursMap.get("friday")[0]);
                tvFridayClose.setText(openingHoursMap.get("friday")[1]);
            }
            if (openingHoursMap.containsKey("saturday")) {
                tvSaturdayOpen.setText(openingHoursMap.get("saturday")[0]);
                tvSaturdayClose.setText(openingHoursMap.get("saturday")[1]);
            }
            if (openingHoursMap.containsKey("sunday")) {
                tvSundayOpen.setText(openingHoursMap.get("sunday")[0]);
                tvSundayClose.setText(openingHoursMap.get("sunday")[1]);
            }
        }

        // Monday: chọn giờ + copy sang ngày khác
        tvMondayOpen.setOnClickListener(v -> {
            showTimePicker(tvMondayOpen, time -> copyTimeToOtherDays(time, true));
        });

        tvMondayClose.setOnClickListener(v -> {
            showTimePicker(tvMondayClose, time -> copyTimeToOtherDays(time, false));
        });

        // Các ngày còn lại: chỉ chọn riêng lẻ
        setIndividualTimePicker(tvTuesdayOpen);
        setIndividualTimePicker(tvTuesdayClose);
        setIndividualTimePicker(tvWednesdayOpen);
        setIndividualTimePicker(tvWednesdayClose);
        setIndividualTimePicker(tvThursdayOpen);
        setIndividualTimePicker(tvThursdayClose);
        setIndividualTimePicker(tvFridayOpen);
        setIndividualTimePicker(tvFridayClose);
        setIndividualTimePicker(tvSaturdayOpen);
        setIndividualTimePicker(tvSaturdayClose);
        setIndividualTimePicker(tvSundayOpen);
        setIndividualTimePicker(tvSundayClose);

        btnSaveHours.setOnClickListener(v -> {
            // Khởi tạo openingHours kiểu Map<String, Map<String, String>>
            Map<String, Map<String, String>> openingHours = new HashMap<>();

            openingHours.put("monday", createTimeMap(tvMondayOpen.getText().toString(), tvMondayClose.getText().toString()));
            openingHours.put("tuesday", createTimeMap(tvTuesdayOpen.getText().toString(), tvTuesdayClose.getText().toString()));
            openingHours.put("wednesday", createTimeMap(tvWednesdayOpen.getText().toString(), tvWednesdayClose.getText().toString()));
            openingHours.put("thursday", createTimeMap(tvThursdayOpen.getText().toString(), tvThursdayClose.getText().toString()));
            openingHours.put("friday", createTimeMap(tvFridayOpen.getText().toString(), tvFridayClose.getText().toString()));
            openingHours.put("saturday", createTimeMap(tvSaturdayOpen.getText().toString(), tvSaturdayClose.getText().toString()));
            openingHours.put("sunday", createTimeMap(tvSundayOpen.getText().toString(), tvSundayClose.getText().toString()));

            // Lưu vào Firebase
            String restaurantId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("openingHours", openingHours);

            dbRef.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(OpenHoursActivity.this, "Đã lưu giờ mở cửa", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OpenHoursActivity.this, "Lỗi khi lưu giờ mở cửa", Toast.LENGTH_SHORT).show();
                }
            });

            // Trả kết quả về fragment trước đó nếu cần
            Intent resultIntent = new Intent();
            resultIntent.putExtra("opening_hours", (Serializable) openingHours);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private Map<String, String> createTimeMap(String open, String close) {
        Map<String, String> timeMap = new HashMap<>();
        timeMap.put("open", open);
        timeMap.put("close", close);
        return timeMap;
    }

    // Hiển thị TimePicker và callback sau khi chọn giờ
    private void showTimePicker(TextView target, OnTimePickedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(OpenHoursActivity.this, (view, hourOfDay, minuteOfHour) -> {
            String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
            target.setText(time);
            if (listener != null) listener.onTimePicked(time);
        }, hour, minute, true).show();
    }

    // Cài đặt TimePicker riêng cho mỗi TextView
    private void setIndividualTimePicker(TextView tv) {
        tv.setOnClickListener(v -> showTimePicker(tv, null));
    }

    // Copy giờ từ thứ 2 sang các ngày khác
    private void copyTimeToOtherDays(String time, boolean isOpeningTime) {
        if (isOpeningTime) {
            tvTuesdayOpen.setText(time);
            tvWednesdayOpen.setText(time);
            tvThursdayOpen.setText(time);
            tvFridayOpen.setText(time);
            tvSaturdayOpen.setText(time);
            tvSundayOpen.setText(time);
        } else {
            tvTuesdayClose.setText(time);
            tvWednesdayClose.setText(time);
            tvThursdayClose.setText(time);
            tvFridayClose.setText(time);
            tvSaturdayClose.setText(time);
            tvSundayClose.setText(time);
        }
    }

    // Interface dùng cho callback chọn giờ
    interface OnTimePickedListener {
        void onTimePicked(String time);
    }
}
