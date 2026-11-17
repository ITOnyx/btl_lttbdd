package com.example.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.app.model.NhiemVu;

import java.util.ArrayList;
import java.util.List;

public class NhiemVuDao {
    private final DatabaseHelper dbHelper;

    public NhiemVuDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(NhiemVu nhiemVu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID, nhiemVu.getDoAnId());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_TEN, nhiemVu.getTen());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_GHI_CHU, nhiemVu.getGhiChu());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_HAN_HOAN_THANH, nhiemVu.getHanHoanThanh());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH, nhiemVu.getDaHoanThanh());

        long id = db.insert(DatabaseHelper.TABLE_NHIEM_VU, null, values);
        if (id != -1) {
            nhiemVu.setId((int) id);
        }
        return id;
    }

    public int update(NhiemVu nhiemVu) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID, nhiemVu.getDoAnId());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_TEN, nhiemVu.getTen());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_GHI_CHU, nhiemVu.getGhiChu());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_HAN_HOAN_THANH, nhiemVu.getHanHoanThanh());
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH, nhiemVu.getDaHoanThanh());

        return db.update(
                DatabaseHelper.TABLE_NHIEM_VU,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(nhiemVu.getId())}
        );
    }

    public int updateStatus(int id, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH, isCompleted ? 1 : 0);

        return db.update(
                DatabaseHelper.TABLE_NHIEM_VU,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_NHIEM_VU,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteByDoAnId(int doAnId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_NHIEM_VU,
                DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID + " = ?",
                new String[]{String.valueOf(doAnId)}
        );
    }

    public NhiemVu getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        NhiemVu nhiemVu = null;
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_NHIEM_VU,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                nhiemVu = cursorToNhiemVu(cursor);
            }
        }
        return nhiemVu;
    }

    public List<NhiemVu> getAll() {
        List<NhiemVu> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_NHIEM_VU,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID + " DESC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToNhiemVu(cursor));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public List<NhiemVu> getByDoAnId(int doAnId) {
        List<NhiemVu> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_NHIEM_VU,
                null,
                DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID + " = ?",
                new String[]{String.valueOf(doAnId)},
                null,
                null,
                DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH + " ASC, " + DatabaseHelper.COLUMN_NHIEM_VU_HAN_HOAN_THANH + " ASC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToNhiemVu(cursor));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public int getCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_NHIEM_VU, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    private NhiemVu cursorToNhiemVu(Cursor cursor) {
        NhiemVu nv = new NhiemVu();
        nv.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        nv.setDoAnId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID)));
        nv.setTen(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NHIEM_VU_TEN)));
        nv.setGhiChu(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NHIEM_VU_GHI_CHU)));
        nv.setHanHoanThanh(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NHIEM_VU_HAN_HOAN_THANH)));
        nv.setDaHoanThanh(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH)));
        return nv;
    }
}
