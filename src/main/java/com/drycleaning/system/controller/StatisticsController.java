package com.drycleaning.system.controller;

import com.drycleaning.system.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/daily-income")
    public ResponseEntity<Double> getDailyIncome(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double income = statisticsService.getDailyIncome(date);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/monthly-income")
    public ResponseEntity<Double> getMonthlyIncome(@RequestParam int year, @RequestParam int month) {
        Double income = statisticsService.getMonthlyIncome(year, month);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/cash-income")
    public ResponseEntity<Double> getCashIncome(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double income = statisticsService.getCashIncome(date);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/prepaid-income")
    public ResponseEntity<Double> getPrepaidIncome(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Double income = statisticsService.getPrepaidIncome(date);
        return ResponseEntity.ok(income);
    }

    @GetMapping("/unfinished-orders-count")
    public ResponseEntity<Long> getUnfinishedOrderCount() {
        Long count = statisticsService.getUnfinishedOrderCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/today-orders-count")
    public ResponseEntity<Long> getTodayOrderCount(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long count = statisticsService.getTodayOrderCount(date);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyStatistics(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> stats = statisticsService.getDailyStatistics(date);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(@RequestParam int year, @RequestParam int month) {
        Map<String, Object> stats = statisticsService.getMonthlyStatistics(year, month);
        return ResponseEntity.ok(stats);
    }
}