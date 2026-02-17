package com.drycleaning.system.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatisticsService {
    Double getDailyIncome(LocalDate date);
    Double getMonthlyIncome(int year, int month);
    Double getCashIncome(LocalDate date);
    Double getPrepaidIncome(LocalDate date);
    Long getUnfinishedOrderCount();
    Map<String, Object> getDailyStatistics(LocalDate date);
    Map<String, Object> getMonthlyStatistics(int year, int month);
}