package com.example.app.web;

import com.example.app.entity.Record;
import com.example.app.entity.RecordStatistics;
import com.example.app.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/records")
public class RecordController {
    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<Void> save(@RequestBody @Valid Record record) {
        recordService.save(record);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<List<Record>> getAll() {
        return ResponseEntity.ok(recordService.getAll());
    }

    @GetMapping("/new")
    public ResponseEntity<Record> getNew() {
        Record record = new Record();
        record.setFuelType("D");
        record.setPricePerLiter(new BigDecimal("1.17"));
        record.setVolume(23);
        record.setDriverId(3);
        record.setDate(LocalDate.now());
        recordService.save(record);
        return ResponseEntity.ok(record);
    }

    @GetMapping(value = "/money_spent")
    public ResponseEntity<Map<String, BigDecimal>> getMoneyPerMonth(
            @RequestParam(name = "driver_id") Optional<Long> driverId) {
        recordService.getMoneyPerMonth(driverId);
        return ResponseEntity.ok(recordService.getMoneyPerMonth(driverId));
    }

    @GetMapping("/{month}")
    public ResponseEntity<List<Record>> getForSpecifiedMonth(@PathVariable String month,
                                                             @RequestParam(name = "driver_id") Optional<Long> driverId) {
        List<Record> records = recordService.getForSpecifiedMonth(month, driverId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<RecordStatistics>> getStatistics(@RequestParam(name = "driver_id") Optional<Long> driverId) {
        return ResponseEntity.ok(recordService.getStatistics(driverId));
    }

}
