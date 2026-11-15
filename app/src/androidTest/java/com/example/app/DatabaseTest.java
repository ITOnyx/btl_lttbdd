package com.example.app;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.db.CuocHenDao;
import com.example.app.db.DatabaseHelper;
import com.example.app.db.DoAnDao;
import com.example.app.db.NhiemVuDao;
import com.example.app.model.CuocHen;
import com.example.app.model.DoAn;
import com.example.app.model.DoAnWithTienDo;
import com.example.app.model.NhiemVu;
import com.example.app.model.TienDoInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private static final String TAG = "TIENDO_DB_TEST";

    private Context context;
    private DatabaseHelper dbHelper;
    private DoAnDao doAnDao;
    private NhiemVuDao nhiemVuDao;
    private CuocHenDao cuocHenDao;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        dbHelper = DatabaseHelper.getInstance(context);
        // Xoa sach DB truoc moi lan test
        context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
        doAnDao = new DoAnDao(context);
        nhiemVuDao = new NhiemVuDao(context);
        cuocHenDao = new CuocHenDao(context);
    }

    @After
    public void tearDown() {
        dbHelper.close();
    }

    @Test
    public void testFullDatabaseOperations() {
        Log.d(TAG, "=== BAT DAU KIEM THU CSDL TIENDO.DB ===");

        // 1. Test chen DoAn
        DoAn doAn = new DoAn("Xây dựng App Quản lý", "Mô tả đề tài", "TS. Nguyễn Văn A", "2026-03-01", "2026-06-30");
        long doAnId = doAnDao.insert(doAn);
        assertTrue("Insert DoAn phai tra ve id > 0", doAnId > 0);
        Log.d(TAG, "1. Insert DoAn thanh cong, id = " + doAnId);

        // 2. Test doc DoAn vua chen
        DoAn fetchedDoAn = doAnDao.getById((int) doAnId);
        assertNotNull("DoAn lay ra khong duoc null", fetchedDoAn);
        assertEquals("Xây dựng App Quản lý", fetchedDoAn.getTen());
        assertEquals("TS. Nguyễn Văn A", fetchedDoAn.getGiangVien());

        // 3. Test chen cac NhiemVu
        NhiemVu nv1 = new NhiemVu((int) doAnId, "Khao sat yeu cau", "Lam viec voi GV", "2026-03-15", 1);
        NhiemVu nv2 = new NhiemVu((int) doAnId, "Thiet ke CSDL", "Tao bang SQLite", "2026-03-25", 1);
        NhiemVu nv3 = new NhiemVu((int) doAnId, "Lap trinh UI", "RecyclerView va Adapter", "2026-04-10", 0);
        NhiemVu nv4 = new NhiemVu((int) doAnId, "Kiem thu & dong goi", "Bao cao", "2026-04-30", 0);
        NhiemVu nv5 = new NhiemVu((int) doAnId, "Bao ve do an", "Trinh chieu slide", "2026-05-15", 0);

        nhiemVuDao.insert(nv1);
        nhiemVuDao.insert(nv2);
        long nv3Id = nhiemVuDao.insert(nv3);
        nhiemVuDao.insert(nv4);
        nhiemVuDao.insert(nv5);

        List<NhiemVu> tasks = nhiemVuDao.getByDoAnId((int) doAnId);
        assertEquals("Tong so nhiem vu phai la 5", 5, tasks.size());

        // 4. Test cap nhat nhanh da_hoan_thanh cua NhiemVuDao
        nhiemVuDao.updateStatus((int) nv3Id, true);
        NhiemVu updatedNv3 = nhiemVuDao.getById((int) nv3Id);
        assertEquals("da_hoan_thanh phai duoc cap nhat len 1", 1, updatedNv3.getDaHoanThanh());
        assertTrue(updatedNv3.isCompleted());

        // Reset lai ve 0 de tinh tien do tiep theo
        nhiemVuDao.updateStatus((int) nv3Id, false);
        assertEquals(0, nhiemVuDao.getById((int) nv3Id).getDaHoanThanh());

        // 5. Test tinh tien do truc tiep: 2/5 (40%)
        TienDoInfo tienDo = doAnDao.getTienDo((int) doAnId);
        assertEquals(2, tienDo.getCompletedTasks());
        assertEquals(5, tienDo.getTotalTasks());
        assertEquals(40, tienDo.getPercent());
        assertEquals("2/5 (40%)", tienDo.getFormattedText());

        // 6. Test ham getAllWithTienDo() (LEFT JOIN + GROUP BY)
        List<DoAnWithTienDo> listWithTienDo = doAnDao.getAllWithTienDo();
        assertEquals(1, listWithTienDo.size());
        DoAnWithTienDo item = listWithTienDo.get(0);
        assertEquals(doAnId, item.getDoAn().getId());
        assertEquals(2, item.getTienDo().getCompletedTasks());
        assertEquals(5, item.getTienDo().getTotalTasks());
        assertEquals(40, item.getTienDo().getPercent());

        // 7. Test CuocHenDao: chen lộn xộn ngày để kiểm tra sắp xếp thoi_gian ASC
        CuocHen chLate = new CuocHen((int) doAnId, "Báo cáo cuối kỳ", "2026-05-20 09:00", "Phòng Hội trường", "Trình bày slide");
        CuocHen chEarly = new CuocHen((int) doAnId, "Gặp GV chốt đề cương", "2026-03-05 08:00", "Phòng C101", "Gặp trực tiếp");
        CuocHen chMid = new CuocHen((int) doAnId, "Báo cáo tiến độ đợt 1", "2026-04-10 14:00", "Google Meet", "Online");

        cuocHenDao.insert(chLate);
        long chEarlyId = cuocHenDao.insert(chEarly);
        cuocHenDao.insert(chMid);

        List<CuocHen> meetings = cuocHenDao.getByDoAnId((int) doAnId);
        assertEquals("Phai co 3 cuoc hen", 3, meetings.size());
        // Kiem tra thu tu tang dan theo thoi_gian (ASC)
        assertEquals("Cuoc hen dau tien phai la ngay 2026-03-05", "2026-03-05 08:00", meetings.get(0).getThoiGian());
        assertEquals("Cuoc hen thu hai phai la ngay 2026-04-10", "2026-04-10 14:00", meetings.get(1).getThoiGian());
        assertEquals("Cuoc hen thu ba phai la ngay 2026-05-20", "2026-05-20 09:00", meetings.get(2).getThoiGian());

        // 8. Test update va delete CuocHen
        CuocHen chToUpdate = cuocHenDao.getById((int) chEarlyId);
        chToUpdate.setDiaDiem("Phòng C305 (Đã chuyển)");
        cuocHenDao.update(chToUpdate);
        assertEquals("Phòng C305 (Đã chuyển)", cuocHenDao.getById((int) chEarlyId).getDiaDiem());

        cuocHenDao.delete((int) chEarlyId);
        assertEquals(2, cuocHenDao.getByDoAnId((int) doAnId).size());
        assertNull(cuocHenDao.getById((int) chEarlyId));

        // 9. Test FOREIGN KEY ON DELETE CASCADE
        int deletedRows = doAnDao.delete((int) doAnId);
        assertEquals(1, deletedRows);

        assertNull(doAnDao.getById((int) doAnId));
        assertEquals(0, nhiemVuDao.getByDoAnId((int) doAnId).size());
        assertEquals(0, cuocHenDao.getByDoAnId((int) doAnId).size());

        Log.d(TAG, "=== TAT CA CAC TEST DA PASS 100%! ===");
    }
}
