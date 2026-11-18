package com.example.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adapter hiển thị các thẻ thống kê nhỏ trên RecyclerView cuộn ngang (slide 185, 192)
public class ThongKeAdapter extends RecyclerView.Adapter<ThongKeAdapter.ThongKeViewHolder> {

    private Context mContext;
    private List<ThongKeItem> mItems;
    private LayoutInflater mLayoutInflater;

    public ThongKeAdapter(Context context, List<ThongKeItem> items) {
        this.mContext = context;
        this.mItems = items;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ThongKeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.row_thong_ke, parent, false);
        return new ThongKeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThongKeViewHolder holder, int position) {
        ThongKeItem item = mItems.get(position);
        holder.tvTitle.setText(item.getTieuDe());
        holder.tvGiaTri.setText(item.getGiaTri());
        holder.ivIcon.setImageResource(item.getIconResId());
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public static class ThongKeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvGiaTri;
        TextView tvTitle;

        public ThongKeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvGiaTri = itemView.findViewById(R.id.tv_gia_tri);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
