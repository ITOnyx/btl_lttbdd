package com.example.app;

import java.io.Serializable;

// Model đại diện cho một Nhiệm vụ thuộc Đồ án (Bước 1: Model implements Serializable)
public class NhiemVu implements Serializable {
    private int id;
    private int doAnId;
    private String ten;
    private String ghiChu;
    private String hanHoanThanh;
    private int daHoanThanh; // 0: chưa hoàn thành, 1: đã hoàn thành

    // Constructor mặc định không tham số
    public NhiemVu() {
    }

    // Constructor có ID (dùng khi đọc từ CSDL SQLite qua Cursor)
    public NhiemVu(int id, int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh) {
        this.id = id;
        this.doAnId = doAnId;
        this.ten = ten;
        this.ghiChu = ghiChu;
        this.hanHoanThanh = hanHoanThanh;
        this.daHoanThanh = daHoanThanh;
    }

    // Constructor không có ID (dùng khi tạo mới vào SQLite)
    public NhiemVu(int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh) {
        this.doAnId = doAnId;
        this.ten = ten;
        this.ghiChu = ghiChu;
        this.hanHoanThanh = hanHoanThanh;
        this.daHoanThanh = daHoanThanh;
    }

    // Các hàm Getter và Setter theo đúng chuẩn JavaBean
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoAnId() {
        return doAnId;
    }

    public void setDoAnId(int doAnId) {
        this.doAnId = doAnId;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getHanHoanThanh() {
        return hanHoanThanh;
    }

    public void setHanHoanThanh(String hanHoanThanh) {
        this.hanHoanThanh = hanHoanThanh;
    }

    public int getDaHoanThanh() {
        return daHoanThanh;
    }

    public void setDaHoanThanh(int daHoanThanh) {
        this.daHoanThanh = daHoanThanh;
    }
}
