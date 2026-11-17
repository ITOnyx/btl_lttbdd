package com.example.app.model;

import java.io.Serializable;
import java.util.Objects;

public class DoAnWithTienDo implements Serializable {
    private DoAn doAn;
    private TienDoInfo tienDo;

    public DoAnWithTienDo() {
    }

    public DoAnWithTienDo(DoAn doAn, TienDoInfo tienDo) {
        this.doAn = doAn;
        this.tienDo = tienDo;
    }

    public DoAn getDoAn() {
        return doAn;
    }

    public void setDoAn(DoAn doAn) {
        this.doAn = doAn;
    }

    public TienDoInfo getTienDo() {
        return tienDo;
    }

    public void setTienDo(TienDoInfo tienDo) {
        this.tienDo = tienDo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoAnWithTienDo that = (DoAnWithTienDo) o;
        if (doAn != null && that.doAn != null) {
            if (doAn.getId() != that.doAn.getId()) return false;
            if (!Objects.equals(doAn.getTen(), that.doAn.getTen())) return false;
            if (!Objects.equals(doAn.getGiangVien(), that.doAn.getGiangVien())) return false;
            if (!Objects.equals(doAn.getNgayBatDau(), that.doAn.getNgayBatDau())) return false;
            if (!Objects.equals(doAn.getHanNop(), that.doAn.getHanNop())) return false;
            if (!Objects.equals(doAn.getMoTa(), that.doAn.getMoTa())) return false;
        } else if (doAn != that.doAn) {
            return false;
        }

        if (tienDo != null && that.tienDo != null) {
            return tienDo.getCompletedTasks() == that.tienDo.getCompletedTasks()
                    && tienDo.getTotalTasks() == that.tienDo.getTotalTasks();
        }
        return tienDo == that.tienDo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doAn != null ? doAn.getId() : 0);
    }
}
