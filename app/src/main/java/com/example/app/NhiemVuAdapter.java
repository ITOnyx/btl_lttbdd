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

import java.util.List;

// Adapter hiển thị danh sách Nhiệm vụ theo chuẩn bài giảng (slide 184–223)
public class NhiemVuAdapter extends RecyclerView.Adapter<NhiemVuAdapter.NhiemVuViewHolder> {

    // Giao diện callback nhận sự kiện đổi trạng thái, sửa và xóa kèm vị trí adapter position
    public interface OnNhiemVuClickListener {
        void onToggleStatus(NhiemVu nv, boolean isCompleted, int position);
        void onEdit(NhiemVu nv, int position);
        void onDelete(NhiemVu nv, int position);
    }

    // Các thuộc tính thành viên với tiền tố m như bài giảng (slide 184)
    private Context mContext;
    private List<NhiemVu> mNhiemVus;
    private LayoutInflater mLayoutInflater;
    private OnNhiemVuClickListener mListener;

    // Bước 4: Constructor cơ bản (Context, List) theo bài giảng
    public NhiemVuAdapter(Context context, List<NhiemVu> nhiemVus) {
        this(context, nhiemVus, null);
    }

    // Constructor mở rộng nhận thêm listener xử lý sự kiện
    public NhiemVuAdapter(Context context, List<NhiemVu> nhiemVus, OnNhiemVuClickListener listener) {
        this.mContext = context;
        this.mNhiemVus = nhiemVus;
        this.mListener = listener;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnNhiemVuClickListener(OnNhiemVuClickListener listener) {
        this.mListener = listener;
    }

    // ==============================================================
    // 3 HÀM ANIMATION CỐT LÕI THEO BÀI GIẢNG (slide 211–223)
    // ==============================================================

    // 1. Thêm phần tử tại vị trí chỉ định có hiệu ứng chèn (slide 213–215)
    public void addItem(int position, NhiemVu item) {
        if (mNhiemVus != null && position >= 0 && position <= mNhiemVus.size()) {
            mNhiemVus.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Thêm phần tử vào cuối danh sách có hiệu ứng chèn (slide 213–215)
    public void addItem(NhiemVu item) {
        if (mNhiemVus != null) {
            addItem(mNhiemVus.size(), item);
        }
    }

    // 2. Xóa phần tử tại vị trí chỉ định có hiệu ứng xóa (slide 216–218)
    public void removeItem(int position) {
        if (mNhiemVus != null && position >= 0 && position < mNhiemVus.size()) {
            mNhiemVus.remove(position);
            notifyItemRemoved(position);
        }
    }

    // 3. Thay thế phần tử tại vị trí chỉ định có hiệu ứng đổi (slide 219–221)
    // Đúng mẫu bài giảng: remove + add + notifyItemChanged
    public void replaceItem(int position, NhiemVu item) {
        if (mNhiemVus != null && position >= 0 && position < mNhiemVus.size()) {
            mNhiemVus.remove(position);
            mNhiemVus.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC HÀM OVERRIDE RECYCLERVIEW.ADAPTER CƠ BẢN
    // ==============================================================

    // Bước 4: onCreateViewHolder - Nạp layout row_nhiem_vu.xml với attachToRoot = false
    @NonNull
    @Override
    public NhiemVuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_nhiem_vu, parent, false);
        return new NhiemVuViewHolder(itemView);
    }

    // Bước 4: onBindViewHolder - Lấy item theo position và gắn dữ liệu vào View
    @Override
    public void onBindViewHolder(@NonNull NhiemVuViewHolder holder, int position) {
        NhiemVu nv = mNhiemVus.get(position);

        // Gắn tên nhiệm vụ
        holder.tvTitle.setText(nv.getTen());

        // Gắn ghi chú chi tiết
        if (nv.getGhiChu() != null && !nv.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(nv.getGhiChu());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Gắn hạn hoàn thành
        String han = nv.getHanHoanThanh() != null ? nv.getHanHoanThanh() : "Chưa đặt";
        holder.tvHanHoanThanh.setText("Hạn: " + han);

        // Hiển thị trạng thái hoàn thành trên CheckBox
        boolean daXong = (nv.getDaHoanThanh() == 1);
        holder.cbHoanThanh.setChecked(daXong);

        // Hiệu ứng gạch ngang chữ nếu nhiệm vụ đã hoàn thành
        if (daXong) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    // Bước 4: getItemCount - Trả về số lượng phần tử trong danh sách mNhiemVus
    @Override
    public int getItemCount() {
        return mNhiemVus != null ? mNhiemVus.size() : 0;
    }

    // Bước 4: ViewHolder là inner class, ánh xạ findViewById trong constructor
    // Bước 6: Bắt sự kiện chọn item trong constructor của ViewHolder (slide 194)
    public class NhiemVuViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbHoanThanh;
        TextView tvTitle;
        TextView tvGhiChu;
        TextView tvHanHoanThanh;
        ImageButton btnEditTask;
        ImageButton btnDeleteTask;

        public NhiemVuViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View bằng findViewById
            cbHoanThanh = itemView.findViewById(R.id.cb_hoan_thanh);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvGhiChu = itemView.findViewById(R.id.tv_ghi_chu);
            tvHanHoanThanh = itemView.findViewById(R.id.tv_han_hoan_thanh);
            btnEditTask = itemView.findViewById(R.id.btn_edit_task);
            btnDeleteTask = itemView.findViewById(R.id.btn_delete_task);

            // Bắt sự kiện bấm vào cả dòng item để sửa nhanh
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mNhiemVus != null && position < mNhiemVus.size()) {
                        if (mListener != null) {
                            mListener.onEdit(mNhiemVus.get(position), position);
                        }
                    }
                }
            });

            // Bắt sự kiện click CheckBox để cập nhật trạng thái (slide 194)
            cbHoanThanh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mNhiemVus != null && position < mNhiemVus.size()) {
                        if (mListener != null) {
                            mListener.onToggleStatus(mNhiemVus.get(position), cbHoanThanh.isChecked(), position);
                        }
                    }
                }
            });

            // Bắt sự kiện nút sửa nhiệm vụ
            btnEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mNhiemVus != null && position < mNhiemVus.size()) {
                        if (mListener != null) {
                            mListener.onEdit(mNhiemVus.get(position), position);
                        }
                    }
                }
            });

            // Bắt sự kiện nút xóa nhiệm vụ
            btnDeleteTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mNhiemVus != null && position < mNhiemVus.size()) {
                        if (mListener != null) {
                            mListener.onDelete(mNhiemVus.get(position), position);
                        }
                    }
                }
            });
        }
    }
}
