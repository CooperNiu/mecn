package com.mecn.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeSeriesData 单元测试
 */
class TimeSeriesDataTest {

    private TimeSeriesData data;

    @BeforeEach
    void setUp() {
        LocalDate[] dates = {
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 2, 1),
            LocalDate.of(2023, 3, 1)
        };
        double[] values = {100.0, 102.5, 105.0};
        data = new TimeSeriesData("GDP", dates, values);
    }

    @Test
    void testDefaultConstructor() {
        TimeSeriesData d = new TimeSeriesData();
        assertNull(d.getIndicatorCode());
        assertNull(d.getDates());
        assertNull(d.getValues());
        assertNotNull(d.getMetadata());
    }

    @Test
    void testConstructorWithSize() {
        TimeSeriesData d = new TimeSeriesData("CPI", 10);
        assertEquals("CPI", d.getIndicatorCode());
        assertEquals(10, d.getDates().length);
        assertEquals(10, d.getValues().length);
    }

    @Test
    void testConstructorWithDates() {
        LocalDate[] dates = {LocalDate.of(2023, 1, 1)};
        TimeSeriesData d = new TimeSeriesData("PMI", 5, dates);
        assertEquals("PMI", d.getIndicatorCode());
        assertEquals(5, d.getValues().length);
    }

    @Test
    void testSetIndicatorCode() {
        data.setIndicatorCode("CPI");
        assertEquals("CPI", data.getIndicatorCode());
    }

    @Test
    void testSetDates() {
        LocalDate[] newDates = {LocalDate.of(2024, 1, 1)};
        data.setDates(newDates);
        assertEquals(1, data.getDates().length);
    }

    @Test
    void testSetValues() {
        double[] newValues = {1.0, 2.0};
        data.setValues(newValues);
        assertEquals(2, data.getValues().length);
    }

    @Test
    void testAddMetadata() {
        data.addMetadata("source", "FRED");
        assertEquals("FRED", data.getMetadata().get("source"));
    }

    @Test
    void testSize() {
        assertEquals(3, data.size());
    }

    @Test
    void testSizeEmpty() {
        TimeSeriesData d = new TimeSeriesData();
        assertEquals(0, d.size());
    }

    @Test
    void testGetValueAt() {
        assertEquals(100.0, data.getValueAt(0), 0.001);
        assertEquals(102.5, data.getValueAt(1), 0.001);
        assertEquals(105.0, data.getValueAt(2), 0.001);
    }

    @Test
    void testGetValueAtOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.getValueAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.getValueAt(10));
    }

    @Test
    void testToDoubleArray() {
        double[][] result = data.toDoubleArray();
        assertNotNull(result);
        assertEquals(2, result.length); // dates and values
        assertEquals(3, result[0].length); // 3 data points
    }
}
