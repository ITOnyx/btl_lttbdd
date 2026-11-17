package com.example.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.example.app.databinding.DialogAddEditCuocHenBinding;
import com.example.app.db.CuocHenDao;
import com.example.app.model.CuocHen;
import com.example.app.util.DateUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.Locale;

public class AddEditCuocHenDialogFragment extends DialogFragment {

    public interface OnCuocHenSavedListener {
        void onCuocHenSaved();
    }

    private static final String ARG_DO_AN_ID = "arg_do_an_id";
    private static final String ARG_CUOC_HEN = "arg_cuoc_hen";

    private static final String TAG_PICKER_MEETING_DATE = "tag_picker_meeting_date";
    private static final String TAG_PICKER_MEETING_TIME = "tag_picker_meeting_time";

    private static final String KEY_TIEU_DE = "key_tieu_de";
    private static final String KEY_THOI_GIAN = "key_thoi_gian";
    private static final String KEY_DIA_DIEM = "key_dia_diem";
    private static final String KEY_GHI_CHU = "key_ghi_chu";
    private static final String KEY_TEMP_SELECTED_DATE = "key_temp_selected_date";

    private int doAnId;
    private CuocHen existingCuocHen;
    private DialogAddEditCuocHenBinding binding;
    private OnCuocHenSavedListener listener;
    private String tempSelectedDate;

    public static AddEditCuocHenDialogFragment newInstance(int doAnId, @Nullable CuocHen existing) {
        AddEditCuocHenDialogFragment fragment = new AddEditCuocHenDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DO_AN_ID, doAnId);
        if (existing != null) {
            args.putSerializable(ARG_CUOC_HEN, existing);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnCuocHenSavedListener(OnCuocHenSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doAnId = getArguments().getInt(ARG_DO_AN_ID, -1);
            existingCuocHen = (CuocHen) getArguments().getSerializable(ARG_CUOC_HEN);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAddEditCuocHenBinding.inflate(LayoutInflater.from(requireContext()));

        boolean isEdit = existingCuocHen != null;
        binding.tvDialogTitle.setText(isEdit ? R.string.edit_meeting : R.string.add_meeting);

        if (savedInstanceState != null) {
            // Khoi phuc du lieu khi xoay man hinh
            tempSelectedDate = savedInstanceState.getString(KEY_TEMP_SELECTED_DATE, null);
            binding.etTieuDe.setText(savedInstanceState.getString(KEY_TIEU_DE, ""));
            binding.etThoiGian.setText(savedInstanceState.getString(KEY_THOI_GIAN, ""));
            binding.etDiaDiem.setText(savedInstanceState.getString(KEY_DIA_DIEM, ""));
            binding.etGhiChu.setText(savedInstanceState.getString(KEY_GHI_CHU, ""));
        } else if (isEdit) {
            binding.etTieuDe.setText(existingCuocHen.getTieuDe());
            binding.etThoiGian.setText(existingCuocHen.getThoiGian());
            binding.etDiaDiem.setText(existingCuocHen.getDiaDiem());
            binding.etGhiChu.setText(existingCuocHen.getGhiChu());
        } else {
            binding.etThoiGian.setText(DateUtils.getCurrentDateTime());
        }

        // Chon thoi gian: MaterialDatePicker -> MaterialTimePicker (24h)
        binding.etThoiGian.setOnClickListener(v -> showDateTimePicker());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(binding.getRoot())
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (d, w) -> dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> handleSave());
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        rebindPickerListeners();
    }

    @SuppressWarnings("unchecked")
    private void rebindPickerListeners() {
        MaterialDatePicker<Long> datePicker =
                (MaterialDatePicker<Long>) getChildFragmentManager().findFragmentByTag(TAG_PICKER_MEETING_DATE);
        if (datePicker != null) {
            datePicker.addOnPositiveButtonClickListener(selection -> {
                tempSelectedDate = DateUtils.formatUtcDate(selection);
                showTimePicker();
            });
        }

        MaterialTimePicker timePicker =
                (MaterialTimePicker) getChildFragmentManager().findFragmentByTag(TAG_PICKER_MEETING_TIME);
        if (timePicker != null) {
            timePicker.addOnPositiveButtonClickListener(v -> applySelectedDateTime(timePicker.getHour(), timePicker.getMinute()));
        }
    }

