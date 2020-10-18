package com.example.app.service;

import com.example.app.entity.Record;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RecordServiceTest {
    @Autowired
    private RecordService victim;


    @Test
    @Sql(scripts = "classpath:db/populate.sql")
    public void shouldSave() {
        Record record = new Record();
        record.setFuelType("95");
        record.setVolume(20);
        record.setDate(LocalDate.now());
        record.setDriverId(3);
        record.setPricePerLiter(new BigDecimal("1.20"));
        victim.save(record);
        List<Record> records = victim.getAll();
        assertEquals(record, records.get(records.size() - 1));
    }

    @Test
    @Sql(scripts = "classpath:db/populate.sql")
    public void getMoneyPerMonthWithoutDriverId() {
        Map<String, BigDecimal> actual = victim.getMoneyPerMonth(Optional.empty());
        Map<String, BigDecimal> expected = new HashMap<>();
        expected.put("OCTOBER", new BigDecimal("75.000"));
        expected.put("SEPTEMBER", new BigDecimal("50.000"));
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @Sql(scripts = "classpath:db/populate.sql")
    public void getMoneyPerMonthByDriverId(long driverId) {
        Map<String, BigDecimal> actual = victim.getMoneyPerMonth(Optional.of(driverId));
        Map<String, BigDecimal> expected = new HashMap<>();
        if (driverId == 1L) {
            expected.put("OCTOBER", new BigDecimal("75.000"));
        }
        expected.put("SEPTEMBER", new BigDecimal("25.000"));
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"september", "october"})
    @Sql(scripts = "classpath:db/populate.sql")
    public void getForSpecifiedMonthWithoutDriverId(String month) {
        List<Record> records = victim.getForSpecifiedMonth(month, Optional.empty());
        assertEquals(month.equals("september") ? 2 : 3, records.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @Sql(scripts = "classpath:db/populate.sql")
    public void getForSpecifiedMonthWithDriverId(long driverId) {
        List<Record> records = victim.getForSpecifiedMonth("september", Optional.of(driverId));
        assertEquals(1, records.size());
    }

    @Test
    @Sql(scripts = "classpath:db/populate.sql")
    public void getStatisticsWithoutDriverId() {
        int actualSize = victim.getStatistics(Optional.empty()).size();
        assertEquals(2, actualSize);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    @Sql(scripts = "classpath:db/populate.sql")
    public void getStatisticsWithDriverId(long driverId) {
        int actualSize = victim.getStatistics(Optional.of(driverId)).size();
        int expectedSize;
        if (driverId == 1L) {
            expectedSize = 2;
        } else {
            expectedSize = 1;
        }
        assertEquals(expectedSize, actualSize);
    }

    @Test
    @Sql(scripts = "classpath:db/populate.sql")
    public void uploadFile() {
        Record record = createRecord();
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        String json = gson.toJson(record);
        json = "{ \"records\":[" + json + "]}";
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes()
        );
        assertTrue(victim.uploadDataFromFile(file));
    }

    private Record createRecord() {
        Record record = new Record();
        record.setId(0);
        record.setFuelType("95");
        record.setPricePerLiter(BigDecimal.valueOf(1.25));
        record.setVolume(20);
        record.setDate(LocalDate.now());
        record.setDriverId(1);
        return record;
    }

}