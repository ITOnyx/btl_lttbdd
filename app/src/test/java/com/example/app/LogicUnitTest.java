package com.example.app;

import com.example.app.model.DoAn;
import com.example.app.model.DoAnWithTienDo;
import com.example.app.model.TienDoInfo;
import com.example.app.util.DateUtils;

import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LogicUnitTest {

    @Test
    public void testTienDoInfoCalculation() {
        // Truong hop 0 nhiem vu: tranh chia cho 0, tra ve 0%
        TienDoInfo emptyInfo = new TienDoInfo(0, 0);
        assertEquals(0, emptyInfo.getPercent());
        assertEquals("0/0 (0%)", emptyInfo.getFormattedText());

        // Truong hop 2/5 -> 40%
        TienDoInfo info = new TienDoInfo(2, 5);
        assertEquals(2, info.getCompletedTasks());
        assertEquals(5, info.getTotalTasks());
        assertEquals(40, info.getPercent());
        assertEquals("2/5 (40%)", info.getFormattedText());

        // Truong hop 3/3 -> 100%
        TienDoInfo fullInfo = new TienDoInfo(3, 3);
        assertEquals(100, fullInfo.getPercent());
        assertEquals("3/3 (100%)", fullInfo.getFormattedText());
    }

    @Test
    public void testDateUtilsValidation() {
        // Ngay hop le (hanNop >= ngayBatDau)
        assertTrue(DateUtils.isValidDateRange("2026-03-01", "2026-03-01"));
        assertTrue(DateUtils.isValidDateRange("2026-03-01", "2026-06-30"));

        // Ngay khong hop le (hanNop < ngayBatDau)
        assertFalse(DateUtils.isValidDateRange("2026-06-30", "2026-03-01"));

        // Dinh dang ngay
        assertTrue(DateUtils.getCurrentDate().matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue(DateUtils.getCurrentDateTime().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));

        // Kiem tra thoi gian da qua hay chua
        assertTrue(DateUtils.isPastDateTime("2020-01-01 10:00"));
        assertFalse(DateUtils.isPastDateTime("2099-01-01 10:00"));
        assertFalse(DateUtils.isPastDateTime("chuoi-sai-dinh-dang"));
    }

    @Test
    public void testDoAnWithTienDoModel() {
        DoAn d1 = new DoAn(1, "App Android", "Mo ta", "TS. Nguyen", "2026-01-01", "2026-05-01");
        TienDoInfo t1 = new TienDoInfo(1, 2);
        DoAnWithTienDo item1 = new DoAnWithTienDo(d1, t1);

        DoAn d2 = new DoAn(1, "App Android", "Mo ta", "TS. Nguyen", "2026-01-01", "2026-05-01");
        TienDoInfo t2 = new TienDoInfo(1, 2);
        DoAnWithTienDo item2 = new DoAnWithTienDo(d2, t2);

        assertEquals(item1, item2);
    }

    @Test
    public void testUtcConversionFixedValueAndMultipleTimeZones() {
        long expectedMillis = 1778803200000L;
        String expectedDate = "2026-05-15";

        TimeZone originalTimeZone = TimeZone.getDefault();
        try {
            String[] testTimeZones = new String[]{
                    "Asia/Ho_Chi_Minh",
                    "America/Los_Angeles",
                    "UTC"
            };

            for (String tzId : testTimeZones) {
                TimeZone.setDefault(TimeZone.getTimeZone(tzId));

                // 1 & 2: Khẳng định giá trị cố định tại từng múi giờ
                Long actualMillis = DateUtils.dateStringToUtcMillis(expectedDate);
                assertNotNull("Millis should not be null in " + tzId, actualMillis);
                assertEquals("dateStringToUtcMillis mismatch in " + tzId, expectedMillis, actualMillis.longValue());

                // 3: Xác nhận formatUtcDate dùng UTC tường minh, không dựa vào múi giờ mặc định
                String actualDate = DateUtils.formatUtcDate(expectedMillis);
                assertEquals("formatUtcDate mismatch in " + tzId, expectedDate, actualDate);
            }
        } finally {
            // Khôi phục TimeZone gốc trong finally
            TimeZone.setDefault(originalTimeZone);
        }
    }
}
