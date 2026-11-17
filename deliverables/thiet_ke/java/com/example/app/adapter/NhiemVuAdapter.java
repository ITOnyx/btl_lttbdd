package com.example.app.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.databinding.ItemNhiemVuBinding;
import com.example.app.model.NhiemVu;

import java.util.Objects;

public class NhiemVuAdapter extends ListAdapter<NhiemVu, NhiemVuAdapter.ViewHolder> {

    public interface OnNhiemVuActionListener {
        void onStatusChanged(NhiemVu item, boolean isChecked);
        void onEditClick(NhiemVu item);
        void onDeleteClick(NhiemVu item);
    }

    private final OnNhiemVuActionListener listener;

    private static final DiffUtil.ItemCallback<NhiemVu> DIFF_CALLBACK = new DiffUtil.ItemCallback<NhiemVu>() {
        @Override
        public boolean areItemsTheSame(@NonNull NhiemVu oldItem, @NonNull NhiemVu newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull NhiemVu oldItem, @NonNull NhiemVu newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public NhiemVuAdapter(OnNhiemVuActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNhiemVuBinding binding = ItemNhiemVuBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NhiemVu item = getItem(position);
        holder.bind(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNhiemVuBinding binding;

        public ViewHolder(@NonNull ItemNhiemVuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NhiemVu item) {
            binding.tvTenNhiemVu.setText(item.getTen());

            // Ghi chu
            if (item.getGhiChu() != null && !item.getGhiChu().trim().isEmpty()) {
                binding.tvGhiChu.setVisibility(View.VISIBLE);
                binding.tvGhiChu.setText(item.getGhiChu().trim());
            } else {
                binding.tvGhiChu.setVisibility(View.GONE);
            }

            // Han hoan thanh
            if (item.getHanHoanThanh() != null && !item.getHanHoanThanh().trim().isEmpty()) {
                binding.tvHanHoanThanh.setText(String.format(itemView.getContext().getString(R.string.task_due_format), item.getHanHoanThanh().trim()));
            } else {
                binding.tvHanHoanThanh.setText(itemView.getContext().getString(R.string.task_no_due));
            }

            // Xu ly strikethrough va mau sac text theo trang thai
            if (item.isCompleted()) {
                binding.tvTenNhiemVu.setPaintFlags(binding.tvTenNhiemVu.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvTenNhiemVu.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
            } else {
                binding.tvTenNhiemVu.setPaintFlags(binding.tvTenNhiemVu.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvTenNhiemVu.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_primary));
            }

            // CheckBox: tranh loi tai su dung ViewHolder
            binding.cbHoanThanh.setOnCheckedChangeListener(null);
            binding.cbHoanThanh.setChecked(item.isCompleted());
            binding.cbHoanThanh.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onStatusChanged(item, isChecked);
                }
            });

            // Click sua
            binding.btnEditTask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(item);
                }
            });

            // Click xoa
            binding.btnDeleteTask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}
