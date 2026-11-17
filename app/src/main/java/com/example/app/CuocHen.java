package com.example.app;

// Lớp đối tượng Cuộc hẹn trao đổi với giảng viên
public class CuocHen {
    private int id;
    private int doAnId;
    private String tieuDe;
    private String thoiGian; // Định dạng "yyyy-MM-dd HH:mm"
    private String diaDiem;
    private String ghiChu;

    // Constructor có ID (dùng khi đọc từ CSDL)
    public CuocHen(int id, int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        this.id = id;
        this.doAnId = doAnId;
        this.tieuDe = tieuDe;
        this.thoiGian = thoiGian;
        this.diaDiem = diaDiem;
        this.ghiChu = ghiChu;
    }

    // Constructor không có ID (dùng khi tạo mới)
    public CuocHen(int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        this.doAnId = doAnId;
        this.tieuDe = tieuDe;
        this.thoiGian = thoiGian;
        this.diaDiem = diaDiem;
        this.ghiChu = ghiChu;
    }

    public int getId() {
        return id;
    }

    public int getDoAnId() {
        return doAnId;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public String getDiaDiem() {
        return diaDiem;
    }

    public String getGhiChu() {
        return ghiChu;
    }
}
