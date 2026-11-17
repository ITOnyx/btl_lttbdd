package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Adapter hiển thị danh sách các cuộc hẹn trao đổi với giảng viên
public class CuocHenAdapter extends RecyclerView.Adapter<CuocHenAdapter.ViewHolder> {

    // Giao diện xử lý sự kiện sửa và xóa cuộc hẹn
    public interface OnCuocHenClickListener {
        void onEdit(CuocHen ch);
        void onDelete(CuocHen ch);
    }

    private final Context context;
    private final ArrayList<CuocHen> list;
    private final OnCuocHenClickListener listener;

    public CuocHenAdapter(Context context, ArrayList<CuocHen> list, OnCuocHenClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cuoc_hen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CuocHen ch = list.get(position);

        // Gán tiêu đề cuộc hẹn
        holder.tvTieuDe.setText(ch.getTieuDe());

        // Gán thời gian hẹn
        holder.tvThoiGian.setText(ch.getThoiGian() != null ? ch.getThoiGian() : "Chưa đặt");

        // Gán địa điểm gặp
        if (ch.getDiaDiem() != null && !ch.getDiaDiem().trim().isEmpty()) {
            holder.layoutDiaDiem.setVisibility(View.VISIBLE);
            holder.tvDiaDiem.setText(ch.getDiaDiem());
        } else {
            holder.layoutDiaDiem.setVisibility(View.GONE);
        }

        // Gán ghi chú chi tiết
        if (ch.getGhiChu() != null && !ch.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(ch.getGhiChu());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Xác định nhãn "Đã qua" hoặc "Sắp tới" bằng cách so sánh chuỗi thời gian yyyy-MM-dd HH:mm với hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        boolean daQua = (ch.getThoiGian() != null && ch.getThoiGian().compareTo(currentDateTime) < 0);
        if (daQua) {
            holder.tvStatusBadge.setText("Đã qua");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_passed);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_expired));
        } else {
            holder.tvStatusBadge.setText("Sắp tới");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_completed));
        }

        // Bấm nút sửa cuộc hẹn
        holder.btnEditMeeting.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(ch);
            }
        });

        // Bấm nút xóa cuộc hẹn
        holder.btnDeleteMeeting.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(ch);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // ViewHolder ánh xạ các View của một dòng cuộc hẹn
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTieuDe;
        TextView tvStatusBadge;
        TextView tvThoiGian;
        LinearLayout layoutDiaDiem;
        TextView tvDiaDiem;
        TextView tvGhiChu;
        ImageButton btnEditMeeting;
        ImageButton btnDeleteMeeting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            layoutDiaDiem = itemView.findViewById(R.id.layoutDiaDiem);
            tvDiaDiem = itemView.findViewById(R.id.tvDiaDiem);
            tvGhiChu = itemView.findViewById(R.id.tvGhiChu);
            btnEditMeeting = itemView.findViewById(R.id.btnEditMeeting);
            btnDeleteMeeting = itemView.findViewById(R.id.btnDeleteMeeting);
        }
    }
}
