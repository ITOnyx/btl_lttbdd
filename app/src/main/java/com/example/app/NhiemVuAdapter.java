package com.example.app;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter hiển thị danh sách nhiệm vụ của đồ án lên RecyclerView
public class NhiemVuAdapter extends RecyclerView.Adapter<NhiemVuAdapter.ViewHolder> {

    // Giao diện xử lý sự kiện hoàn thành, sửa và xóa nhiệm vụ
    public interface OnNhiemVuClickListener {
        void onToggleStatus(NhiemVu nv, boolean isCompleted);
        void onEdit(NhiemVu nv);
        void onDelete(NhiemVu nv);
    }

    private final Context context;
    private final ArrayList<NhiemVu> list;
    private final OnNhiemVuClickListener listener;

    public NhiemVuAdapter(Context context, ArrayList<NhiemVu> list, OnNhiemVuClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nhiem_vu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NhiemVu nv = list.get(position);

        // Gán tên nhiệm vụ
        holder.tvTenNhiemVu.setText(nv.getTen());

        // Gán ghi chú chi tiết
        if (nv.getGhiChu() != null && !nv.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(nv.getGhiChu());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Gán hạn hoàn thành
        String han = nv.getHanHoanThanh() != null ? nv.getHanHoanThanh() : "Chưa đặt";
        holder.tvHanHoanThanh.setText("Hạn: " + han);

        // Hiển thị trạng thái đã hoàn thành lên CheckBox
        boolean daXong = (nv.getDaHoanThanh() == 1);
        holder.cbHoanThanh.setChecked(daXong);

        // Hiệu ứng gạch ngang chữ nếu nhiệm vụ đã hoàn thành
        if (daXong) {
            holder.tvTenNhiemVu.setPaintFlags(holder.tvTenNhiemVu.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTenNhiemVu.setPaintFlags(holder.tvTenNhiemVu.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Bấm vào CheckBox để cập nhật trạng thái hoàn thành vào SQLite
        holder.cbHoanThanh.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleStatus(nv, holder.cbHoanThanh.isChecked());
            }
        });

        // Bấm nút sửa nhiệm vụ
        holder.btnEditTask.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(nv);
            }
        });

        // Bấm nút xóa nhiệm vụ
        holder.btnDeleteTask.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(nv);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // ViewHolder ánh xạ các View của một dòng nhiệm vụ
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbHoanThanh;
        TextView tvTenNhiemVu;
        TextView tvGhiChu;
        TextView tvHanHoanThanh;
        ImageButton btnEditTask;
        ImageButton btnDeleteTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbHoanThanh = itemView.findViewById(R.id.cbHoanThanh);
            tvTenNhiemVu = itemView.findViewById(R.id.tvTenNhiemVu);
            tvGhiChu = itemView.findViewById(R.id.tvGhiChu);
            tvHanHoanThanh = itemView.findViewById(R.id.tvHanHoanThanh);
            btnEditTask = itemView.findViewById(R.id.btnEditTask);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
