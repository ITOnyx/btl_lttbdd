package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter hiển thị danh sách Đồ án theo chuẩn 5 bước RecyclerView (slide 184–194)
public class DoAnAdapter extends RecyclerView.Adapter<DoAnAdapter.DoAnViewHolder> {

    // Giao diện callback để lắng nghe sự kiện click/sửa/xóa từ Activity
    public interface OnDoAnClickListener {
        void onItemClick(DoAn doAn);
        void onEditClick(DoAn doAn);
        void onDeleteClick(DoAn doAn);
    }

    // Các thuộc tính thành viên với tiền tố m như bài giảng (slide 184)
    private Context mContext;
    private List<DoAn> mDoAns;
    private LayoutInflater mLayoutInflater;
    private DBHelper mDbHelper;
    private OnDoAnClickListener mListener;

    // Bước 4: Constructor cơ bản (Context, List) theo bài giảng
    public DoAnAdapter(Context context, List<DoAn> doAns) {
        this(context, doAns, null);
    }

    // Constructor mở rộng nhận thêm listener xử lý sự kiện
    public DoAnAdapter(Context context, List<DoAn> doAns, OnDoAnClickListener listener) {
        this.mContext = context;
        this.mDoAns = doAns;
        this.mListener = listener;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDbHelper = new DBHelper(context);
    }

    // Setter listener nếu cần đăng ký sau
    public void setOnDoAnClickListener(OnDoAnClickListener listener) {
        this.mListener = listener;
    }

    // Bước 4: onCreateViewHolder - Nạp layout row_do_an.xml với attachToRoot = false
    @NonNull
    @Override
    public DoAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_do_an, parent, false);
        return new DoAnViewHolder(itemView);
    }

    // Bước 4: onBindViewHolder - Lấy item theo position và gắn dữ liệu vào View
    @Override
    public void onBindViewHolder(@NonNull DoAnViewHolder holder, int position) {
        DoAn doAn = mDoAns.get(position);

        // Gắn tiêu đề đồ án
        holder.tvTitle.setText(doAn.getTen());

        // Gắn mô tả (nếu rỗng thì ẩn đi)
        if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
            holder.tvMoTa.setVisibility(View.VISIBLE);
            holder.tvMoTa.setText(doAn.getMoTa());
        } else {
            holder.tvMoTa.setVisibility(View.GONE);
        }

        // Gắn giảng viên hướng dẫn
        if (doAn.getGiangVien() != null && !doAn.getGiangVien().trim().isEmpty()) {
            holder.tvGiangVien.setText("GVHD: " + doAn.getGiangVien());
        } else {
            holder.tvGiangVien.setText("GVHD: Chưa có");
        }

        // Gắn khoảng thời gian thực hiện
        String tg = "Hạn: " + (doAn.getHanNop() != null ? doAn.getHanNop() : "Chưa đặt");
        if (doAn.getNgayBatDau() != null && !doAn.getNgayBatDau().isEmpty()) {
            tg = doAn.getNgayBatDau() + "  đến  " + (doAn.getHanNop() != null ? doAn.getHanNop() : "---");
        }
        holder.tvThoiGian.setText(tg);

        // Lấy tiến độ từ SQLite (dạng "x/y (z%)")
        String tienDoStr = mDbHelper.getTienDoDoAn(doAn.getId());
        holder.tvTienDo.setText(tienDoStr);

        // Cập nhật thanh ProgressBar
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
    }

    // Bước 4: getItemCount - Trả về số lượng phần tử trong danh sách mDoAns
    @Override
    public int getItemCount() {
        return mDoAns != null ? mDoAns.size() : 0;
    }

    // Bước 4: ViewHolder là inner class, ánh xạ findViewById trong constructor
    // Bước 6: Bắt sự kiện chọn item trong constructor của ViewHolder (slide 194)
    public class DoAnViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvMoTa;
        TextView tvGiangVien;
        TextView tvThoiGian;
        TextView tvTienDo;
        ProgressBar progressBar;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public DoAnViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View bằng findViewById
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvMoTa = itemView.findViewById(R.id.tv_mo_ta);
            tvGiangVien = itemView.findViewById(R.id.tv_giang_vien);
            tvThoiGian = itemView.findViewById(R.id.tv_thoi_gian);
            tvTienDo = itemView.findViewById(R.id.tv_tien_do);
            progressBar = itemView.findViewById(R.id.progress_bar);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // Bắt sự kiện click chọn item trong constructor ViewHolder (slide 194)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Kiểm tra getAdapterPosition() hợp lệ tránh crash khi item đang bị xóa
                    if (position != RecyclerView.NO_POSITION && mDoAns != null && position < mDoAns.size()) {
                        DoAn doAn = mDoAns.get(position);
                        if (mListener != null) {
                            mListener.onItemClick(doAn);
                        } else {
                            // Mở màn hình chi tiết đồ án bằng Intent (slide 194)
                            Intent intent = new Intent(mContext, DoAnDetailActivity.class);
                            intent.putExtra("do_an_id", doAn.getId());
                            mContext.startActivity(intent);
                        }
                    }
                }
            });

            // Bắt sự kiện nhấn giữ (long click) trên dòng đồ án
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mDoAns != null && position < mDoAns.size()) {
                        if (mListener != null) {
                            mListener.onDeleteClick(mDoAns.get(position));
                            return true;
                        }
                    }
                    return false;
                }
            });

            // Bắt sự kiện bấm nút sửa
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mDoAns != null && position < mDoAns.size()) {
                        if (mListener != null) {
                            mListener.onEditClick(mDoAns.get(position));
                        }
                    }
                }
            });

            // Bắt sự kiện bấm nút xóa
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mDoAns != null && position < mDoAns.size()) {
                        if (mListener != null) {
                            mListener.onDeleteClick(mDoAns.get(position));
                        }
                    }
                }
            });
        }
    }
}
