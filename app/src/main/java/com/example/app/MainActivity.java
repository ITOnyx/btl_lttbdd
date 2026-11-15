package com.example.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.adapter.DoAnAdapter;
import com.example.app.databinding.ActivityMainBinding;
import com.example.app.databinding.DialogAddEditDoAnBinding;
import com.example.app.db.DoAnDao;
import com.example.app.model.DoAn;
import com.example.app.model.DoAnWithTienDo;
import com.example.app.util.DateUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DoAnAdapter.OnDoAnActionListener {

    private ActivityMainBinding binding;
    private DoAnDao doAnDao;
    private DoAnAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        doAnDao = new DoAnDao(this);

        getSupportFragmentManager().setFragmentResultListener(
                com.example.app.ui.ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                this,
                (requestKey, bundle) -> {
                    boolean confirmed = bundle.getBoolean(com.example.app.ui.ConfirmDeleteDialogFragment.RESULT_KEY_CONFIRMED, false);
                    if (confirmed) {
                        long id = bundle.getLong(com.example.app.ui.ConfirmDeleteDialogFragment.RESULT_KEY_ITEM_ID);
                        int rows = doAnDao.delete((int) id);
                        if (rows > 0) {
                            Toast.makeText(this, R.string.action_success, Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Toast.makeText(this, R.string.action_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initViews();
    }

    private void initViews() {
        adapter = new DoAnAdapter(this);
        binding.rvDoAn.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDoAn.setAdapter(adapter);

        binding.fabAddDoAn.setOnClickListener(v -> showAddEditDialog(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<DoAnWithTienDo> list = doAnDao.getAllWithTienDo();
        adapter.submitList(list);

        if (list.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvDoAn.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvDoAn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(DoAnWithTienDo item) {
        android.content.Intent intent = new android.content.Intent(this, com.example.app.ui.DoAnDetailActivity.class);
        intent.putExtra(com.example.app.ui.DoAnDetailActivity.EXTRA_DO_AN_ID, item.getDoAn().getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(DoAnWithTienDo item) {
        showAddEditDialog(item.getDoAn());
    }

    @Override
    public void onDeleteClick(DoAnWithTienDo item) {
        DoAn doAn = item.getDoAn();
        String message = String.format(getString(R.string.delete_project_confirm), doAn.getTen());

        com.example.app.ui.ConfirmDeleteDialogFragment dialog =
                com.example.app.ui.ConfirmDeleteDialogFragment.newInstance(
                        com.example.app.ui.ConfirmDeleteDialogFragment.REQUEST_KEY_DELETE,
                        getString(R.string.delete_project_title),
                        message,
                        com.example.app.ui.ConfirmDeleteDialogFragment.TYPE_DO_AN,
                        doAn.getId()
                );
        dialog.show(getSupportFragmentManager(), com.example.app.ui.ConfirmDeleteDialogFragment.TAG);
    }

    private void showAddEditDialog(DoAn existing) {
        com.example.app.ui.AddEditDoAnDialogFragment dialog = com.example.app.ui.AddEditDoAnDialogFragment.newInstance(existing);
        dialog.setOnDoAnSavedListener(this::loadData);
        dialog.show(getSupportFragmentManager(), "AddEditDoAnDialogFragment");
    }
}
