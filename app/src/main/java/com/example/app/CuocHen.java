package com.example.app;

import java.io.Serializable;

// Model đại diện cho một Cuộc hẹn trao đổi với GVHD (Bước 1: Model implements Serializable)
public class CuocHen implements Serializable {
    private int id;
    private int doAnId;
    private String tieuDe;
    private String thoiGian; // Định dạng "yyyy-MM-dd HH:mm"
    private String diaDiem;
    private String ghiChu;

    // Constructor mặc định không tham số
    public CuocHen() {
    }

    // Constructor có ID (dùng khi đọc từ CSDL SQLite qua Cursor)
    public CuocHen(int id, int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        this.id = id;
        this.doAnId = doAnId;
        this.tieuDe = tieuDe;
        this.thoiGian = thoiGian;
        this.diaDiem = diaDiem;
        this.ghiChu = ghiChu;
    }

    // Constructor không có ID (dùng khi tạo mới vào SQLite)
    public CuocHen(int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        this.doAnId = doAnId;
        this.tieuDe = tieuDe;
        this.thoiGian = thoiGian;
        this.diaDiem = diaDiem;
        this.ghiChu = ghiChu;
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

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
