package com.example.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.databinding.ItemDoAnBinding;
import com.example.app.model.DoAn;
import com.example.app.model.DoAnWithTienDo;
import com.example.app.model.TienDoInfo;

import java.util.Objects;

public class DoAnAdapter extends ListAdapter<DoAnWithTienDo, DoAnAdapter.ViewHolder> {

    public interface OnDoAnActionListener {
        void onItemClick(DoAnWithTienDo item);
        void onEditClick(DoAnWithTienDo item);
        void onDeleteClick(DoAnWithTienDo item);
    }

    private final OnDoAnActionListener listener;

    private static final DiffUtil.ItemCallback<DoAnWithTienDo> DIFF_CALLBACK = new DiffUtil.ItemCallback<DoAnWithTienDo>() {
        @Override
        public boolean areItemsTheSame(@NonNull DoAnWithTienDo oldItem, @NonNull DoAnWithTienDo newItem) {
            return oldItem.getDoAn().getId() == newItem.getDoAn().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DoAnWithTienDo oldItem, @NonNull DoAnWithTienDo newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public DoAnAdapter(OnDoAnActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDoAnBinding binding = ItemDoAnBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoAnWithTienDo item = getItem(position);
        holder.bind(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDoAnBinding binding;

        public ViewHolder(@NonNull ItemDoAnBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(DoAnWithTienDo item) {
            DoAn doAn = item.getDoAn();
            TienDoInfo tienDo = item.getTienDo();

            binding.tvTen.setText(doAn.getTen());

            // Mo ta
            if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
                binding.tvMoTa.setVisibility(View.VISIBLE);
                binding.tvMoTa.setText(doAn.getMoTa().trim());
            } else {
                binding.tvMoTa.setVisibility(View.GONE);
            }

            // Giang vien
            String gv = (doAn.getGiangVien() != null && !doAn.getGiangVien().trim().isEmpty())
                    ? String.format(itemView.getContext().getString(R.string.supervisor_format), doAn.getGiangVien().trim())
                    : String.format(itemView.getContext().getString(R.string.supervisor_format), itemView.getContext().getString(R.string.supervisor_unassigned));
            binding.tvGiangVien.setText(gv);

            // Thoi gian / Han nop
            String start = doAn.getNgayBatDau();
            String end = doAn.getHanNop();
            if (end != null && !end.trim().isEmpty()) {
                if (start != null && !start.trim().isEmpty()) {
                    binding.tvThoiGian.setText(String.format(itemView.getContext().getString(R.string.date_range_format), start.trim(), end.trim()));
                } else {
                    binding.tvThoiGian.setText(String.format(itemView.getContext().getString(R.string.due_date_format), end.trim()));
                }
            } else {
                binding.tvThoiGian.setText(itemView.getContext().getString(R.string.no_due_date));
            }

            // Tien do
            if (tienDo != null) {
                int percent = tienDo.getPercent();
                binding.progressBar.setProgress(percent);
                binding.tvTienDo.setText(tienDo.getFormattedText());
            } else {
                binding.progressBar.setProgress(0);
                binding.tvTienDo.setText(itemView.getContext().getString(R.string.default_progress_text));
            }

            // Click card -> xem chi tiet
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            // Click sua
            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(item);
                }
            });

            // Click xoa
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}
