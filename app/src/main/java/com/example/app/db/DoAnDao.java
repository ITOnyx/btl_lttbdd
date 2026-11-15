package com.example.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.app.model.DoAn;
import com.example.app.model.DoAnWithTienDo;
import com.example.app.model.TienDoInfo;

import java.util.ArrayList;
import java.util.List;

public class DoAnDao {
    private final DatabaseHelper dbHelper;

    public DoAnDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(DoAn doAn) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DO_AN_TEN, doAn.getTen());
        values.put(DatabaseHelper.COLUMN_DO_AN_MO_TA, doAn.getMoTa());
        values.put(DatabaseHelper.COLUMN_DO_AN_GIANG_VIEN, doAn.getGiangVien());
        values.put(DatabaseHelper.COLUMN_DO_AN_NGAY_BAT_DAU, doAn.getNgayBatDau());
        values.put(DatabaseHelper.COLUMN_DO_AN_HAN_NOP, doAn.getHanNop());

        long id = db.insert(DatabaseHelper.TABLE_DO_AN, null, values);
        if (id != -1) {
            doAn.setId((int) id);
        }
        return id;
    }

    public int update(DoAn doAn) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DO_AN_TEN, doAn.getTen());
        values.put(DatabaseHelper.COLUMN_DO_AN_MO_TA, doAn.getMoTa());
        values.put(DatabaseHelper.COLUMN_DO_AN_GIANG_VIEN, doAn.getGiangVien());
        values.put(DatabaseHelper.COLUMN_DO_AN_NGAY_BAT_DAU, doAn.getNgayBatDau());
        values.put(DatabaseHelper.COLUMN_DO_AN_HAN_NOP, doAn.getHanNop());

        return db.update(
                DatabaseHelper.TABLE_DO_AN,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(doAn.getId())}
        );
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_DO_AN,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public DoAn getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DoAn doAn = null;
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_DO_AN,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                doAn = cursorToDoAn(cursor);
            }
        }
        return doAn;
    }

    public List<DoAn> getAll() {
        List<DoAn> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_DO_AN,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToDoAn(cursor));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public List<DoAnWithTienDo> getAllWithTienDo() {
        List<DoAnWithTienDo> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT "
                + "d." + DatabaseHelper.COLUMN_ID + " AS d_id, "
                + "d." + DatabaseHelper.COLUMN_DO_AN_TEN + " AS d_ten, "
                + "d." + DatabaseHelper.COLUMN_DO_AN_MO_TA + " AS d_mo_ta, "
                + "d." + DatabaseHelper.COLUMN_DO_AN_GIANG_VIEN + " AS d_giang_vien, "
                + "d." + DatabaseHelper.COLUMN_DO_AN_NGAY_BAT_DAU + " AS d_ngay_bat_dau, "
                + "d." + DatabaseHelper.COLUMN_DO_AN_HAN_NOP + " AS d_han_nop, "
                + "COUNT(n." + DatabaseHelper.COLUMN_ID + ") AS total_tasks, "
                + "COALESCE(SUM(CASE WHEN n." + DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH + " = 1 THEN 1 ELSE 0 END), 0) AS completed_tasks "
                + "FROM " + DatabaseHelper.TABLE_DO_AN + " d "
                + "LEFT JOIN " + DatabaseHelper.TABLE_NHIEM_VU + " n ON d." + DatabaseHelper.COLUMN_ID + " = n." + DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID + " "
                + "GROUP BY d." + DatabaseHelper.COLUMN_ID + " "
                + "ORDER BY d." + DatabaseHelper.COLUMN_ID + " DESC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    DoAn doAn = new DoAn();
                    doAn.setId(cursor.getInt(cursor.getColumnIndexOrThrow("d_id")));
                    doAn.setTen(cursor.getString(cursor.getColumnIndexOrThrow("d_ten")));
                    doAn.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("d_mo_ta")));
                    doAn.setGiangVien(cursor.getString(cursor.getColumnIndexOrThrow("d_giang_vien")));
                    doAn.setNgayBatDau(cursor.getString(cursor.getColumnIndexOrThrow("d_ngay_bat_dau")));
                    doAn.setHanNop(cursor.getString(cursor.getColumnIndexOrThrow("d_han_nop")));

                    int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_tasks"));
                    int completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed_tasks"));
                    TienDoInfo tienDo = new TienDoInfo(completed, total);

                    result.add(new DoAnWithTienDo(doAn, tienDo));
                } while (cursor.moveToNext());
            }
        }

        return result;
    }

    public TienDoInfo getTienDo(int doAnId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int totalTasks = 0;
        int completedTasks = 0;

        String query = "SELECT "
                + "COUNT(*) AS total, "
                + "COALESCE(SUM(CASE WHEN " + DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH + " = 1 THEN 1 ELSE 0 END), 0) AS completed "
                + "FROM " + DatabaseHelper.TABLE_NHIEM_VU + " "
                + "WHERE " + DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doAnId)})) {
            if (cursor != null && cursor.moveToFirst()) {
                totalTasks = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
                completedTasks = cursor.getInt(cursor.getColumnIndexOrThrow("completed"));
            }
        }

        return new TienDoInfo(completedTasks, totalTasks);
    }

    private DoAn cursorToDoAn(Cursor cursor) {
        DoAn doAn = new DoAn();
        doAn.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        doAn.setTen(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DO_AN_TEN)));
        doAn.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DO_AN_MO_TA)));
        doAn.setGiangVien(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DO_AN_GIANG_VIEN)));
        doAn.setNgayBatDau(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DO_AN_NGAY_BAT_DAU)));
        doAn.setHanNop(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DO_AN_HAN_NOP)));
        return doAn;
    }
}
