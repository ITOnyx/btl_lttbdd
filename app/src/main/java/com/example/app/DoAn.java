package com.example.app;

import java.io.Serializable;

// Model đại diện cho một Đồ án tốt nghiệp (Bước 1: Model implements Serializable)
public class DoAn implements Serializable {
    private int id;
    private String ten;
    private String moTa;
    private String giangVien;
    private String ngayBatDau;
    private String hanNop;

    // Constructor mặc định không tham số
    public DoAn() {
    }

    // Constructor đầy đủ tham số có ID (dùng khi đọc từ CSDL SQLite qua Cursor)
    public DoAn(int id, String ten, String moTa, String giangVien, String ngayBatDau, String hanNop) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.giangVien = giangVien;
        this.ngayBatDau = ngayBatDau;
        this.hanNop = hanNop;
    }

    // Constructor không có ID (dùng khi thêm mới vào SQLite)
    public DoAn(String ten, String moTa, String giangVien, String ngayBatDau, String hanNop) {
        this.ten = ten;
        this.moTa = moTa;
        this.giangVien = giangVien;
        this.ngayBatDau = ngayBatDau;
        this.hanNop = hanNop;
    }

    // Các hàm Getter và Setter theo đúng chuẩn JavaBean
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(String giangVien) {
        this.giangVien = giangVien;
    }

    public String getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(String ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public String getHanNop() {
        return hanNop;
    }

    public void setHanNop(String hanNop) {
        this.hanNop = hanNop;
    }
}
