package com.example.app.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.example.app.databinding.DialogAddEditDoAnBinding;
import com.example.app.db.DoAnDao;
import com.example.app.model.DoAn;
import com.example.app.util.DateUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddEditDoAnDialogFragment extends DialogFragment {

    public interface OnDoAnSavedListener {
        void onDoAnSaved();
    }

    private static final String ARG_DO_AN = "arg_do_an";

    private static final String TAG_PICKER_START_DATE = "tag_picker_start_date";
    private static final String TAG_PICKER_DUE_DATE = "tag_picker_due_date";

    private static final String KEY_TEN = "key_ten";
    private static final String KEY_MO_TA = "key_mo_ta";
    private static final String KEY_GIANG_VIEN = "key_giang_vien";
    private static final String KEY_NGAY_BAT_DAU = "key_ngay_bat_dau";
    private static final String KEY_HAN_NOP = "key_han_nop";

    private DoAn existingDoAn;
    private DialogAddEditDoAnBinding binding;
    private OnDoAnSavedListener listener;

    public static AddEditDoAnDialogFragment newInstance(@Nullable DoAn existing) {
        AddEditDoAnDialogFragment fragment = new AddEditDoAnDialogFragment();
        Bundle args = new Bundle();
        if (existing != null) {
            args.putSerializable(ARG_DO_AN, existing);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDoAnSavedListener(OnDoAnSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            existingDoAn = (DoAn) getArguments().getSerializable(ARG_DO_AN);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAddEditDoAnBinding.inflate(LayoutInflater.from(requireContext()));

        boolean isEdit = existingDoAn != null;
        binding.tvDialogTitle.setText(isEdit ? R.string.edit_project : R.string.add_project);

        if (savedInstanceState != null) {
            binding.etTen.setText(savedInstanceState.getString(KEY_TEN, ""));
            binding.etMoTa.setText(savedInstanceState.getString(KEY_MO_TA, ""));
            binding.etGiangVien.setText(savedInstanceState.getString(KEY_GIANG_VIEN, ""));
            binding.etNgayBatDau.setText(savedInstanceState.getString(KEY_NGAY_BAT_DAU, ""));
            binding.etHanNop.setText(savedInstanceState.getString(KEY_HAN_NOP, ""));
        } else if (isEdit) {
            binding.etTen.setText(existingDoAn.getTen());
            binding.etMoTa.setText(existingDoAn.getMoTa());
            binding.etGiangVien.setText(existingDoAn.getGiangVien());
            binding.etNgayBatDau.setText(existingDoAn.getNgayBatDau());
            binding.etHanNop.setText(existingDoAn.getHanNop());
        } else {
            binding.etNgayBatDau.setText(DateUtils.getCurrentDate());
        }

        binding.etNgayBatDau.setOnClickListener(v -> showStartDatePicker());
        binding.etHanNop.setOnClickListener(v -> showDueDatePicker());

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
        MaterialDatePicker<Long> startPicker =
                (MaterialDatePicker<Long>) getChildFragmentManager().findFragmentByTag(TAG_PICKER_START_DATE);
        if (startPicker != null) {
            startPicker.addOnPositiveButtonClickListener(selection -> {
                String dateStr = DateUtils.formatUtcDate(selection);
                binding.etNgayBatDau.setText(dateStr);
                binding.tilNgayBatDau.setError(null);
            });
        }

        MaterialDatePicker<Long> duePicker =
                (MaterialDatePicker<Long>) getChildFragmentManager().findFragmentByTag(TAG_PICKER_DUE_DATE);
        if (duePicker != null) {
            duePicker.addOnPositiveButtonClickListener(selection -> {
                String dateStr = DateUtils.formatUtcDate(selection);
                binding.etHanNop.setText(dateStr);
                binding.tilHanNop.setError(null);
            });
        }
    }

    private void showStartDatePicker() {
        String current = binding.etNgayBatDau.getText() != null
                ? binding.etNgayBatDau.getText().toString().trim()
                : DateUtils.getCurrentDate();
        Long selection = DateUtils.dateStringToUtcMillis(current);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date);
        if (selection != null) {
            builder.setSelection(selection);
        }
        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selectedMillis -> {
            binding.etNgayBatDau.setText(DateUtils.formatUtcDate(selectedMillis));
            binding.tilNgayBatDau.setError(null);
        });
        picker.show(getChildFragmentManager(), TAG_PICKER_START_DATE);
    }

    private void showDueDatePicker() {
        String current = binding.etHanNop.getText() != null
                ? binding.etHanNop.getText().toString().trim()
                : "";
        Long selection = DateUtils.dateStringToUtcMillis(current);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date);
        if (selection != null) {
            builder.setSelection(selection);
        }
        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selectedMillis -> {
            binding.etHanNop.setText(DateUtils.formatUtcDate(selectedMillis));
            binding.tilHanNop.setError(null);
        });
        picker.show(getChildFragmentManager(), TAG_PICKER_DUE_DATE);
    }

    private void handleSave() {
        String ten = binding.etTen.getText() != null
                ? binding.etTen.getText().toString().trim()
                : "";
        String moTa = binding.etMoTa.getText() != null
                ? binding.etMoTa.getText().toString().trim()
                : "";
        String giangVien = binding.etGiangVien.getText() != null
                ? binding.etGiangVien.getText().toString().trim()
                : "";
        String ngayBatDau = binding.etNgayBatDau.getText() != null
                ? binding.etNgayBatDau.getText().toString().trim()
                : "";
        String hanNop = binding.etHanNop.getText() != null
                ? binding.etHanNop.getText().toString().trim()
                : "";

        boolean hasError = false;

        if (ten.isEmpty()) {
            binding.tilTen.setError(getString(R.string.error_empty_project_name));
            hasError = true;
        } else {
            binding.tilTen.setError(null);
        }

        if (!hanNop.isEmpty() && !ngayBatDau.isEmpty()) {
            if (!DateUtils.isValidDateRange(ngayBatDau, hanNop)) {
                binding.tilHanNop.setError(getString(R.string.error_invalid_due_date));
                hasError = true;
            } else {
                binding.tilHanNop.setError(null);
            }
        } else {
            binding.tilHanNop.setError(null);
        }

        if (hasError) {
            return;
        }

        DoAnDao dao = new DoAnDao(requireContext());
        if (existingDoAn != null) {
            existingDoAn.setTen(ten);
            existingDoAn.setMoTa(moTa);
            existingDoAn.setGiangVien(giangVien);
            existingDoAn.setNgayBatDau(ngayBatDau);
            existingDoAn.setHanNop(hanNop);
            dao.update(existingDoAn);
        } else {
            DoAn newDoAn = new DoAn(ten, moTa, giangVien, ngayBatDau, hanNop);
            dao.insert(newDoAn);
        }

        Toast.makeText(requireContext(), R.string.action_success, Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onDoAnSaved();
        }
        dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding != null) {
            outState.putString(KEY_TEN, binding.etTen.getText() != null ? binding.etTen.getText().toString() : "");
            outState.putString(KEY_MO_TA, binding.etMoTa.getText() != null ? binding.etMoTa.getText().toString() : "");
            outState.putString(KEY_GIANG_VIEN, binding.etGiangVien.getText() != null ? binding.etGiangVien.getText().toString() : "");
            outState.putString(KEY_NGAY_BAT_DAU, binding.etNgayBatDau.getText() != null ? binding.etNgayBatDau.getText().toString() : "");
            outState.putString(KEY_HAN_NOP, binding.etHanNop.getText() != null ? binding.etHanNop.getText().toString() : "");
        }
    }
}
