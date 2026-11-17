package com.example.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.app.db.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class SampleDataLoader {

    public static class ImportResult {
        private final boolean success;
        private final int projectsCount;
        private final int tasksCount;
        private final int meetingsCount;
        private final String errorMessage;

        public ImportResult(boolean success, int projectsCount, int tasksCount, int meetingsCount, String errorMessage) {
            this.success = success;
            this.projectsCount = projectsCount;
            this.tasksCount = tasksCount;
            this.meetingsCount = meetingsCount;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getProjectsCount() {
            return projectsCount;
        }

        public int getTasksCount() {
            return tasksCount;
        }

        public int getMeetingsCount() {
            return meetingsCount;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static ImportResult importFromAssets(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        int insertedProjects = 0;
        int insertedTasks = 0;
        int insertedMeetings = 0;

        db.beginTransaction();
        try {
            String jsonContent = readAssetFile(context, "du_lieu_mau.json");
            JSONObject root = new JSONObject(jsonContent);
            JSONArray doAnArray = root.getJSONArray("do_an");

            for (int i = 0; i < doAnArray.length(); i++) {
                JSONObject doAnObj = doAnArray.getJSONObject(i);

                ContentValues vDoAn = new ContentValues();
                vDoAn.put(DatabaseHelper.COLUMN_DO_AN_TEN, doAnObj.getString("ten"));

                if (doAnObj.isNull("mo_ta")) {
                    vDoAn.putNull(DatabaseHelper.COLUMN_DO_AN_MO_TA);
                } else {
                    vDoAn.put(DatabaseHelper.COLUMN_DO_AN_MO_TA, doAnObj.getString("mo_ta"));
                }

                if (doAnObj.isNull("giang_vien")) {
                    vDoAn.putNull(DatabaseHelper.COLUMN_DO_AN_GIANG_VIEN);
                } else {
                    vDoAn.put(DatabaseHelper.COLUMN_DO_AN_GIANG_VIEN, doAnObj.getString("giang_vien"));
                }

                if (doAnObj.isNull("ngay_bat_dau")) {
                    vDoAn.putNull(DatabaseHelper.COLUMN_DO_AN_NGAY_BAT_DAU);
                } else {
                    vDoAn.put(DatabaseHelper.COLUMN_DO_AN_NGAY_BAT_DAU, doAnObj.getString("ngay_bat_dau"));
                }

                if (doAnObj.isNull("han_nop")) {
                    vDoAn.putNull(DatabaseHelper.COLUMN_DO_AN_HAN_NOP);
                } else {
                    vDoAn.put(DatabaseHelper.COLUMN_DO_AN_HAN_NOP, doAnObj.getString("han_nop"));
                }

                long doAnId = db.insertOrThrow(DatabaseHelper.TABLE_DO_AN, null, vDoAn);
                insertedProjects++;

                // Nhiệm vụ
                if (doAnObj.has("nhiem_vu")) {
                    JSONArray nvArray = doAnObj.getJSONArray("nhiem_vu");
                    for (int j = 0; j < nvArray.length(); j++) {
                        JSONObject nvObj = nvArray.getJSONObject(j);
                        ContentValues vNv = new ContentValues();
                        vNv.put(DatabaseHelper.COLUMN_NHIEM_VU_DO_AN_ID, doAnId);
                        vNv.put(DatabaseHelper.COLUMN_NHIEM_VU_TEN, nvObj.getString("ten"));

                        if (nvObj.isNull("ghi_chu")) {
                            vNv.putNull(DatabaseHelper.COLUMN_NHIEM_VU_GHI_CHU);
                        } else {
                            vNv.put(DatabaseHelper.COLUMN_NHIEM_VU_GHI_CHU, nvObj.getString("ghi_chu"));
                        }

                        if (nvObj.isNull("han_hoan_thanh")) {
                            vNv.putNull(DatabaseHelper.COLUMN_NHIEM_VU_HAN_HOAN_THANH);
                        } else {
                            vNv.put(DatabaseHelper.COLUMN_NHIEM_VU_HAN_HOAN_THANH, nvObj.getString("han_hoan_thanh"));
                        }

                        vNv.put(DatabaseHelper.COLUMN_NHIEM_VU_DA_HOAN_THANH, nvObj.optInt("da_hoan_thanh", 0));
                        db.insertOrThrow(DatabaseHelper.TABLE_NHIEM_VU, null, vNv);
                        insertedTasks++;
                    }
                }

                // Cuộc hẹn
                if (doAnObj.has("cuoc_hen")) {
                    JSONArray chArray = doAnObj.getJSONArray("cuoc_hen");
                    for (int k = 0; k < chArray.length(); k++) {
                        JSONObject chObj = chArray.getJSONObject(k);
                        ContentValues vCh = new ContentValues();
                        vCh.put(DatabaseHelper.COLUMN_CUOC_HEN_DO_AN_ID, doAnId);
                        vCh.put(DatabaseHelper.COLUMN_CUOC_HEN_TIEU_DE, chObj.getString("tieu_de"));
                        vCh.put(DatabaseHelper.COLUMN_CUOC_HEN_THOI_GIAN, chObj.getString("thoi_gian"));

                        if (chObj.isNull("dia_diem")) {
                            vCh.putNull(DatabaseHelper.COLUMN_CUOC_HEN_DIA_DIEM);
                        } else {
                            vCh.put(DatabaseHelper.COLUMN_CUOC_HEN_DIA_DIEM, chObj.getString("dia_diem"));
                        }

                        if (chObj.isNull("ghi_chu")) {
                            vCh.putNull(DatabaseHelper.COLUMN_CUOC_HEN_GHI_CHU);
                        } else {
                            vCh.put(DatabaseHelper.COLUMN_CUOC_HEN_GHI_CHU, chObj.getString("ghi_chu"));
                        }

                        db.insertOrThrow(DatabaseHelper.TABLE_CUOC_HEN, null, vCh);
                        insertedMeetings++;
                    }
                }
            }

            db.setTransactionSuccessful();
            return new ImportResult(true, insertedProjects, insertedTasks, insertedMeetings, null);
        } catch (Exception e) {
            return new ImportResult(false, 0, 0, 0, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private static String readAssetFile(Context context, String fileName) throws Exception {
        try (InputStream is = context.getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
}
