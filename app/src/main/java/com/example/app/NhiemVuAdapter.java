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

// Adapter một kiểu dòng hiển thị Nhiệm vụ (Khớp chuẩn khung slide 190–191, 194, 220–223)
public class NhiemVuAdapter extends RecyclerView.Adapter<NhiemVuAdapter.NhiemVuViewHolder> {

    // Khai báo hằng số TAG theo mẫu slide 190
    private static final String TAG = "NhiemVuAdapter";

    // Khai báo các thuộc tính thành viên tiền tố m theo mẫu slide 190
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

    // Constructor theo mẫu slide 190: (Context context, List<NhiemVu> datas)
    public NhiemVuAdapter(Context context, List<NhiemVu> datas) {
        mContext = context;
        mNhiemVus = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    // Constructor mở rộng nhận thêm listener
    public NhiemVuAdapter(Context context, List<NhiemVu> datas, OnNhiemVuClickListener listener) {
        this(context, datas);
        mListener = listener;
    }

    public void setOnNhiemVuClickListener(OnNhiemVuClickListener listener) {
        mListener = listener;
    }

    // ==============================================================
    // ANIMATION: ĐÚNG 3 NHÓM HÀM THEO MẪU SLIDE 220–223
    // ==============================================================

    // Thêm phần tử vào cuối danh sách (mẫu slide 220)
    public void addItem(NhiemVu item) {
        if (mNhiemVus != null) {
            mNhiemVus.add(item);
            notifyItemInserted(mNhiemVus.size() - 1);
        }
    }

    // Thêm phần tử tại vị trí position chỉ định (mẫu slide 220)
    public void addItem(int position, NhiemVu item) {
        if (mNhiemVus != null && position >= 0 && position <= mNhiemVus.size()) {
            mNhiemVus.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Xóa phần tử theo vị trí position (mẫu slide 221)
    public void removeItem(int position) {
        if (mNhiemVus != null && position >= 0 && position < mNhiemVus.size()) {
            mNhiemVus.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Xóa phần tử theo đối tượng dùng indexOf (mở rộng an toàn từ mẫu slide 221)
    public void removeItem(NhiemVu item) {
        if (mNhiemVus != null) {
            int index = mNhiemVus.indexOf(item);
            if (index != -1) {
                removeItem(index);
            }
        }
    }

    // Thay thế/đổi phần tử tại vị trí position (mẫu slide 221: remove + add + notifyItemChanged)
    public void replaceItem(int position, NhiemVu item) {
        if (mNhiemVus != null && position >= 0 && position < mNhiemVus.size()) {
            mNhiemVus.remove(position);
            mNhiemVus.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC PHƯƠNG THỨC CHÍNH CỦA RECYCLERVIEW.ADAPTER (SLIDE 190–191)
    // ==============================================================

    @NonNull
    @Override
    public NhiemVuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view từ row_nhiem_vu.xml (mẫu slide 190)
        View itemView = mLayoutInflater.inflate(R.layout.row_nhiem_vu, parent, false);
        return new NhiemVuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NhiemVuViewHolder holder, int position) {
        // Lấy nhiệm vụ theo vị trí và gắn dữ liệu vào ViewHolder (mẫu slide 190)
        NhiemVu nv = mNhiemVus.get(position);

        // Gắn dữ liệu vào ViewHolder
        holder.tvTitle.setText(nv.getTen());

        if (nv.getGhiChu() != null && !nv.getGhiChu().trim().isEmpty()) {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText(nv.getGhiChu());
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        String han = nv.getHanHoanThanh() != null ? nv.getHanHoanThanh() : "Chưa đặt";
        holder.tvHanHoanThanh.setText("Hạn: " + han);

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
        // Trả về số lượng phần tử (mẫu slide 191)
        return mNhiemVus != null ? mNhiemVus.size() : 0;
    }

    // ==============================================================
    // LỚP VIEWHOLDER NỘI BỘ (MẪU SLIDE 191)
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
            // Ánh xạ View bằng findViewById ép kiểu theo mẫu slide 191
            cbHoanThanh = (CheckBox) itemView.findViewById(R.id.cb_hoan_thanh);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvGhiChu = (TextView) itemView.findViewById(R.id.tv_ghi_chu);
            tvHanHoanThanh = (TextView) itemView.findViewById(R.id.tv_han_hoan_thanh);
            btnEditTask = (ImageButton) itemView.findViewById(R.id.btn_edit_task);
            btnDeleteTask = (ImageButton) itemView.findViewById(R.id.btn_delete_task);

            // Thiết lập sự kiện chọn dòng (mẫu slide 194, 203)
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

            // Sự kiện nhấn giữ (onLongClick) (mẫu slide 222):
            // KHÁC SLIDE: Trả về true để tiêu thụ sự kiện, ngăn sự kiện click thường chạy tiếp theo
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

            // Bắt sự kiện CheckBox hoàn thành
            cbHoanThanh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onToggleStatus(mNhiemVus.get(position), cbHoanThanh.isChecked(), position);
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
