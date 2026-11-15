package com.example.app.model;

import java.io.Serializable;

public class CuocHen implements Serializable {
    private int id;
    private int doAnId;
    private String tieuDe;
    private String thoiGian; // yyyy-MM-dd HH:mm
    private String diaDiem;
    private String ghiChu;

    public CuocHen() {
    }

    public CuocHen(int id, int doAnId, String tieuDe, String thoiGian, String diaDiem, String ghiChu) {
        this.id = id;
        this.doAnId = doAnId;
        this.tieuDe = tieuDe;
        this.thoiGian = thoiGian;
        this.diaDiem = diaDiem;
        this.ghiChu = ghiChu;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuocHen cuocHen = (CuocHen) o;
        return id == cuocHen.id
                && doAnId == cuocHen.doAnId
                && java.util.Objects.equals(tieuDe, cuocHen.tieuDe)
                && java.util.Objects.equals(thoiGian, cuocHen.thoiGian)
                && java.util.Objects.equals(diaDiem, cuocHen.diaDiem)
                && java.util.Objects.equals(ghiChu, cuocHen.ghiChu);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, doAnId, tieuDe, thoiGian, diaDiem, ghiChu);
    }
}
