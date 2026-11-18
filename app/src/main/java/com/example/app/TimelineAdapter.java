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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Adapter minh họa kỹ thuật Multiple View Type kinh điển trong bài giảng (slide 195–210)
// Cho phép hiển thị nhiều kiểu dòng khác nhau (Tiêu đề nhóm, Nhiệm vụ, Lịch hẹn) trong cùng một RecyclerView
public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Mục D3: Định nghĩa các hằng số đại diện cho từng loại ViewType (slide 198)
    public static final int HEADER = 0;
    public static final int TASK = 1;
    public static final int MEETING = 2;

    // Danh sách dữ liệu đa kiểu (List<Object>) chứa lẫn String, NhiemVu, CuocHen (slide 199)
    private Context mContext;
    private List<Object> mObjects;
    private LayoutInflater mLayoutInflater;

    // Constructor nhận Context và danh sách đối tượng tổng hợp
    public TimelineAdapter(Context context, List<Object> objects) {
        this.mContext = context;
        this.mObjects = objects;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    // ==============================================================
    // BƯỚC QUAN TRỌNG NHẤT TRONG MULTIPLE VIEW TYPE: getItemViewType
    // ==============================================================

    // Mục D5: Override getItemViewType xác định kiểu của từng dòng dựa trên kiểu đối tượng thực tế (slide 201)
    @Override
    public int getItemViewType(int position) {
        if (mObjects == null || position < 0 || position >= mObjects.size()) {
            return -1;
        }

        Object obj = mObjects.get(position);
        if (obj instanceof String) {
            return HEADER; // Dòng tiêu đề nhóm
        } else if (obj instanceof NhiemVu) {
            return TASK; // Dòng hiển thị nhiệm vụ
        } else if (obj instanceof CuocHen) {
            return MEETING; // Dòng hiển thị lịch hẹn
        }
        return -1; // Mặc định nếu không nhận dạng được
    }

    // Mục D6: onCreateViewHolder dựa trên viewType để nạp layout XML tương ứng (slide 202)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER: {
                View view = mLayoutInflater.inflate(R.layout.row_header, parent, false);
                return new HeaderViewHolder(view);
            }
            case TASK: {
                View view = mLayoutInflater.inflate(R.layout.row_timeline_task, parent, false);
                return new TaskViewHolder(view);
            }
            case MEETING: {
                View view = mLayoutInflater.inflate(R.layout.row_timeline_meeting, parent, false);
                return new MeetingViewHolder(view);
            }
            default:
                throw new IllegalArgumentException("Không hỗ trợ viewType: " + viewType);
        }
    }

    // Mục D6: onBindViewHolder kiểm tra kiểu dòng để ép kiểu ViewHolder và gắn dữ liệu (slide 203)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case HEADER: {
                HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                String title = (String) mObjects.get(position);
                headerHolder.tvTitle.setText(title);
                break;
            }
            case TASK: {
                TaskViewHolder taskHolder = (TaskViewHolder) holder;
                NhiemVu nv = (NhiemVu) mObjects.get(position);

                taskHolder.tvTitle.setText(nv.getTen());

                if (nv.getGhiChu() != null && !nv.getGhiChu().trim().isEmpty()) {
                    taskHolder.tvGhiChu.setVisibility(View.VISIBLE);
                    taskHolder.tvGhiChu.setText(nv.getGhiChu());
                } else {
                    taskHolder.tvGhiChu.setVisibility(View.GONE);
                }

                String han = nv.getHanHoanThanh() != null ? nv.getHanHoanThanh() : "Chưa đặt";
                taskHolder.tvHanHoanThanh.setText("Hạn: " + han);

                boolean daXong = (nv.getDaHoanThanh() == 1);
                taskHolder.cbHoanThanh.setChecked(daXong);

                if (daXong) {
                    taskHolder.tvTitle.setPaintFlags(taskHolder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    taskHolder.tvTitle.setPaintFlags(taskHolder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                break;
            }
            case MEETING: {
                MeetingViewHolder meetingHolder = (MeetingViewHolder) holder;
                CuocHen ch = (CuocHen) mObjects.get(position);

                meetingHolder.tvTitle.setText(ch.getTieuDe());
                meetingHolder.tvThoiGian.setText(ch.getThoiGian() != null ? ch.getThoiGian() : "Chưa đặt");

                if (ch.getDiaDiem() != null && !ch.getDiaDiem().trim().isEmpty()) {
                    meetingHolder.layoutDiaDiem.setVisibility(View.VISIBLE);
                    meetingHolder.tvDiaDiem.setText(ch.getDiaDiem());
                } else {
                    meetingHolder.layoutDiaDiem.setVisibility(View.GONE);
                }

                // Tính toán huy hiệu Sắp tới / Đã qua
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String now = sdf.format(new Date());
                boolean daQua = (ch.getThoiGian() != null && ch.getThoiGian().compareTo(now) < 0);

                if (daQua) {
                    meetingHolder.tvStatusBadge.setText("Đã qua");
                    meetingHolder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_passed);
                    meetingHolder.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_expired));
                } else {
                    meetingHolder.tvStatusBadge.setText("Sắp tới");
                    meetingHolder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
                    meetingHolder.tvStatusBadge.setTextColor(ContextCompat.getColor(mContext, R.color.status_completed));
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mObjects != null ? mObjects.size() : 0;
    }

    // ==============================================================
    // 3 VIEWHOLDER CHO 3 KIỂU DÒNG KHÁC NHAU (slide 200, 204)
    // ==============================================================

    // 1. ViewHolder cho tiêu đề phân nhóm (HEADER)
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }

    // 2. ViewHolder cho dòng nhiệm vụ (TASK)
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbHoanThanh;
        TextView tvTitle;
        TextView tvGhiChu;
        TextView tvHanHoanThanh;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbHoanThanh = itemView.findViewById(R.id.cb_hoan_thanh);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvGhiChu = itemView.findViewById(R.id.tv_ghi_chu);
            tvHanHoanThanh = itemView.findViewById(R.id.tv_han_hoan_thanh);

            // Click vào nhiệm vụ trong dòng thời gian: hiện Toast tên và mở form sửa (mục D7)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < mObjects.size()) {
                        Object obj = mObjects.get(pos);
                        if (obj instanceof NhiemVu) {
                            NhiemVu nv = (NhiemVu) obj;
                            Toast.makeText(mContext, "Nhiệm vụ: " + nv.getTen(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(mContext, NhiemVuFormActivity.class);
                            intent.putExtra("do_an_id", nv.getDoAnId());
                            intent.putExtra("nhiem_vu_id", nv.getId());
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    // 3. ViewHolder cho dòng cuộc hẹn (MEETING)
    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvStatusBadge;
        TextView tvThoiGian;
        LinearLayout layoutDiaDiem;
        TextView tvDiaDiem;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvThoiGian = itemView.findViewById(R.id.tv_thoi_gian);
            layoutDiaDiem = itemView.findViewById(R.id.layout_dia_diem);
            tvDiaDiem = itemView.findViewById(R.id.tv_dia_diem);

            // Click vào cuộc hẹn trong dòng thời gian: hiện Toast tiêu đề và mở form sửa (mục D7)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && pos < mObjects.size()) {
                        Object obj = mObjects.get(pos);
                        if (obj instanceof CuocHen) {
                            CuocHen ch = (CuocHen) obj;
                            Toast.makeText(mContext, "Lịch hẹn: " + ch.getTieuDe(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(mContext, CuocHenFormActivity.class);
                            intent.putExtra("do_an_id", ch.getDoAnId());
                            intent.putExtra("cuoc_hen_id", ch.getId());
                            mContext.startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
