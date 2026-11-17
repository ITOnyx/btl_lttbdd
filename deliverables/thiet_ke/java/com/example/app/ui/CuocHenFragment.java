package com.example.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.R;
import com.example.app.adapter.CuocHenAdapter;
import com.example.app.databinding.FragmentCuocHenBinding;
import com.example.app.db.CuocHenDao;
import com.example.app.model.CuocHen;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CuocHenFragment extends Fragment implements CuocHenAdapter.OnCuocHenActionListener {

    private static final String ARG_DO_AN_ID = "arg_do_an_id";

    private int doAnId;
    private FragmentCuocHenBinding binding;
    private CuocHenDao cuocHenDao;
    private CuocHenAdapter adapter;

    public static CuocHenFragment newInstance(int doAnId) {
        CuocHenFragment fragment = new CuocHenFragment();
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
        cuocHenDao = new CuocHenDao(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCuocHenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CuocHenAdapter(this);
        binding.rvCuocHen.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCuocHen.setAdapter(adapter);

        binding.fabAddCuocHen.setOnClickListener(v -> showAddEditDialog(null));

        getChildFragmentManager().setFragmentResultListener(
                ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    boolean confirmed = bundle.getBoolean(ConfirmDeleteDialogFragment.RESULT_KEY_CONFIRMED, false);
                    if (confirmed) {
                        long id = bundle.getLong(ConfirmDeleteDialogFragment.RESULT_KEY_ITEM_ID);
                        cuocHenDao.delete((int) id);
                        Toast.makeText(requireContext(), R.string.action_success, Toast.LENGTH_SHORT).show();
                        loadMeetings();
                    }
                }
        );

        loadMeetings();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMeetings();
    }

    public void loadMeetings() {
        if (!isAdded()) return;
        List<CuocHen> list = cuocHenDao.getByDoAnId(doAnId);
        adapter.submitList(list);

        if (list.isEmpty()) {
            binding.layoutEmptyMeetings.setVisibility(View.VISIBLE);
            binding.rvCuocHen.setVisibility(View.GONE);
        } else {
            binding.layoutEmptyMeetings.setVisibility(View.GONE);
            binding.rvCuocHen.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditClick(CuocHen item) {
        showAddEditDialog(item);
    }

    @Override
    public void onDeleteClick(CuocHen item) {
        String msg = String.format(getString(R.string.delete_meeting_confirm), item.getTieuDe());

        ConfirmDeleteDialogFragment dialog = ConfirmDeleteDialogFragment.newInstance(
                ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                getString(R.string.delete_meeting_title),
                msg,
                ConfirmDeleteDialogFragment.TYPE_CUOC_HEN,
                item.getId()
        );
        dialog.show(getChildFragmentManager(), ConfirmDeleteDialogFragment.TAG);
    }

    private void showAddEditDialog(CuocHen existing) {
        AddEditCuocHenDialogFragment dialog = AddEditCuocHenDialogFragment.newInstance(doAnId, existing);
        dialog.setOnCuocHenSavedListener(this::loadMeetings);
        dialog.show(getChildFragmentManager(), "AddEditCuocHenDialogFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
