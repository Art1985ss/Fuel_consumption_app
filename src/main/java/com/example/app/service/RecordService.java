package com.example.app.service;

import com.example.app.entity.Record;
import com.example.app.entity.RecordStatistics;
import com.example.app.repository.RecordRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

@Service
public class RecordService {
    private final RecordRepository recordRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public void save(Record record) {
        recordRepository.save(record);
    }

    public List<Record> getAll() {
        return recordRepository.findAll();
    }

    public Map<String, BigDecimal> getMoneyPerMonth(Optional<Long> driverId) {
        List<Record> records = getRecords(driverId);
        Function<Record, String> getMonth = record -> record.getDate().getMonth().name();
        Function<Record, BigDecimal> getTotalPrice = record ->
                record.getPricePerLiter().multiply(BigDecimal.valueOf(record.getVolume()));
        return records.stream().sorted()
                .collect(groupingBy(getMonth, mapping(getTotalPrice, reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    public List<Record> getForSpecifiedMonth(String month, Optional<Long> driverId) {
        List<Record> records = getRecords(driverId);
        Predicate<Record> filterByMonth =
                record -> record.getDate().getMonth().name().toLowerCase().equals(month.toLowerCase());
        records = records.stream().filter(filterByMonth).sorted().collect(toList());
        return records;
    }

    public List<RecordStatistics> getStatistics(Optional<Long> driverId) {
        List<Record> records = getRecords(driverId);
        Function<Record, Month> getMonth = record -> record.getDate().getMonth();
        List<RecordStatistics> recordStatisticsList = new ArrayList<>();
        records.stream()
                .collect(groupingBy(getMonth, groupingBy(Record::getFuelType)))
                .forEach((key, value) -> recordStatisticsList.addAll(
                        value.entrySet().stream().map(getEntryRecordStatisticsFunction(key)).collect(toList())));
        Collections.sort(recordStatisticsList);
        return recordStatisticsList;
    }

    public boolean uploadDataFromFile(MultipartFile file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sb.toString());
        JsonObject mainObject = JsonParser.parseString(sb.toString()).getAsJsonObject();
        JsonElement records = mainObject.get("records");
        List<Record> recordList = new ArrayList<>();
        for (JsonElement element : records.getAsJsonArray()) {
            JsonObject object = element.getAsJsonObject();
            Record record = new Record();
            record.setFuelType(object.get("fuelType").getAsString());
            record.setVolume(object.get("volume").getAsDouble());
            record.setPricePerLiter(object.get("pricePerLiter").getAsBigDecimal());
            record.setDate(LocalDate.parse(object.get("date").getAsString(), DateTimeFormatter.ofPattern("MM.dd.yyyy")));
            record.setDriverId(object.get("driverId").getAsLong());
            recordList.add(record);
        }
        recordRepository.saveAll(recordList);
        return true;
    }

    private Function<Map.Entry<String, List<Record>>, RecordStatistics> getEntryRecordStatisticsFunction(Month key) {
        return e -> {
            List<Record> recordList = e.getValue();
            RecordStatistics recordStatistics = new RecordStatistics();
            recordStatistics.setMonth(key);
            recordStatistics.setFuelType(e.getKey());
            recordStatistics.setVolume(recordList.stream()
                    .map(Record::getVolume)
                    .reduce(0d, Double::sum));
            List<BigDecimal> pricesPerRecord = recordList.stream()
                    .map(r -> r.getPricePerLiter().multiply(BigDecimal.valueOf(r.getVolume())))
                    .collect(toList());
            BigDecimal totalPrice = pricesPerRecord.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            //average for what? Per liter or per record?
            recordStatistics.setAveragePrice(totalPrice.divide(BigDecimal.valueOf(pricesPerRecord.size()),
                    RoundingMode.HALF_EVEN));
            recordStatistics.setTotalPrice(totalPrice);
            return recordStatistics;
        };
    }

    private List<Record> getRecords(Optional<Long> driverId) {
        List<Record> records = getAll();
        if (driverId.isEmpty()) {
            return records;
        }
        Predicate<Record> filterByDriverId = record -> record.getDriverId() == driverId.get();
        records = records.stream()
                .filter(filterByDriverId)
                .collect(toList());
        return records;
    }


}
