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

// Adapter một kiểu dòng hiển thị Đồ án (Khớp chuẩn khung slide 190–191, 194, 220–223)
public class DoAnAdapter extends RecyclerView.Adapter<DoAnAdapter.DoAnViewHolder> {

    // Khai báo hằng số TAG theo mẫu slide 190
    private static final String TAG = "DoAnAdapter";

    // Khai báo các thuộc tính thành viên tiền tố m theo mẫu slide 190
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

    // Constructor chính thống nhất nhận listener (Context context, List<DoAn> datas, OnDoAnClickListener listener)
    public DoAnAdapter(Context context, List<DoAn> datas, OnDoAnClickListener listener) {
        mContext = context;
        mDoAns = datas;
        mLayoutInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    // ==============================================================
    // ANIMATION: ĐÚNG 3 NHÓM HÀM THEO MẪU SLIDE 220–223
    // ==============================================================

    // Thêm phần tử vào cuối danh sách (mẫu slide 220)
    public void addItem(DoAn item) {
        if (mDoAns != null) {
            mDoAns.add(item);
            notifyItemInserted(mDoAns.size() - 1);
        }
    }

    // Thêm phần tử tại vị trí position chỉ định (mẫu slide 220)
    public void addItem(int position, DoAn item) {
        if (mDoAns != null && position >= 0 && position <= mDoAns.size()) {
            mDoAns.add(position, item);
            notifyItemInserted(position);
        }
    }

    // Xóa phần tử theo vị trí position (mẫu slide 221)
    public void removeItem(int position) {
        if (mDoAns != null && position >= 0 && position < mDoAns.size()) {
            mDoAns.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Xóa phần tử theo đối tượng dùng indexOf (mở rộng an toàn từ mẫu slide 221)
    public void removeItem(DoAn item) {
        if (mDoAns != null) {
            int index = mDoAns.indexOf(item);
            if (index != -1) {
                removeItem(index);
            }
        }
    }

    // Thay thế/đổi phần tử tại vị trí position (mẫu slide 221: remove + add + notifyItemChanged)
    public void replaceItem(int position, DoAn item) {
        if (mDoAns != null && position >= 0 && position < mDoAns.size()) {
            mDoAns.remove(position);
            mDoAns.add(position, item);
            notifyItemChanged(position);
        }
    }

    // ==============================================================
    // CÁC PHƯƠNG THỨC CHÍNH CỦA RECYCLERVIEW.ADAPTER (SLIDE 190–191)
    // ==============================================================

    @NonNull
    @Override
    public DoAnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view từ row_do_an.xml (mẫu slide 190)
        View itemView = mLayoutInflater.inflate(R.layout.row_do_an, parent, false);
        return new DoAnViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DoAnViewHolder holder, int position) {
        // Lấy đồ án theo vị trí và gắn dữ liệu vào ViewHolder (mẫu slide 190)
        DoAn doAn = mDoAns.get(position);

        // Gắn tiêu đề đồ án
        holder.tvTitle.setText(doAn.getTen());

        // Mô tả tóm tắt: ẩn nếu rỗng hoặc null sau khi trim
        if (doAn.getMoTa() != null && !doAn.getMoTa().trim().isEmpty()) {
            holder.tvMoTa.setVisibility(View.VISIBLE);
            holder.tvMoTa.setText(doAn.getMoTa().trim());
        } else {
            holder.tvMoTa.setVisibility(View.GONE);
        }

        // Giảng viên hướng dẫn: kiểm tra null và rỗng sau khi trim, dùng chuỗi từ strings.xml
        if (doAn.getGiangVien() != null && !doAn.getGiangVien().trim().isEmpty()) {
            holder.tvGiangVien.setText(mContext.getString(R.string.gvhd_prefix) + doAn.getGiangVien().trim());
        } else {
            holder.tvGiangVien.setText(mContext.getString(R.string.gvhd_prefix) + mContext.getString(R.string.not_available));
        }

        // Thời gian: ngày bắt đầu và hạn nộp (kiểm tra null hoặc rỗng sau trim coi là chưa có)
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

        // Hiển thị tiến độ hoàn thành từ thuộc tính model được nạp sẵn qua LEFT JOIN (không gọi DBHelper)
        int tong = doAn.getTongNhiemVu();
        int xong = doAn.getSoNhiemVuXong();
        int phanTram = (tong == 0) ? 0 : (xong * 100 / tong);
        holder.tvTienDo.setText(xong + "/" + tong + " (" + phanTram + "%)");
        holder.progressBar.setProgress(phanTram);
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phần tử (mẫu slide 191)
        return mDoAns != null ? mDoAns.size() : 0;
    }

    // ==============================================================
    // LỚP VIEWHOLDER NỘI BỘ (MẪU SLIDE 191, 194, 203)
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
            // Ánh xạ View bằng findViewById ép kiểu theo mẫu slide 191
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvMoTa = (TextView) itemView.findViewById(R.id.tv_mo_ta);
            tvGiangVien = (TextView) itemView.findViewById(R.id.tv_giang_vien);
            tvThoiGian = (TextView) itemView.findViewById(R.id.tv_thoi_gian);
            tvTienDo = (TextView) itemView.findViewById(R.id.tv_tien_do);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            btnEdit = (ImageButton) itemView.findViewById(R.id.btn_edit);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btn_delete);

            // Thiết lập sự kiện chọn dòng (mẫu slide 194, 203)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Kiểm tra an toàn khác slide: tránh crash khi đang xóa dòng
                    if (position == RecyclerView.NO_POSITION) return;
                    if (mListener != null) {
                        mListener.onItemClick(mDoAns.get(position), position);
                    }
                }
            });

            // Sự kiện nhấn giữ (onLongClick) (mẫu slide 222):
            // KHÁC SLIDE: Slide trả về false làm sự kiện click chạy tiếp khi nhấc tay;
            // Ở đây trả về true để tiêu thụ hoàn toàn sự kiện, ngăn click nhầm sau khi nhấn giữ.
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return false;
                    if (mListener != null) {
                        mListener.onDeleteClick(mDoAns.get(position), position);
                        return true; // Đã tiêu thụ sự kiện long click an toàn
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
