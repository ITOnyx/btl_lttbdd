package com.example.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.app.model.CuocHen;

import java.util.ArrayList;
import java.util.List;

public class CuocHenDao {
    private final DatabaseHelper dbHelper;

    public CuocHenDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(CuocHen cuocHen) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_DO_AN_ID, cuocHen.getDoAnId());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_TIEU_DE, cuocHen.getTieuDe());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_THOI_GIAN, cuocHen.getThoiGian());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_DIA_DIEM, cuocHen.getDiaDiem());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_GHI_CHU, cuocHen.getGhiChu());

        long id = db.insert(DatabaseHelper.TABLE_CUOC_HEN, null, values);
        if (id != -1) {
            cuocHen.setId((int) id);
        }
        return id;
    }

    public int update(CuocHen cuocHen) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_DO_AN_ID, cuocHen.getDoAnId());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_TIEU_DE, cuocHen.getTieuDe());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_THOI_GIAN, cuocHen.getThoiGian());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_DIA_DIEM, cuocHen.getDiaDiem());
        values.put(DatabaseHelper.COLUMN_CUOC_HEN_GHI_CHU, cuocHen.getGhiChu());

        return db.update(
                DatabaseHelper.TABLE_CUOC_HEN,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(cuocHen.getId())}
        );
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_CUOC_HEN,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteByDoAnId(int doAnId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                DatabaseHelper.TABLE_CUOC_HEN,
                DatabaseHelper.COLUMN_CUOC_HEN_DO_AN_ID + " = ?",
                new String[]{String.valueOf(doAnId)}
        );
    }

    public CuocHen getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        CuocHen cuocHen = null;
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CUOC_HEN,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                cuocHen = cursorToCuocHen(cursor);
            }
        }
        return cuocHen;
    }

    public List<CuocHen> getAll() {
        List<CuocHen> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CUOC_HEN,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_CUOC_HEN_THOI_GIAN + " ASC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToCuocHen(cursor));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    public List<CuocHen> getByDoAnId(int doAnId) {
        List<CuocHen> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CUOC_HEN,
                null,
                DatabaseHelper.COLUMN_CUOC_HEN_DO_AN_ID + " = ?",
                new String[]{String.valueOf(doAnId)},
                null,
                null,
                DatabaseHelper.COLUMN_CUOC_HEN_THOI_GIAN + " ASC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(cursorToCuocHen(cursor));
                } while (cursor.moveToNext());
            }
        }
        return list;
    }

    private CuocHen cursorToCuocHen(Cursor cursor) {
        CuocHen ch = new CuocHen();
        ch.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        ch.setDoAnId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUOC_HEN_DO_AN_ID)));
        ch.setTieuDe(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUOC_HEN_TIEU_DE)));
        ch.setThoiGian(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUOC_HEN_THOI_GIAN)));
        ch.setDiaDiem(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUOC_HEN_DIA_DIEM)));
        ch.setGhiChu(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CUOC_HEN_GHI_CHU)));
        return ch;
    }
}
