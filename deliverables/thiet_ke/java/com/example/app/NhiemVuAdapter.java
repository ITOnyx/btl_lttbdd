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

// Adapter hiển thị danh sách Nhiệm vụ
public class NhiemVuAdapter extends RecyclerView.Adapter<NhiemVuAdapter.NhiemVuViewHolder> {

    private static final String TAG = "NhiemVuAdapter";

    private List<NhiemVu> mNhiemVus;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnNhiemVuClickListener mListener;

    // Interface callback để Activity lắng nghe sự kiện
    public interface OnNhiemVuClickListener {
        void onToggleStatus(NhiemVu nv, boolean isCompleted, int position);
        void onEdit(NhiemVu nv, int position);
        void onDelete(NhiemVu nv, int position);
    }

    public NhiemVuAdapter(Context context, List<NhiemVu> datas, OnNhiemVuClickListener listener) {
        mContext = context;
        mNhiemVus = datas;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // ==============================================================
    // CÁC HÀM XỬ LÝ ANIMATION RECYCLERVIEW
    // ==============================================================

    // Thêm phần tử vào cuối danh sách
    public void addItem(NhiemVu item) {
        if (mNhiemVus != null) {
            mNhiemVus.add(item);
            notifyItemInserted(mNhiemVus.size() - 1);
        }
    }

    // Thêm phần tử tại vị trí chỉ định
    public void addItem(int position, NhiemVu item) {
        if (mNhiemVus != null && position >= 0 && position <= mNhiemVus.size()) {
            mNhiemVus.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Xóa phần tử theo vị trí
    public void removeItem(int position) {
        if (mNhiemVus != null && position >= 0 && position < mNhiemVus.size()) {
            mNhiemVus.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Xóa phần tử theo đối tượng dùng indexOf
    public void removeItem(NhiemVu item) {
        if (mNhiemVus != null) {
            int index = mNhiemVus.indexOf(item);
            if (index != -1) {
                removeItem(index);
            }
        }
    }

    // Thay thế phần tử tại vị trí chỉ định
    public void replaceItem(int position, NhiemVu item) {
        if (mNhiemVus != null && position >= 0 && position < mNhiemVus.size()) {
            mNhiemVus.remove(position);
            mNhiemVus.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC PHƯƠNG THỨC CHÍNH CỦA RECYCLERVIEW.ADAPTER
    // ==============================================================

    @NonNull
    @Override
    public NhiemVuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_nhiem_vu, parent, false);
        return new NhiemVuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NhiemVuViewHolder holder, int position) {
        NhiemVu nv = mNhiemVus.get(position);

        holder.tvTitle.setText(nv.getTen());

        if (nv.getGhiChu() != null && !nv.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(nv.getGhiChu().trim());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        if (nv.getHanHoanThanh() != null && !nv.getHanHoanThanh().trim().isEmpty()) {
            holder.tvHanHoanThanh.setText(mContext.getString(R.string.deadline_prefix) + nv.getHanHoanThanh().trim());
        } else {
            holder.tvHanHoanThanh.setText(mContext.getString(R.string.deadline_prefix) + mContext.getString(R.string.not_set));
        }

        boolean daXong = (nv.getDaHoanThanh() == 1);
        holder.cbHoanThanh.setChecked(daXong);

        if (daXong) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return mNhiemVus != null ? mNhiemVus.size() : 0;
    }

    // ==============================================================
    // LỚP VIEWHOLDER NỘI BỘ
    // ==============================================================

    class NhiemVuViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cbHoanThanh;
        private TextView tvTitle;
        private TextView tvGhiChu;
        private TextView tvHanHoanThanh;
        private ImageButton btnEditTask;
        private ImageButton btnDeleteTask;

        public NhiemVuViewHolder(@NonNull View itemView) {
            super(itemView);
            cbHoanThanh = (CheckBox) itemView.findViewById(R.id.cb_hoan_thanh);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvGhiChu = (TextView) itemView.findViewById(R.id.tv_ghi_chu);
            tvHanHoanThanh = (TextView) itemView.findViewById(R.id.tv_han_hoan_thanh);
            btnEditTask = (ImageButton) itemView.findViewById(R.id.btn_edit_task);
            btnDeleteTask = (ImageButton) itemView.findViewById(R.id.btn_delete_task);

            // Thiết lập sự kiện chọn dòng
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onEdit(mNhiemVus.get(position), position);
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
                        mListener.onDelete(mNhiemVus.get(position), position);
                        return true;
                    }
                    return false;
                }
            });

            // Bắt sự kiện CheckBox hoàn thành: cập nhật ngay model và gạch ngang, sau đó báo listener
            cbHoanThanh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    NhiemVu nv = mNhiemVus.get(position);
                    boolean checked = cbHoanThanh.isChecked();
                    nv.setDaHoanThanh(checked ? 1 : 0);
                    if (checked) {
                        tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    if (mListener != null) {
                        mListener.onToggleStatus(nv, checked, position);
                    }
                }
            });

            btnEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onEdit(mNhiemVus.get(position), position);
                    }
                }
            });

            btnDeleteTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onDelete(mNhiemVus.get(position), position);
                    }
                }
            });
        }
    }
}
