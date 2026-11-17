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
import com.example.app.databinding.DialogAddEditNhiemVuBinding;
import com.example.app.db.DoAnDao;
import com.example.app.db.NhiemVuDao;
import com.example.app.model.DoAn;
import com.example.app.model.NhiemVu;
import com.example.app.util.DateUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddEditNhiemVuDialogFragment extends DialogFragment {

    public interface OnNhiemVuSavedListener {
        void onNhiemVuSaved();
    }

    private static final String ARG_DO_AN_ID = "arg_do_an_id";
    private static final String ARG_NHIEM_VU = "arg_nhiem_vu";

    private static final String TAG_PICKER_TASK_DUE = "tag_picker_task_due";

    private static final String KEY_TEN = "key_ten";
    private static final String KEY_GHI_CHU = "key_ghi_chu";
    private static final String KEY_HAN = "key_han";
    private static final String KEY_DA_HOAN_THANH = "key_da_hoan_thanh";

    private int doAnId;
    private NhiemVu existingNhiemVu;
    private DialogAddEditNhiemVuBinding binding;
    private OnNhiemVuSavedListener listener;

    public static AddEditNhiemVuDialogFragment newInstance(int doAnId, @Nullable NhiemVu existing) {
        AddEditNhiemVuDialogFragment fragment = new AddEditNhiemVuDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DO_AN_ID, doAnId);
        if (existing != null) {
            args.putSerializable(ARG_NHIEM_VU, existing);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnNhiemVuSavedListener(OnNhiemVuSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doAnId = getArguments().getInt(ARG_DO_AN_ID, -1);
            existingNhiemVu = (NhiemVu) getArguments().getSerializable(ARG_NHIEM_VU);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAddEditNhiemVuBinding.inflate(LayoutInflater.from(requireContext()));

        boolean isEdit = existingNhiemVu != null;
        binding.tvDialogTitle.setText(isEdit ? R.string.edit_task : R.string.add_task);

        if (savedInstanceState != null) {
            binding.etTenNhiemVu.setText(savedInstanceState.getString(KEY_TEN, ""));
            binding.etGhiChu.setText(savedInstanceState.getString(KEY_GHI_CHU, ""));
            binding.etHanHoanThanh.setText(savedInstanceState.getString(KEY_HAN, ""));
            binding.cbDaHoanThanh.setChecked(savedInstanceState.getBoolean(KEY_DA_HOAN_THANH, false));
        } else if (isEdit) {
            binding.etTenNhiemVu.setText(existingNhiemVu.getTen());
            binding.etGhiChu.setText(existingNhiemVu.getGhiChu());
            binding.etHanHoanThanh.setText(existingNhiemVu.getHanHoanThanh());
            binding.cbDaHoanThanh.setChecked(existingNhiemVu.isCompleted());
        } else {
            binding.cbDaHoanThanh.setChecked(false);
        }

        binding.etHanHoanThanh.setOnClickListener(v -> showDueDatePicker());

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
        MaterialDatePicker<Long> duePicker =
                (MaterialDatePicker<Long>) getChildFragmentManager().findFragmentByTag(TAG_PICKER_TASK_DUE);
        if (duePicker != null) {
            duePicker.addOnPositiveButtonClickListener(selection -> {
                String dateStr = DateUtils.formatUtcDate(selection);
                binding.etHanHoanThanh.setText(dateStr);
                binding.tilHanHoanThanh.setError(null);
            });
        }
    }

    private void showDueDatePicker() {
        String current = binding.etHanHoanThanh.getText() != null
                ? binding.etHanHoanThanh.getText().toString().trim()
                : "";
        Long selection = DateUtils.dateStringToUtcMillis(current);

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date);
        if (selection != null) {
            builder.setSelection(selection);
        }
        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selectedMillis -> {
            binding.etHanHoanThanh.setText(DateUtils.formatUtcDate(selectedMillis));
            binding.tilHanHoanThanh.setError(null);
        });
        picker.show(getChildFragmentManager(), TAG_PICKER_TASK_DUE);
    }

    private void handleSave() {
        String ten = binding.etTenNhiemVu.getText() != null
                ? binding.etTenNhiemVu.getText().toString().trim()
                : "";
        String ghiChu = binding.etGhiChu.getText() != null
                ? binding.etGhiChu.getText().toString().trim()
                : "";
        String hanHoanThanh = binding.etHanHoanThanh.getText() != null
                ? binding.etHanHoanThanh.getText().toString().trim()
                : "";
        boolean daHoanThanh = binding.cbDaHoanThanh.isChecked();

        boolean hasError = false;

        if (ten.isEmpty()) {
            binding.tilTenNhiemVu.setError(getString(R.string.error_empty_task_name));
            hasError = true;
        } else {
            binding.tilTenNhiemVu.setError(null);
        }

        DoAnDao doAnDao = new DoAnDao(requireContext());
        DoAn doAn = doAnDao.getById(doAnId);
        if (doAn != null && doAn.getHanNop() != null && !doAn.getHanNop().trim().isEmpty() && !hanHoanThanh.isEmpty()) {
            if (!DateUtils.isValidDateRange(hanHoanThanh, doAn.getHanNop().trim())) {
                binding.tilHanHoanThanh.setError(getString(R.string.error_task_due_after_project, doAn.getHanNop().trim()));
                hasError = true;
            } else {
                binding.tilHanHoanThanh.setError(null);
            }
        } else {
            binding.tilHanHoanThanh.setError(null);
        }

        if (hasError) {
            return;
        }

        NhiemVuDao dao = new NhiemVuDao(requireContext());
        if (existingNhiemVu != null) {
            existingNhiemVu.setTen(ten);
            existingNhiemVu.setGhiChu(ghiChu);
            existingNhiemVu.setHanHoanThanh(hanHoanThanh);
            existingNhiemVu.setCompleted(daHoanThanh);
            dao.update(existingNhiemVu);
        } else {
            NhiemVu newNv = new NhiemVu(doAnId, ten, ghiChu, hanHoanThanh, daHoanThanh ? 1 : 0);
            dao.insert(newNv);
        }

        Toast.makeText(requireContext(), R.string.action_success, Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onNhiemVuSaved();
        }
        dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding != null) {
            outState.putString(KEY_TEN, binding.etTenNhiemVu.getText() != null ? binding.etTenNhiemVu.getText().toString() : "");
            outState.putString(KEY_GHI_CHU, binding.etGhiChu.getText() != null ? binding.etGhiChu.getText().toString() : "");
            outState.putString(KEY_HAN, binding.etHanHoanThanh.getText() != null ? binding.etHanHoanThanh.getText().toString() : "");
            outState.putBoolean(KEY_DA_HOAN_THANH, binding.cbDaHoanThanh.isChecked());
        }
    }
}
