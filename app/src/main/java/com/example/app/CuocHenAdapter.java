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
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Adapter hiển thị danh sách Cuộc hẹn theo chuẩn bài giảng (slide 184–223)
public class CuocHenAdapter extends RecyclerView.Adapter<CuocHenAdapter.CuocHenViewHolder> {

    // Giao diện callback nhận sự kiện sửa và xóa cuộc hẹn kèm vị trí adapter position
    public interface OnCuocHenClickListener {
        void onEdit(CuocHen ch, int position);
        void onDelete(CuocHen ch, int position);
    }

    // Các thuộc tính thành viên với tiền tố m như bài giảng (slide 184)
    private Context mContext;
    private List<CuocHen> mCuocHens;
    private LayoutInflater mLayoutInflater;
    private OnCuocHenClickListener mListener;

    // Bước 4: Constructor cơ bản (Context, List) theo bài giảng
    public CuocHenAdapter(Context context, List<CuocHen> cuocHens) {
        this(context, cuocHens, null);
    }

    // Constructor mở rộng nhận thêm listener xử lý sự kiện
    public CuocHenAdapter(Context context, List<CuocHen> cuocHens, OnCuocHenClickListener listener) {
        this.mContext = context;
        this.mCuocHens = cuocHens;
        this.mListener = listener;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnCuocHenClickListener(OnCuocHenClickListener listener) {
        this.mListener = listener;
    }

    // ==============================================================
    // 3 HÀM ANIMATION CỐT LÕI THEO BÀI GIẢNG (slide 211–223)
    // ==============================================================

    // 1. Thêm phần tử tại vị trí chỉ định có hiệu ứng chèn (slide 213–215)
    public void addItem(int position, CuocHen item) {
        if (mCuocHens != null && position >= 0 && position <= mCuocHens.size()) {
            mCuocHens.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Thêm phần tử vào cuối danh sách có hiệu ứng chèn (slide 213–215)
    public void addItem(CuocHen item) {
        if (mCuocHens != null) {
            addItem(mCuocHens.size(), item);
        }
    }

    // 2. Xóa phần tử tại vị trí chỉ định có hiệu ứng xóa (slide 216–218)
    public void removeItem(int position) {
        if (mCuocHens != null && position >= 0 && position < mCuocHens.size()) {
            mCuocHens.remove(position);
            notifyItemRemoved(position);
        }
    }

    // 3. Thay thế phần tử tại vị trí chỉ định có hiệu ứng đổi (slide 219–221)
    // Đúng mẫu bài giảng: remove + add + notifyItemChanged
    public void replaceItem(int position, CuocHen item) {
        if (mCuocHens != null && position >= 0 && position < mCuocHens.size()) {
            mCuocHens.remove(position);
            mCuocHens.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC HÀM OVERRIDE RECYCLERVIEW.ADAPTER CƠ BẢN
    // ==============================================================

    // Bước 4: onCreateViewHolder - Nạp layout row_cuoc_hen.xml với attachToRoot = false
    @NonNull
    @Override
    public CuocHenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_cuoc_hen, parent, false);
        return new CuocHenViewHolder(itemView);
    }

    // Bước 4: onBindViewHolder - Lấy item theo position và gắn dữ liệu vào View
    @Override
    public void onBindViewHolder(@NonNull CuocHenViewHolder holder, int position) {
        CuocHen ch = mCuocHens.get(position);

        // Gắn tiêu đề cuộc hẹn
        holder.tvTitle.setText(ch.getTieuDe());

        // Gắn thời gian hẹn
        holder.tvThoiGian.setText(ch.getThoiGian() != null ? ch.getThoiGian() : "Chưa đặt");

        // Gắn địa điểm gặp
        if (ch.getDiaDiem() != null && !ch.getDiaDiem().trim().isEmpty()) {
            holder.layoutDiaDiem.setVisibility(View.VISIBLE);
            holder.tvDiaDiem.setText(ch.getDiaDiem());
        } else {
            holder.layoutDiaDiem.setVisibility(View.GONE);
        }

        // Gắn ghi chú chi tiết
        if (ch.getGhiChu() != null && !ch.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(ch.getGhiChu());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Xác định nhãn "Đã qua" hoặc "Sắp tới" dựa trên thời gian so với hiện tại
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        boolean daQua = (ch.getThoiGian() != null && ch.getThoiGian().compareTo(currentDateTime) < 0);
        if (daQua) {
            holder.tvStatusBadge.setText("Đã qua");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_passed);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_expired));
        } else {
            holder.tvStatusBadge.setText("Sắp tới");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_completed));
        }
    }

    // Bước 4: getItemCount - Trả về số lượng phần tử trong danh sách mCuocHens
    @Override
    public int getItemCount() {
        return mCuocHens != null ? mCuocHens.size() : 0;
    }

    // Bước 4: ViewHolder là inner class, ánh xạ findViewById trong constructor
    // Bước 6: Bắt sự kiện chọn item trong constructor của ViewHolder (slide 194)
    public class CuocHenViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvStatusBadge;
        TextView tvThoiGian;
        LinearLayout layoutDiaDiem;
        TextView tvDiaDiem;
        TextView tvGhiChu;
        ImageButton btnEditMeeting;
        ImageButton btnDeleteMeeting;

        public CuocHenViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View bằng findViewById
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvThoiGian = itemView.findViewById(R.id.tv_thoi_gian);
            layoutDiaDiem = itemView.findViewById(R.id.layout_dia_diem);
            tvDiaDiem = itemView.findViewById(R.id.tv_dia_diem);
            tvGhiChu = itemView.findViewById(R.id.tv_ghi_chu);
            btnEditMeeting = itemView.findViewById(R.id.btn_edit_meeting);
            btnDeleteMeeting = itemView.findViewById(R.id.btn_delete_meeting);

            // Bắt sự kiện click vào cả dòng để sửa nhanh
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mCuocHens != null && position < mCuocHens.size()) {
                        if (mListener != null) {
                            mListener.onEdit(mCuocHens.get(position), position);
                        }
                    }
                }
            });

            // Bắt sự kiện nút sửa cuộc hẹn
            btnEditMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mCuocHens != null && position < mCuocHens.size()) {
                        if (mListener != null) {
                            mListener.onEdit(mCuocHens.get(position), position);
                        }
                    }
                }
            });

            // Bắt sự kiện nút xóa cuộc hẹn
            btnDeleteMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mCuocHens != null && position < mCuocHens.size()) {
                        if (mListener != null) {
                            mListener.onDelete(mCuocHens.get(position), position);
                        }
                    }
                }
            });
        }
    }
}
