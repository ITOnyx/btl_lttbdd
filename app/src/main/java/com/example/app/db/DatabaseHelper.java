package com.example.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tiendo.db";
    public static final int DATABASE_VERSION = 1;

    // Table names - chính xác theo thiết kế CSDL
    public static final String TABLE_DO_AN = "DoAn";
    public static final String TABLE_NHIEM_VU = "NhiemVu";
    public static final String TABLE_CUOC_HEN = "CuocHen";

    // Common column
    public static final String COLUMN_ID = "id";

    // Columns for DoAn
    public static final String COLUMN_DO_AN_TEN = "ten";
    public static final String COLUMN_DO_AN_MO_TA = "mo_ta";
    public static final String COLUMN_DO_AN_GIANG_VIEN = "giang_vien";
    public static final String COLUMN_DO_AN_NGAY_BAT_DAU = "ngay_bat_dau";
    public static final String COLUMN_DO_AN_HAN_NOP = "han_nop";

    // Columns for NhiemVu
    public static final String COLUMN_NHIEM_VU_DO_AN_ID = "do_an_id";
    public static final String COLUMN_NHIEM_VU_TEN = "ten";
    public static final String COLUMN_NHIEM_VU_GHI_CHU = "ghi_chu";
    public static final String COLUMN_NHIEM_VU_HAN_HOAN_THANH = "han_hoan_thanh";
    public static final String COLUMN_NHIEM_VU_DA_HOAN_THANH = "da_hoan_thanh";

    // Columns for CuocHen
    public static final String COLUMN_CUOC_HEN_DO_AN_ID = "do_an_id";
    public static final String COLUMN_CUOC_HEN_TIEU_DE = "tieu_de";
    public static final String COLUMN_CUOC_HEN_THOI_GIAN = "thoi_gian";
    public static final String COLUMN_CUOC_HEN_DIA_DIEM = "dia_diem";
    public static final String COLUMN_CUOC_HEN_GHI_CHU = "ghi_chu";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tao bang DoAn
        String createTableDoAn = "CREATE TABLE " + TABLE_DO_AN + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DO_AN_TEN + " TEXT NOT NULL, "
                + COLUMN_DO_AN_MO_TA + " TEXT, "
                + COLUMN_DO_AN_GIANG_VIEN + " TEXT, "
                + COLUMN_DO_AN_NGAY_BAT_DAU + " TEXT, "
                + COLUMN_DO_AN_HAN_NOP + " TEXT"
                + ");";

        // 2. Tao bang NhiemVu (REFERENCES DoAn(id) ON DELETE CASCADE)
        String createTableNhiemVu = "CREATE TABLE " + TABLE_NHIEM_VU + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NHIEM_VU_DO_AN_ID + " INTEGER NOT NULL REFERENCES " + TABLE_DO_AN + "(" + COLUMN_ID + ") ON DELETE CASCADE, "
                + COLUMN_NHIEM_VU_TEN + " TEXT NOT NULL, "
                + COLUMN_NHIEM_VU_GHI_CHU + " TEXT, "
                + COLUMN_NHIEM_VU_HAN_HOAN_THANH + " TEXT, "
                + COLUMN_NHIEM_VU_DA_HOAN_THANH + " INTEGER NOT NULL DEFAULT 0"
                + ");";

        // 3. Tao bang CuocHen (REFERENCES DoAn(id) ON DELETE CASCADE)
        String createTableCuocHen = "CREATE TABLE " + TABLE_CUOC_HEN + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CUOC_HEN_DO_AN_ID + " INTEGER NOT NULL REFERENCES " + TABLE_DO_AN + "(" + COLUMN_ID + ") ON DELETE CASCADE, "
                + COLUMN_CUOC_HEN_TIEU_DE + " TEXT NOT NULL, "
                + COLUMN_CUOC_HEN_THOI_GIAN + " TEXT NOT NULL, "
                + COLUMN_CUOC_HEN_DIA_DIEM + " TEXT, "
                + COLUMN_CUOC_HEN_GHI_CHU + " TEXT"
                + ");";

        db.execSQL(createTableDoAn);
        db.execSQL(createTableNhiemVu);
        db.execSQL(createTableCuocHen);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUOC_HEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NHIEM_VU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DO_AN);
        onCreate(db);
    }
}
