package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.OrderMapper;
import com.drycleaning.system.model.Order;
import com.drycleaning.system.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Double getDailyIncome(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        List<Order> orders = orderMapper.selectList(null);
        return orders.stream()
                .filter(order -> order.getCreateTime() != null)
                .filter(order -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(order.getCreateTime());
                        return createTime.isAfter(startOfDay) && createTime.isBefore(endOfDay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    @Override
    public Double getMonthlyIncome(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);
        
        LocalDateTime startOfMonth = startDate.atStartOfDay();
        LocalDateTime endOfMonth = endDate.atStartOfDay();
        
        List<Order> orders = orderMapper.selectList(null);
        return orders.stream()
                .filter(order -> order.getCreateTime() != null)
                .filter(order -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(order.getCreateTime());
                        return createTime.isAfter(startOfMonth) && createTime.isBefore(endOfMonth);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    @Override
    public Double getCashIncome(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        List<Order> orders = orderMapper.selectList(null);
        return orders.stream()
                .filter(order -> "CASH".equalsIgnoreCase(order.getPayType()))
                .filter(order -> order.getCreateTime() != null)
                .filter(order -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(order.getCreateTime());
                        return createTime.isAfter(startOfDay) && createTime.isBefore(endOfDay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    @Override
    public Double getPrepaidIncome(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        List<Order> orders = orderMapper.selectList(null);
        return orders.stream()
                .filter(order -> "PREPAID".equalsIgnoreCase(order.getPayType()))
                .filter(order -> order.getCreateTime() != null)
                .filter(order -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(order.getCreateTime());
                        return createTime.isAfter(startOfDay) && createTime.isBefore(endOfDay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(Order::getTotalPrice)
                .sum();
    }

    @Override
    public Long getUnfinishedOrderCount() {
        List<Order> orders = orderMapper.selectList(null);
        return orders.stream()
                .filter(order -> !"FINISHED".equals(order.getStatus()))
                .count();
    }

    @Override
    public Map<String, Object> getDailyStatistics(LocalDate date) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("dailyIncome", getDailyIncome(date));
        stats.put("cashIncome", getCashIncome(date));
        stats.put("prepaidIncome", getPrepaidIncome(date));
        stats.put("date", date);
        return stats;
    }

    @Override
    public Map<String, Object> getMonthlyStatistics(int year, int month) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("monthlyIncome", getMonthlyIncome(year, month));
        stats.put("year", year);
        stats.put("month", month);
        return stats;
    }
}
