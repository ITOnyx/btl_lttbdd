package com.example.app;

import java.io.Serializable;

// Model cho từng thẻ thống kê nhỏ hiển thị trên RecyclerView cuộn ngang
public class ThongKeItem implements Serializable {
    private String tieuDe;
    private String giaTri;
    private int iconResId;

    public ThongKeItem() {
    }

    public ThongKeItem(String tieuDe, String giaTri, int iconResId) {
        this.tieuDe = tieuDe;
        this.giaTri = giaTri;
        this.iconResId = iconResId;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(String giaTri) {
        this.giaTri = giaTri;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
