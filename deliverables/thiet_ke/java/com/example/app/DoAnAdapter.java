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

import java.util.List;

// Adapter hiển thị danh sách Đồ án
public class DoAnAdapter extends RecyclerView.Adapter<DoAnAdapter.DoAnViewHolder> {

    private static final String TAG = "DoAnAdapter";

    private List<DoAn> mDoAns;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnDoAnClickListener mListener;

    // Interface callback để Activity lắng nghe sự kiện
    public interface OnDoAnClickListener {
        void onItemClick(DoAn doAn, int position);
        void onEditClick(DoAn doAn, int position);
        void onDeleteClick(DoAn doAn, int position);
    }

    public DoAnAdapter(Context context, List<DoAn> datas, OnDoAnClickListener listener) {
        mContext = context;
        mDoAns = datas;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // ==============================================================
    // CÁC HÀM XỬ LÝ ANIMATION RECYCLERVIEW
    // ==============================================================

    // Thêm phần tử vào cuối danh sách
    public void addItem(DoAn item) {
        if (mDoAns != null) {
            mDoAns.add(item);
            notifyItemInserted(mDoAns.size() - 1);
        }
    }

    // Thêm phần tử tại vị trí chỉ định
    public void addItem(int position, DoAn item) {
        if (mDoAns != null && position >= 0 && position <= mDoAns.size()) {
            mDoAns.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Xóa phần tử theo vị trí
    public void removeItem(int position) {
        if (mDoAns != null && position >= 0 && position < mDoAns.size()) {
            mDoAns.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Xóa phần tử theo đối tượng dùng indexOf
    public void removeItem(DoAn item) {
        if (mDoAns != null) {
            int index = mDoAns.indexOf(item);
            if (index != -1) {
                removeItem(index);
            }
        }
    }

    // Thay thế phần tử tại vị trí chỉ định
    public void replaceItem(int position, DoAn item) {
        if (mDoAns != null && position >= 0 && position < mDoAns.size()) {
            DoAn oldItem = mDoAns.get(position);
            // Đảm bảo đối tượng mới có soNhiemVuXong và tongNhiemVu đúng (sao chép số đếm cũ nếu chưa nạp, không để tiến độ về 0/0)
            if (item != null && item.getTongNhiemVu() == 0 && item.getSoNhiemVuXong() == 0 && oldItem != null) {
                item.setTongNhiemVu(oldItem.getTongNhiemVu());
                item.setSoNhiemVuXong(oldItem.getSoNhiemVuXong());
            }
            mDoAns.remove(position);
            mDoAns.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC PHƯƠNG THỨC CHÍNH CỦA RECYCLERVIEW.ADAPTER
    // ==============================================================

    @NonNull
    @Override
    public DoAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_do_an, parent, false);
        return new DoAnViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoAnViewHolder holder, int position) {
        DoAn doAn = mDoAns.get(position);

        holder.tvTitle.setText(doAn.getTen());

        if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
            holder.tvMoTa.setVisibility(View.VISIBLE);
            holder.tvMoTa.setText(doAn.getMoTa().trim());
        } else {
            holder.tvMoTa.setVisibility(View.GONE);
        }

        if (doAn.getGiangVien() != null && !doAn.getGiangVien().trim().isEmpty()) {
            holder.tvGiangVien.setText(mContext.getString(R.string.gvhd_prefix) + doAn.getGiangVien().trim());
        } else {
            holder.tvGiangVien.setText(mContext.getString(R.string.gvhd_prefix) + mContext.getString(R.string.not_available));
        }

        boolean hasNgayBatDau = doAn.getNgayBatDau() != null && !doAn.getNgayBatDau().trim().isEmpty();
        boolean hasHanNop = doAn.getHanNop() != null && !doAn.getHanNop().trim().isEmpty();

        String tg;
        if (hasNgayBatDau) {
            String nbd = doAn.getNgayBatDau().trim();
            String hn = hasHanNop ? doAn.getHanNop().trim() : mContext.getString(R.string.not_available);
            tg = nbd + " " + mContext.getString(R.string.date_to) + " " + hn;
        } else {
            String hn = hasHanNop ? doAn.getHanNop().trim() : mContext.getString(R.string.not_set);
            tg = mContext.getString(R.string.deadline_prefix) + hn;
        }
        holder.tvThoiGian.setText(tg);

        // Hiển thị tiến độ hoàn thành từ thuộc tính model được nạp sẵn qua LEFT JOIN
        int tong = doAn.getTongNhiemVu();
        int xong = doAn.getSoNhiemVuXong();
        // Làm tròn giống DBHelper.getTienDoDoAn() để danh sách và màn hình chi tiết hiện cùng một con số
        int phanTram = (tong == 0) ? 0 : (int) Math.round(xong * 100.0 / tong);
        holder.tvTienDo.setText(xong + "/" + tong + " (" + phanTram + "%)");
        holder.progressBar.setProgress(phanTram);
    }

    @Override
    public int getItemCount() {
        return mDoAns != null ? mDoAns.size() : 0;
    }

    // ==============================================================
    // LỚP VIEWHOLDER NỘI BỘ
    // ==============================================================

    class DoAnViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvMoTa;
        private TextView tvGiangVien;
        private TextView tvThoiGian;
        private TextView tvTienDo;
        private ProgressBar progressBar;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public DoAnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvMoTa = (TextView) itemView.findViewById(R.id.tv_mo_ta);
            tvGiangVien = (TextView) itemView.findViewById(R.id.tv_giang_vien);
            tvThoiGian = (TextView) itemView.findViewById(R.id.tv_thoi_gian);
            tvTienDo = (TextView) itemView.findViewById(R.id.tv_tien_do);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            btnEdit = (ImageButton) itemView.findViewById(R.id.btn_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete);

            // Thiết lập sự kiện chọn dòng
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onItemClick(mDoAns.get(position), position);
                    }
                }
            });

            // Sự kiện nhấn giữ (onLongClick): Trả về true để tiêu thụ sự kiện, ngăn click nhầm sau khi nhấn giữ
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return false;
                    if (mListener != null) {
                        mListener.onDeleteClick(mDoAns.get(position), position);
                        return true;
                    }
                    return false;
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onEditClick(mDoAns.get(position), position);
                    }
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onDeleteClick(mDoAns.get(position), position);
                    }
                }
            });
        }
    }
}
