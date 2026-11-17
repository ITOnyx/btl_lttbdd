package com.example.app;

// Lớp đối tượng Nhiệm vụ thuộc một Đồ án
public class NhiemVu {
    private int id;
    private int doAnId;
    private String ten;
    private String ghiChu;
    private String hanHoanThanh;
    private int daHoanThanh; // 0: chưa hoàn thành, 1: đã hoàn thành

    // Constructor có ID (dùng khi đọc từ CSDL)
    public NhiemVu(int id, int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh) {
        this.id = id;
        this.doAnId = doAnId;
        this.ten = ten;
        this.ghiChu = ghiChu;
        this.hanHoanThanh = hanHoanThanh;
        this.daHoanThanh = daHoanThanh;
    }

    // Constructor không có ID (dùng khi tạo mới)
    public NhiemVu(int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh) {
        this.doAnId = doAnId;
        this.ten = ten;
        this.ghiChu = ghiChu;
        this.hanHoanThanh = hanHoanThanh;
        this.daHoanThanh = daHoanThanh;
    }

    public int getId() {
        return id;
    }

    public int getDoAnId() {
        return doAnId;
    }

    public String getTen() {
        return ten;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getHanHoanThanh() {
        return hanHoanThanh;
    }

    public int getDaHoanThanh() {
        return daHoanThanh;
    }
}
