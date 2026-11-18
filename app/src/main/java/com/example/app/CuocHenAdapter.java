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

// Adapter hiển thị danh sách Cuộc hẹn
public class CuocHenAdapter extends RecyclerView.Adapter<CuocHenAdapter.CuocHenViewHolder> {

    private static final String TAG = "CuocHenAdapter";

    private List<CuocHen> mCuocHens;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnCuocHenClickListener mListener;

    // Interface callback để Activity lắng nghe sự kiện
    public interface OnCuocHenClickListener {
        void onEdit(CuocHen ch, int position);
        void onDelete(CuocHen ch, int position);
    }

    public CuocHenAdapter(Context context, List<CuocHen> datas, OnCuocHenClickListener listener) {
        mContext = context;
        mCuocHens = datas;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // ==============================================================
    // CÁC HÀM XỬ LÝ ANIMATION RECYCLERVIEW
    // ==============================================================

    // Thêm phần tử vào cuối danh sách
    public void addItem(CuocHen item) {
        if (mCuocHens != null) {
            mCuocHens.add(item);
            notifyItemInserted(mCuocHens.size() - 1);
        }
    }

    // Thêm phần tử tại vị trí chỉ định
    public void addItem(int position, CuocHen item) {
        if (mCuocHens != null && position >= 0 && position <= mCuocHens.size()) {
            mCuocHens.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Xóa phần tử theo vị trí
    public void removeItem(int position) {
        if (mCuocHens != null && position >= 0 && position < mCuocHens.size()) {
            mCuocHens.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Xóa phần tử theo đối tượng dùng indexOf
    public void removeItem(CuocHen item) {
        if (mCuocHens != null) {
            int index = mCuocHens.indexOf(item);
            if (index != -1) {
                removeItem(index);
            }
        }
    }

    // Thay thế phần tử tại vị trí chỉ định
    public void replaceItem(int position, CuocHen item) {
        if (mCuocHens != null && position >= 0 && position < mCuocHens.size()) {
            mCuocHens.remove(position);
            mCuocHens.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC PHƯƠNG THỨC CHÍNH CỦA RECYCLERVIEW.ADAPTER
    // ==============================================================

    @NonNull
    @Override
    public CuocHenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_cuoc_hen, parent, false);
        return new CuocHenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CuocHenViewHolder holder, int position) {
        CuocHen ch = mCuocHens.get(position);

        holder.tvTitle.setText(ch.getTieuDe());

        if (ch.getThoiGian() != null && !ch.getThoiGian().trim().isEmpty()) {
            holder.tvThoiGian.setText(ch.getThoiGian().trim());
        } else {
            holder.tvThoiGian.setText(mContext.getString(R.string.not_set));
        }

        if (ch.getDiaDiem() != null && !ch.getDiaDiem().trim().isEmpty()) {
            holder.layoutDiaDiem.setVisibility(View.VISIBLE);
            holder.tvDiaDiem.setText(ch.getDiaDiem().trim());
        } else {
            holder.layoutDiaDiem.setVisibility(View.GONE);
        }

        if (ch.getGhiChu() != null && !ch.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(ch.getGhiChu().trim());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Kiểm tra cuộc hẹn đã qua hay chưa bằng hàm tiện ích dùng chung
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
        return mCuocHens != null ? mCuocHens.size() : 0;
    }

    // ==============================================================
    // LỚP VIEWHOLDER NỘI BỘ
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
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvStatusBadge = (TextView) itemView.findViewById(R.id.tv_status_badge);
            tvThoiGian = (TextView) itemView.findViewById(R.id.tv_thoi_gian);
            layoutDiaDiem = (LinearLayout) itemView.findViewById(R.id.layout_dia_diem);
            tvDiaDiem = (TextView) itemView.findViewById(R.id.tv_dia_diem);
            tvGhiChu = (TextView) itemView.findViewById(R.id.tv_ghi_chu);
            btnEditMeeting = (ImageButton) itemView.findViewById(R.id.btn_edit_meeting);
            btnDeleteMeeting = (ImageButton) itemView.findViewById(R.id.btn_delete_meeting);

            // Thiết lập sự kiện chọn dòng
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

            // Sự kiện nhấn giữ (onLongClick): Trả về true để tiêu thụ sự kiện, ngăn sự kiện click thường chạy tiếp theo
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