    private void showDateTimePicker() {
        String current = binding.etThoiGian.getText() != null
                ? binding.etThoiGian.getText().toString().trim()
                : DateUtils.getCurrentDateTime();

        String initialDate = current.length() >= 10 ? current.substring(0, 10) : DateUtils.getCurrentDate();
        Long selection = DateUtils.dateStringToUtcMillis(initialDate);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date);
        if (selection != null) {
            builder.setSelection(selection);
        }
        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selectedMillis -> {
            tempSelectedDate = DateUtils.formatUtcDate(selectedMillis);
            showTimePicker();
        });
        picker.show(getChildFragmentManager(), TAG_PICKER_MEETING_DATE);
    }

    private void showTimePicker() {
        String current = binding.etThoiGian.getText() != null
                ? binding.etThoiGian.getText().toString().trim()
                : DateUtils.getCurrentDateTime();

        int hour = 9;
        int minute = 0;
        if (current.length() >= 16) {
            try {
                hour = Integer.parseInt(current.substring(11, 13));
                minute = Integer.parseInt(current.substring(14, 16));
            } catch (NumberFormatException ignored) {}
        } else {
            Calendar cal = Calendar.getInstance();
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);
        }

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText(R.string.select_time)
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> applySelectedDateTime(timePicker.getHour(), timePicker.getMinute()));
        timePicker.show(getChildFragmentManager(), TAG_PICKER_MEETING_TIME);
    }

    private void applySelectedDateTime(int hour, int minute) {
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        String datePart = tempSelectedDate;
        if (datePart == null || datePart.trim().isEmpty()) {
            String current = binding.etThoiGian.getText() != null
                    ? binding.etThoiGian.getText().toString().trim()
                    : "";
            if (current.length() >= 10) {
                datePart = current.substring(0, 10);
            } else {
                datePart = DateUtils.getCurrentDate();
            }
        }
        String finalDateTime = datePart + " " + timeStr;
        binding.etThoiGian.setText(finalDateTime);
        binding.tilThoiGian.setError(null);
    }

    private void handleSave() {
        String tieuDe = binding.etTieuDe.getText() != null
                ? binding.etTieuDe.getText().toString().trim()
                : "";
        String thoiGian = binding.etThoiGian.getText() != null
                ? binding.etThoiGian.getText().toString().trim()
                : "";
        String diaDiem = binding.etDiaDiem.getText() != null
                ? binding.etDiaDiem.getText().toString().trim()
                : "";
        String ghiChu = binding.etGhiChu.getText() != null
                ? binding.etGhiChu.getText().toString().trim()
                : "";

        boolean hasError = false;

        // 1. Kiem tra tieu de khong rong
        if (tieuDe.isEmpty()) {
            binding.tilTieuDe.setError(getString(R.string.error_empty_meeting_title));
            hasError = true;
        } else {
            binding.tilTieuDe.setError(null);
        }

        // 2. Kiem tra thoi gian khong rong
        if (thoiGian.isEmpty()) {
            binding.tilThoiGian.setError(getString(R.string.error_empty_meeting_time));
            hasError = true;
        } else {
            binding.tilThoiGian.setError(null);
        }

        if (hasError) {
            return;
        }

        CuocHenDao dao = new CuocHenDao(requireContext());
        if (existingCuocHen != null) {
            existingCuocHen.setTieuDe(tieuDe);
            existingCuocHen.setThoiGian(thoiGian);
            existingCuocHen.setDiaDiem(diaDiem);
            existingCuocHen.setGhiChu(ghiChu);
            dao.update(existingCuocHen);
        } else {
            CuocHen newCh = new CuocHen(doAnId, tieuDe, thoiGian, diaDiem, ghiChu);
            dao.insert(newCh);
        }

        Toast.makeText(requireContext(), R.string.action_success, Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onCuocHenSaved();
        }
        dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TEMP_SELECTED_DATE, tempSelectedDate);
        if (binding != null) {
            outState.putString(KEY_TIEU_DE, binding.etTieuDe.getText() != null ? binding.etTieuDe.getText().toString() : "");
            outState.putString(KEY_THOI_GIAN, binding.etThoiGian.getText() != null ? binding.etThoiGian.getText().toString() : "");
            outState.putString(KEY_DIA_DIEM, binding.etDiaDiem.getText() != null ? binding.etDiaDiem.getText().toString() : "");
            outState.putString(KEY_GHI_CHU, binding.etGhiChu.getText() != null ? binding.etGhiChu.getText().toString() : "");
        }
    }
}
