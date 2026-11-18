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

import java.util.List;

// Adapter một kiểu dòng hiển thị Cuộc hẹn (Khớp chuẩn khung slide 190–191, 194, 220–223)
public class CuocHenAdapter extends RecyclerView.Adapter<CuocHenAdapter.CuocHenViewHolder> {

    // Khai báo hằng số TAG theo mẫu slide 190
    private static final String TAG = "CuocHenAdapter";

    // Khai báo các thuộc tính thành viên tiền tố m theo mẫu slide 190
    private List<CuocHen> mCuocHens;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnCuocHenClickListener mListener;

    // Interface callback để Activity lắng nghe sự kiện
    public interface OnCuocHenClickListener {
        void onEdit(CuocHen ch, int position);
        void onDelete(CuocHen ch, int position);
    }

    // Constructor duy nhất thống nhất nhận listener (Context context, List<CuocHen> datas, OnCuocHenClickListener listener)
    public CuocHenAdapter(Context context, List<CuocHen> datas, OnCuocHenClickListener listener) {
        mContext = context;
        mCuocHens = datas;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // ==============================================================
    // ANIMATION: ĐÚNG 3 NHÓM HÀM THEO MẪU SLIDE 220–223
    // ==============================================================

    // Thêm phần tử vào cuối danh sách (mẫu slide 220)
    public void addItem(CuocHen item) {
        if (mCuocHens != null) {
            mCuocHens.add(item);
            notifyItemInserted(mCuocHens.size() - 1);
        }
    }

    // Thêm phần tử tại vị trí position chỉ định (mẫu slide 220)
    public void addItem(int position, CuocHen item) {
        if (mCuocHens != null && position >= 0 && position <= mCuocHens.size()) {
            mCuocHens.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Xóa phần tử theo vị trí position (mẫu slide 221)
    public void removeItem(int position) {
        if (mCuocHens != null && position >= 0 && position < mCuocHens.size()) {
            mCuocHens.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Xóa phần tử theo đối tượng dùng indexOf (mở rộng an toàn từ mẫu slide 221)
    public void removeItem(CuocHen item) {
        if (mCuocHens != null) {
            int index = mCuocHens.indexOf(item);
            if (index != -1) {
                removeItem(index);
            }
        }
    }

    // Thay thế/đổi phần tử tại vị trí position (mẫu slide 221: remove + add + notifyItemChanged)
    public void replaceItem(int position, CuocHen item) {
        if (mCuocHens != null && position >= 0 && position < mCuocHens.size()) {
            mCuocHens.remove(position);
            mCuocHens.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC PHƯƠNG THỨC CHÍNH CỦA RECYCLERVIEW.ADAPTER (SLIDE 190–191)
    // ==============================================================

    @NonNull
    @Override
    public CuocHenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view từ row_cuoc_hen.xml (mẫu slide 190)
        View itemView = mLayoutInflater.inflate(R.layout.row_cuoc_hen, parent, false);
        return new CuocHenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CuocHenViewHolder holder, int position) {
        // Lấy cuộc hẹn theo vị trí và gắn dữ liệu vào ViewHolder (mẫu slide 190)
        CuocHen ch = mCuocHens.get(position);

        // Gắn dữ liệu vào ViewHolder
        holder.tvTitle.setText(ch.getTieuDe());

        // Thời gian cuộc hẹn (kiểm tra null hoặc rỗng sau trim coi là chưa có)
        if (ch.getThoiGian() != null && !ch.getThoiGian().trim().isEmpty()) {
            holder.tvThoiGian.setText(ch.getThoiGian().trim());
        } else {
            holder.tvThoiGian.setText(mContext.getString(R.string.not_set));
        }

        // Địa điểm (ẩn nếu rỗng hoặc null sau khi trim)
        if (ch.getDiaDiem() != null && !ch.getDiaDiem().trim().isEmpty()) {
            holder.layoutDiaDiem.setVisibility(View.VISIBLE);
            holder.tvDiaDiem.setText(ch.getDiaDiem().trim());
        } else {
            holder.layoutDiaDiem.setVisibility(View.GONE);
        }

        // Ghi chú (ẩn nếu rỗng hoặc null sau khi trim)
        if (ch.getGhiChu() != null && !ch.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(ch.getGhiChu().trim());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Kiểm tra cuộc hẹn đã qua hay chưa bằng hàm dùng chung laDaQua trong DBHelper
        boolean daQua = DBHelper.laDaQua(ch.getThoiGian());
        if (daQua) {
            holder.tvStatusBadge.setText(mContext.getString(R.string.meeting_passed));
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_passed);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_expired));
        } else {
            holder.tvStatusBadge.setText(mContext.getString(R.string.meeting_upcoming));
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_completed));
        }
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phần tử (mẫu slide 191)
        return mCuocHens != null ? mCuocHens.size() : 0;
    }

    // ==============================================================
    // LỚP VIEWHOLDER NỘI BỘ (MẪU SLIDE 191)
    // ==============================================================

    class CuocHenViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvStatusBadge;
        private TextView tvThoiGian;
        private LinearLayout layoutDiaDiem;
        private TextView tvDiaDiem;
        private TextView tvGhiChu;
        private ImageButton btnEditMeeting;
        private ImageButton btnDeleteMeeting;

        public CuocHenViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View bằng findViewById ép kiểu theo mẫu slide 191
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvStatusBadge = (TextView) itemView.findViewById(R.id.tv_status_badge);
            tvThoiGian = (TextView) itemView.findViewById(R.id.tv_thoi_gian);
            layoutDiaDiem = (LinearLayout) itemView.findViewById(R.id.layout_dia_diem);
            tvDiaDiem = (TextView) itemView.findViewById(R.id.tv_dia_diem);
            tvGhiChu = (TextView) itemView.findViewById(R.id.tv_ghi_chu);
            btnEditMeeting = (ImageButton) itemView.findViewById(R.id.btn_edit_meeting);
            btnDeleteMeeting = (ImageButton) itemView.findViewById(R.id.btn_delete_meeting);

            // Thiết lập sự kiện chọn dòng (mẫu slide 194)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onEdit(mCuocHens.get(position), position);
                    }
                }
            });

            // Sự kiện nhấn giữ (onLongClick) (mẫu slide 222):
            // KHÁC SLIDE: Trả về true để tiêu thụ sự kiện, ngăn sự kiện click thường chạy tiếp theo
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return false;
                    if (mListener != null) {
                        mListener.onDelete(mCuocHens.get(position), position);
                        return true;
                    }
                    return false;
                }
            });

            btnEditMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onEdit(mCuocHens.get(position), position);
                    }
                }
            });

            btnDeleteMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onDelete(mCuocHens.get(position), position);
                    }
                }
            });
        }
    }
}
