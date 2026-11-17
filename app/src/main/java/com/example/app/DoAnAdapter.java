package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Adapter hiển thị danh sách đồ án lên RecyclerView
public class DoAnAdapter extends RecyclerView.Adapter<DoAnAdapter.ViewHolder> {

    // Giao diện xử lý các sự kiện click trên từng dòng đồ án
    public interface OnDoAnClickListener {
        void onItemClick(DoAn doAn);
        void onEditClick(DoAn doAn);
        void onDeleteClick(DoAn doAn);
    }

    private final Context context;
    private final ArrayList<DoAn> list;
    private final DBHelper dbHelper;
    private final OnDoAnClickListener listener;

    // Khởi tạo Adapter với context, danh sách dữ liệu và listener
    public DoAnAdapter(Context context, ArrayList<DoAn> list, OnDoAnClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.dbHelper = new DBHelper(context);
    }

    // Tạo ViewHolder bằng cách nạp giao diện từ tệp item_do_an.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_do_an, parent, false);
        return new ViewHolder(view);
    }

    // Đổ dữ liệu từ đối tượng DoAn vào các View trên từng dòng
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DoAn doAn = list.get(position);

        // Gán tên đồ án
        holder.tvTen.setText(doAn.getTen());

        // Gán mô tả đồ án (nếu không có thì ẩn đi)
        if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
            holder.tvMoTa.setVisibility(View.VISIBLE);
            holder.tvMoTa.setText(doAn.getMoTa());
        } else {
            holder.tvMoTa.setVisibility(View.GONE);
        }

        // Gán giảng viên hướng dẫn
        if (doAn.getGiangVien() != null && !doAn.getGiangVien().trim().isEmpty()) {
            holder.tvGiangVien.setText("GVHD: " + doAn.getGiangVien());
        } else {
            holder.tvGiangVien.setText("GVHD: Chưa có");
        }

        // Gán thời gian thực hiện đồ án
        String tg = "Hạn: " + (doAn.getHanNop() != null ? doAn.getHanNop() : "Chưa đặt");
        if (doAn.getNgayBatDau() != null && !doAn.getNgayBatDau().isEmpty()) {
            tg = doAn.getNgayBatDau() + "  đến  " + (doAn.getHanNop() != null ? doAn.getHanNop() : "---");
        }
        holder.tvThoiGian.setText(tg);

        // Lấy và hiển thị tiến độ từ DBHelper (dạng "x/y (z%)")
        String tienDoStr = dbHelper.getTienDoDoAn(doAn.getId());
        holder.tvTienDo.setText(tienDoStr);

        // Cập nhật thanh ProgressBar theo phần trăm
        int percent = 0;
        try {
            if (tienDoStr.contains("(") && tienDoStr.contains("%")) {
                int start = tienDoStr.indexOf("(") + 1;
                int end = tienDoStr.indexOf("%");
                percent = Integer.parseInt(tienDoStr.substring(start, end).trim());
            }
        } catch (Exception ignored) {
            percent = 0;
        }
        holder.progressBar.setProgress(percent);

        // Bấm vào thẻ để xem chi tiết đồ án
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(doAn);
            }
        });

        // Nhấn giữ thẻ để xóa đồ án
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(doAn);
            }
            return true;
        });

        // Bấm nút sửa đồ án
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(doAn);
            }
        });

        // Bấm nút xóa đồ án
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(doAn);
            }
        });
    }

    // Trả về tổng số phần tử trong danh sách
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // Lớp ViewHolder giữ các thành phần giao diện của một dòng đồ án
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen;
        TextView tvMoTa;
        TextView tvGiangVien;
        TextView tvThoiGian;
        TextView tvTienDo;
        ProgressBar progressBar;
        ImageButton btnEdit;
        ImageButton btnDelete;

        // Ánh xạ các View thông qua findViewById
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvGiangVien = itemView.findViewById(R.id.tvGiangVien);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            tvTienDo = itemView.findViewById(R.id.tvTienDo);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
