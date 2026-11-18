package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter nhiều kiểu dòng (Khớp chuẩn khung slide 202–208)
public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Khai báo hằng số TAG theo mẫu bài giảng
    private static final String TAG = "TimelineAdapter";

    // 1. Khai báo các hằng số nhận diện ViewType theo đúng mẫu slide 202
    public static final int HEADER = 0;
    public static final int TASK = 1;
    public static final int MEETING = 2;

    // 2. Khai báo các thuộc tính thành viên theo mẫu slide 202
    private List<Object> mObjects;
    private Context mContext;

    // 3. Constructor theo mẫu slide 202
    public TimelineAdapter(Context context, List<Object> objects) {
        mContext = context;
        mObjects = objects;
    }

    // Xác định kiểu dòng theo mẫu slide 206
    @Override
    public int getItemViewType(int position) {
        if (mObjects == null || position < 0 || position >= mObjects.size()) {
            return -1;
        }

        // Kiểm tra đối tượng bằng instanceof (mẫu slide 206)
        if (mObjects.get(position) instanceof String) {
            return HEADER;
        } else if (mObjects.get(position) instanceof NhiemVu) {
            return TASK;
        } else if (mObjects.get(position) instanceof CuocHen) {
            return MEETING;
        }
        return -1;
    }

    // Nạp layout tương ứng với viewType theo mẫu slide 207
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(mContext);
        switch (viewType) {
            case HEADER: {
                View itemView0 = li.inflate(R.layout.row_header, parent, false);
                return new HeaderViewHolder(itemView0);
            }
            case TASK: {
                View itemView1 = li.inflate(R.layout.row_timeline_task, parent, false);
                return new TaskViewHolder(itemView1);
            }
            case MEETING: {
                View itemView2 = li.inflate(R.layout.row_timeline_meeting, parent, false);
                return new MeetingViewHolder(itemView2);
            }
            default:
                break;
        }

        // KHÁC SLIDE: Trong slide cuối hàm trả về null; tuy nhiên Android RecyclerView
        // không cho phép ViewHolder null và sẽ crash ứng dụng với NullPointerException.
        // Ta ném ngoại lệ rõ ràng để bảo vệ an toàn mã nguồn.
        throw new IllegalArgumentException("Kiểu dòng không hợp lệ: " + viewType);
    }

    // Ép kiểu holder và gắn dữ liệu theo mẫu slide 208
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER: {
                HeaderViewHolder h = (HeaderViewHolder) holder;
                String title = (String) mObjects.get(position);
                h.tvTitle.setText(title);
                break;
            }
            case TASK: {
                TaskViewHolder t = (TaskViewHolder) holder;
                NhiemVu nv = (NhiemVu) mObjects.get(position);

                t.tvTitle.setText(nv.getTen());

                if (nv.getGhiChu() != null && !nv.getGhiChu().trim().isEmpty()) {
                    t.tvGhiChu.setVisibility(View.VISIBLE);
                    t.tvGhiChu.setText(nv.getGhiChu().trim());
                } else {
                    t.tvGhiChu.setVisibility(View.GONE);
                }

                if (nv.getHanHoanThanh() != null && !nv.getHanHoanThanh().trim().isEmpty()) {
                    t.tvHanHoanThanh.setText(mContext.getString(R.string.deadline_prefix) + nv.getHanHoanThanh().trim());
                } else {
                    t.tvHanHoanThanh.setText(mContext.getString(R.string.deadline_prefix) + mContext.getString(R.string.not_set));
                }

                boolean daXong = (nv.getDaHoanThanh() == 1);
                t.cbHoanThanh.setChecked(daXong);

                if (daXong) {
                    t.tvTitle.setPaintFlags(t.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    t.tvTitle.setPaintFlags(t.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                break;
            }
            case MEETING: {
                MeetingViewHolder m = (MeetingViewHolder) holder;
                CuocHen ch = (CuocHen) mObjects.get(position);

                m.tvTitle.setText(ch.getTieuDe());

                if (ch.getThoiGian() != null && !ch.getThoiGian().trim().isEmpty()) {
                    m.tvThoiGian.setText(ch.getThoiGian().trim());
                } else {
                    m.tvThoiGian.setText(mContext.getString(R.string.not_set));
                }

                if (ch.getDiaDiem() != null && !ch.getDiaDiem().trim().isEmpty()) {
                    m.layoutDiaDiem.setVisibility(View.VISIBLE);
                    m.tvDiaDiem.setText(ch.getDiaDiem().trim());
                } else {
                    m.layoutDiaDiem.setVisibility(View.GONE);
                }

                // Gọi hàm logic dùng chung kiểm tra lịch hẹn đã qua hay chưa
                boolean daQua = DBHelper.laDaQua(ch.getThoiGian());
                if (daQua) {
                    m.tvStatusBadge.setText(mContext.getString(R.string.meeting_passed));
                    m.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_passed);
                    m.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_expired));
                } else {
                    m.tvStatusBadge.setText(mContext.getString(R.string.meeting_upcoming));
                    m.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
                    m.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_completed));
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phần tử theo mẫu slide 208
        return mObjects != null ? mObjects.size() : 0;
    }

    // ==============================================================
    // 3 LỚP VIEWHOLDER LỒNG NHAU (MẪU SLIDE 203–205)
    // ==============================================================

    // 1. ViewHolder cho dòng tiêu đề nhóm (mẫu slide 203)
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            // Header không bắt sự kiện click
        }
    }

    // 2. ViewHolder cho dòng nhiệm vụ (mẫu slide 204)
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cbHoanThanh;
        public TextView tvTitle;
        public TextView tvGhiChu;
        public TextView tvHanHoanThanh;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbHoanThanh = (CheckBox) itemView.findViewById(R.id.cb_hoan_thanh);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvGhiChu = (TextView) itemView.findViewById(R.id.tv_ghi_chu);
            tvHanHoanThanh = (TextView) itemView.findViewById(R.id.tv_han_hoan_thanh);

            // Chỉ hiển thị; đổi trạng thái ở màn hình Nhiệm vụ
            cbHoanThanh.setClickable(false);
            cbHoanThanh.setFocusable(false);

            // Thiết lập sự kiện chọn dòng (mẫu slide 194)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    Object obj = mObjects.get(pos);
                    if (obj instanceof NhiemVu) {
                        NhiemVu nv = (NhiemVu) obj;
                        Intent intent = new Intent(mContext, NhiemVuFormActivity.class);
                        intent.putExtra(NhiemVuFormActivity.EXTRA_DO_AN_ID, nv.getDoAnId());
                        intent.putExtra(NhiemVuFormActivity.EXTRA_NHIEM_VU_ID, nv.getId());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    // 3. ViewHolder cho dòng cuộc hẹn (mẫu slide 205)
    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvStatusBadge;
        public TextView tvThoiGian;
        public LinearLayout layoutDiaDiem;
        public TextView tvDiaDiem;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvStatusBadge = (TextView) itemView.findViewById(R.id.tv_status_badge);
            tvThoiGian = (TextView) itemView.findViewById(R.id.tv_thoi_gian);
            layoutDiaDiem = (LinearLayout) itemView.findViewById(R.id.layout_dia_diem);
            tvDiaDiem = (TextView) itemView.findViewById(R.id.tv_dia_diem);

            // Thiết lập sự kiện chọn dòng (mẫu slide 194)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    Object obj = mObjects.get(pos);
                    if (obj instanceof CuocHen) {
                        CuocHen ch = (CuocHen) obj;
                        Intent intent = new Intent(mContext, CuocHenFormActivity.class);
                        intent.putExtra(CuocHenFormActivity.EXTRA_DO_AN_ID, ch.getDoAnId());
                        intent.putExtra(CuocHenFormActivity.EXTRA_CUOC_HEN_ID, ch.getId());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
