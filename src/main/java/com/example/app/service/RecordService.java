package com.example.app.service;

import com.example.app.entity.Record;
import com.example.app.entity.RecordStatistics;
import com.example.app.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return records.stream()
                .collect(groupingBy(getMonth, mapping(getTotalPrice, reducing(BigDecimal.ZERO, BigDecimal::add))));
    }

    public List<Record> getForSpecifiedMonth(String month, Optional<Long> driverId) {
        List<Record> records = getRecords(driverId);
        Predicate<Record> filterByMonth =
                record -> record.getDate().getMonth().name().toLowerCase().equals(month.toLowerCase());
        records = records.stream().filter(filterByMonth).collect(toList());
        return records;
    }

    public List<RecordStatistics> getStatistics(Optional<Long> driverId) {
        List<Record> records = getRecords(driverId);
        Function<Record, Month> getMonth = record -> record.getDate().getMonth();
        List<RecordStatistics> recordStatisticsList = new ArrayList<>();
        records.stream()
                .collect(groupingBy(getMonth, groupingBy(Record::getFuelType)))
                .forEach((key, value) -> recordStatisticsList.addAll(
                        value.entrySet().stream().map(e -> {
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
                            recordStatistics.setAveragePrice(totalPrice.divide(BigDecimal.valueOf(pricesPerRecord.size()),
                                    RoundingMode.HALF_EVEN));
                            recordStatistics.setTotalPrice(totalPrice);
                            return recordStatistics;
                        }).collect(toList())));
        return recordStatisticsList;
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
