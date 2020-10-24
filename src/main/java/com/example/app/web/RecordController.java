package com.example.app.web;

import com.example.app.entity.Record;
import com.example.app.entity.RecordStatistics;
import com.example.app.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

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
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<Record>> getAll() {
        return ResponseEntity.ok(recordService.getAll());
    }


    @GetMapping(value = "/money_spent")
    public ResponseEntity<Map<String, BigDecimal>> getMoneyPerMonth(
            @RequestParam(name = "driver_id") Optional<Long> driverId) {
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

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadMultipart(@RequestParam MultipartFile file) {
        recordService.uploadDataFromFile(file);
        return ResponseEntity.ok().build();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> exceptionHandling(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();
        map.put("Error", e.getClass().getSimpleName());
        map.put("Message", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.ok(map);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> exceptionHandling(Exception e) {
        Map<String, String> map = new HashMap<>();
        map.put("Error", e.getClass().getSimpleName());
        map.put("Message", e.getMessage());
        return ResponseEntity.ok(map);
    }


}
