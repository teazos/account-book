package com.example.accountbook.statistics.controller;

import com.example.accountbook.common.Result;
import com.example.accountbook.statistics.dto.MonthlySummary;
import com.example.accountbook.statistics.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books/{bookId}/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;
    public StatisticsController(StatisticsService statisticsService) { this.statisticsService = statisticsService; }

    @GetMapping("/monthly")
    public Result<MonthlySummary> monthly(@PathVariable Long bookId, @RequestParam String month) {
        return Result.success(statisticsService.monthly(bookId, YearMonth.parse(month)));
    }

    @GetMapping("/category")
    public Result<List<Map<String, Object>>> category(
            @PathVariable Long bookId,
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.category(bookId, type, startDate, endDate));
    }

    @GetMapping("/daily-trend")
    public Result<List<Map<String, Object>>> dailyTrend(
            @PathVariable Long bookId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.dailyTrend(bookId, startDate, endDate));
    }

    @GetMapping("/yearly")
    public Result<List<Map<String, Object>>> yearly(@PathVariable Long bookId, @RequestParam Integer year) {
        return Result.success(statisticsService.yearly(bookId, year));
    }
}
