package com.example.app.model;

import java.io.Serializable;

public class NhiemVu implements Serializable {
    private int id;
    private int doAnId;
    private String ten;
    private String ghiChu;
    private String hanHoanThanh;
    private int daHoanThanh; // 0: chua hoan thanh, 1: da hoan thanh

    public NhiemVu() {
    }

    public NhiemVu(int id, int doAnId, String ten, String ghiChu, String hanHoanThanh, int daHoanThanh) {
        this.id = id;
        this.doAnId = doAnId;
        this.ten = ten;
        this.ghiChu = ghiChu;
        this.hanHoanThanh = hanHoanThanh;
        this.daHoanThanh = daHoanThanh;
    }

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

    public boolean isCompleted() {
        return daHoanThanh == 1;
    }

    public void setCompleted(boolean completed) {
        this.daHoanThanh = completed ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhiemVu nhiemVu = (NhiemVu) o;
        return id == nhiemVu.id
                && doAnId == nhiemVu.doAnId
                && daHoanThanh == nhiemVu.daHoanThanh
                && java.util.Objects.equals(ten, nhiemVu.ten)
                && java.util.Objects.equals(ghiChu, nhiemVu.ghiChu)
                && java.util.Objects.equals(hanHoanThanh, nhiemVu.hanHoanThanh);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, doAnId, ten, ghiChu, hanHoanThanh, daHoanThanh);
    }
}
