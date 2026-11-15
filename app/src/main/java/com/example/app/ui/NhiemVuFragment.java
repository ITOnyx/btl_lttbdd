package com.example.app.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.R;
import com.example.app.adapter.NhiemVuAdapter;
import com.example.app.databinding.DialogAddEditNhiemVuBinding;
import com.example.app.databinding.FragmentNhiemVuBinding;
import com.example.app.db.DoAnDao;
import com.example.app.db.NhiemVuDao;
import com.example.app.model.DoAn;
import com.example.app.model.NhiemVu;
import com.example.app.util.DateUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class NhiemVuFragment extends Fragment implements NhiemVuAdapter.OnNhiemVuActionListener {

    private static final String ARG_DO_AN_ID = "arg_do_an_id";

    private int doAnId;
    private FragmentNhiemVuBinding binding;
    private NhiemVuDao nhiemVuDao;
    private DoAnDao doAnDao;
    private NhiemVuAdapter adapter;

    public static NhiemVuFragment newInstance(int doAnId) {
        NhiemVuFragment fragment = new NhiemVuFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DO_AN_ID, doAnId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doAnId = getArguments().getInt(ARG_DO_AN_ID, -1);
        }
        nhiemVuDao = new NhiemVuDao(requireContext());
        doAnDao = new DoAnDao(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNhiemVuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new NhiemVuAdapter(this);
        binding.rvNhiemVu.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNhiemVu.setAdapter(adapter);

        binding.fabAddNhiemVu.setOnClickListener(v -> showAddEditDialog(null));

        getChildFragmentManager().setFragmentResultListener(
                ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean confirmed = bundle.getBoolean(ConfirmDeleteDialogFragment.RESULT_KEY_CONFIRMED, false);
                    if (confirmed) {
                        long id = bundle.getLong(ConfirmDeleteDialogFragment.RESULT_KEY_ITEM_ID);
                        nhiemVuDao.delete((int) id);
                        Toast.makeText(requireContext(), R.string.action_success, Toast.LENGTH_SHORT).show();
                        loadTasks();
                    }
                }
        );

        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    public void loadTasks() {
        if (!isAdded()) return;
        List<NhiemVu> list = nhiemVuDao.getByDoAnId(doAnId);
        adapter.submitList(list);

        if (list.isEmpty()) {
            binding.layoutEmptyTasks.setVisibility(View.VISIBLE);
            binding.rvNhiemVu.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyTasks.setVisibility(View.GONE);
            binding.rvNhiemVu.setVisibility(View.VISIBLE);
        }

        notifyActivityProgressChanged();
    }

    private void notifyActivityProgressChanged() {
        if (getActivity() instanceof DoAnDetailActivity) {
            ((DoAnDetailActivity) getActivity()).updateHeaderProgress();
        }
    }

    @Override
    public void onStatusChanged(NhiemVu item, boolean isChecked) {
        nhiemVuDao.updateStatus(item.getId(), isChecked);
        loadTasks();
    }

    @Override
    public void onEditClick(NhiemVu item) {
        showAddEditDialog(item);
    }

    @Override
    public void onDeleteClick(NhiemVu item) {
        String msg = String.format(getString(R.string.delete_task_confirm), item.getTen());

        ConfirmDeleteDialogFragment dialog = ConfirmDeleteDialogFragment.newInstance(
                ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                getString(R.string.delete_task_title),
                msg,
                ConfirmDeleteDialogFragment.TYPE_NHIEM_VU,
                item.getId()
        );
        dialog.show(getChildFragmentManager(), ConfirmDeleteDialogFragment.TAG);
    }

    private void showAddEditDialog(NhiemVu existing) {
        AddEditNhiemVuDialogFragment dialog = AddEditNhiemVuDialogFragment.newInstance(doAnId, existing);
        dialog.setOnNhiemVuSavedListener(this::loadTasks);
        dialog.show(getChildFragmentManager(), "AddEditNhiemVuDialogFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
