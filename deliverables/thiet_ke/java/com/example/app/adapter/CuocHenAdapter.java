package com.example.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.databinding.ItemCuocHenBinding;
import com.example.app.model.CuocHen;
import com.example.app.util.DateUtils;

import java.util.Objects;

public class CuocHenAdapter extends ListAdapter<CuocHen, CuocHenAdapter.ViewHolder> {

    public interface OnCuocHenActionListener {
        void onEditClick(CuocHen item);
        void onDeleteClick(CuocHen item);
    }

    private final OnCuocHenActionListener listener;

    private static final DiffUtil.ItemCallback<CuocHen> DIFF_CALLBACK = new DiffUtil.ItemCallback<CuocHen>() {
        @Override
        public boolean areItemsTheSame(@NonNull CuocHen oldItem, @NonNull CuocHen newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CuocHen oldItem, @NonNull CuocHen newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public CuocHenAdapter(OnCuocHenActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCuocHenBinding binding = ItemCuocHenBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CuocHen item = getItem(position);
        holder.bind(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCuocHenBinding binding;

        public ViewHolder(@NonNull ItemCuocHenBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CuocHen item) {
            binding.tvTieuDe.setText(item.getTieuDe());
            binding.tvThoiGian.setText(item.getThoiGian());

            // Dia diem
            if (item.getDiaDiem() != null && !item.getDiaDiem().trim().isEmpty()) {
                binding.layoutDiaDiem.setVisibility(View.VISIBLE);
                binding.tvDiaDiem.setText(item.getDiaDiem().trim());
            } else {
                binding.layoutDiaDiem.setVisibility(View.GONE);
            }

            // Ghi chu
            if (item.getGhiChu() != null && !item.getGhiChu().trim().isEmpty()) {
                binding.tvGhiChu.setVisibility(View.VISIBLE);
                binding.tvGhiChu.setText(item.getGhiChu().trim());
            } else {
                binding.tvGhiChu.setVisibility(View.GONE);
            }

            // Kiem tra cuoc hen da qua hay chua
            boolean isPast = DateUtils.isPastDateTime(item.getThoiGian());
            if (isPast) {
                binding.tvStatusBadge.setText(R.string.meeting_passed);
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_passed);
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.text_secondary));
                binding.cardCuocHen.setAlpha(0.65f);
            } else {
                binding.tvStatusBadge.setText(R.string.meeting_upcoming);
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_completed));
                binding.cardCuocHen.setAlpha(1.0f);
            }

            // Nut sua
            binding.btnEditMeeting.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(item);
                }
            });

            // Nut xoa
            binding.btnDeleteMeeting.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}
