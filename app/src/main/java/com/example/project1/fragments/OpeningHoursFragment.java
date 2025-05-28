package com.example.project1.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OpeningHoursFragment extends Fragment {

    private TextView tvMondayOpen, tvMondayClose,
            tvTuesdayOpen, tvTuesdayClose,
            tvWednesdayOpen, tvWednesdayClose,
            tvThursdayOpen, tvThursdayClose,
            tvFridayOpen, tvFridayClose,
            tvSaturdayOpen, tvSaturdayClose,
            tvSundayOpen, tvSundayClose;

    private Button btnSaveHours;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_opening_hours, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo tất cả TextView
        tvMondayOpen = view.findViewById(R.id.tvMondayOpen);
        tvMondayClose = view.findViewById(R.id.tvMondayClose);
        tvTuesdayOpen = view.findViewById(R.id.tvTuesdayOpen);
        tvTuesdayClose = view.findViewById(R.id.tvTuesdayClose);
        tvWednesdayOpen = view.findViewById(R.id.tvWednesdayOpen);
        tvWednesdayClose = view.findViewById(R.id.tvWednesdayClose);
        tvThursdayOpen = view.findViewById(R.id.tvThursdayOpen);
        tvThursdayClose = view.findViewById(R.id.tvThursdayClose);
        tvFridayOpen = view.findViewById(R.id.tvFridayOpen);
        tvFridayClose = view.findViewById(R.id.tvFridayClose);
        tvSaturdayOpen = view.findViewById(R.id.tvSaturdayOpen);
        tvSaturdayClose = view.findViewById(R.id.tvSaturdayClose);
        tvSundayOpen = view.findViewById(R.id.tvSundayOpen);
        tvSundayClose = view.findViewById(R.id.tvSundayClose);

        btnSaveHours = view.findViewById(R.id.btnSaveHours);

        setIndividualTimePicker(tvMondayOpen);
        setIndividualTimePicker(tvMondayClose);

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
            HashMap<String, String[]> openingHours = new HashMap<>();

            openingHours.put("monday", new String[]{tvMondayOpen.getText().toString(), tvMondayClose.getText().toString()});
            openingHours.put("tuesday", new String[]{tvTuesdayOpen.getText().toString(), tvTuesdayClose.getText().toString()});
            openingHours.put("wednesday", new String[]{tvWednesdayOpen.getText().toString(), tvWednesdayClose.getText().toString()});
            openingHours.put("thursday", new String[]{tvThursdayOpen.getText().toString(), tvThursdayClose.getText().toString()});
            openingHours.put("friday", new String[]{tvFridayOpen.getText().toString(), tvFridayClose.getText().toString()});
            openingHours.put("saturday", new String[]{tvSaturdayOpen.getText().toString(), tvSaturdayClose.getText().toString()});
            openingHours.put("sunday", new String[]{tvSundayOpen.getText().toString(), tvSundayClose.getText().toString()});

            Bundle result = new Bundle();
            result.putSerializable("opening_hours", openingHours);

            getParentFragmentManager().setFragmentResult("opening_hours_result", result);
            requireActivity().getSupportFragmentManager().popBackStack();  // quay lại AccountFragment
        });
    }

    // Hiển thị TimePicker và callback sau khi chọn giờ
    private void showTimePicker(TextView target, OnTimePickedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), (view, hourOfDay, minuteOfHour) -> {
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
