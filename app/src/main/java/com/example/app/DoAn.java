package com.example.app;

// Lớp đối tượng Đồ án lưu trữ thông tin đề tài tốt nghiệp
public class DoAn {
    private int id;
    private String ten;
    private String moTa;
    private String giangVien;
    private String ngayBatDau;
    private String hanNop;

    // Constructor có ID (dùng khi đọc từ CSDL)
    public DoAn(int id, String ten, String moTa, String giangVien, String ngayBatDau, String hanNop) {
        this.id = id;
        this.ten = ten;
        this.moTa = moTa;
        this.giangVien = giangVien;
        this.ngayBatDau = ngayBatDau;
        this.hanNop = hanNop;
    }

    // Constructor không có ID (dùng khi thêm mới)
    public DoAn(String ten, String moTa, String giangVien, String ngayBatDau, String hanNop) {
        this.ten = ten;
        this.moTa = moTa;
        this.giangVien = giangVien;
        this.ngayBatDau = ngayBatDau;
        this.hanNop = hanNop;
    }

    public int getId() {
        return id;
    }

    public String getTen() {
        return ten;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getGiangVien() {
        return giangVien;
    }

    public String getNgayBatDau() {
        return ngayBatDau;
    }

    public String getHanNop() {
        return hanNop;
    }
}
