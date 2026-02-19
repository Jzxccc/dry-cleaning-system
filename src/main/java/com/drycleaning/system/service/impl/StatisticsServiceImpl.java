package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.OrderMapper;
import com.drycleaning.system.mapper.RechargeRecordMapper;
import com.drycleaning.system.model.Order;
import com.drycleaning.system.model.RechargeRecord;
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

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public Double getDailyIncome(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Order> orders = orderMapper.selectList(null);
        // 今日收入 = 现金收入 + 储值充值（客户存的钱）
        // 不包括储值支付的订单（因为用的是余额）
        Double cashIncome = orders.stream()
                .filter(order -> order.getCreateTime() != null)
                .filter(order -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(order.getCreateTime());
                        return createTime.isAfter(startOfDay) && createTime.isBefore(endOfDay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                // 只统计现金支付
                .filter(order -> "CASH".equalsIgnoreCase(order.getPayType()))
                .mapToDouble(Order::getTotalPrice)
                .sum();
        
        // 加上当天的充值金额
        Double rechargeAmount = getPrepaidIncome(date);
        
        return cashIncome + rechargeAmount;
    }

    @Override
    public Double getMonthlyIncome(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        LocalDateTime startOfMonth = startDate.atStartOfDay();
        LocalDateTime endOfMonth = endDate.atStartOfDay();

        List<Order> orders = orderMapper.selectList(null);
        // 月度收入 = 现金收入 + 储值充值
        Double cashIncome = orders.stream()
                .filter(order -> order.getCreateTime() != null)
                .filter(order -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(order.getCreateTime());
                        return createTime.isAfter(startOfMonth) && createTime.isBefore(endOfMonth);
                    } catch (Exception e) {
                        return false;
                    }
                })
                // 只统计现金支付
                .filter(order -> "CASH".equalsIgnoreCase(order.getPayType()))
                .mapToDouble(Order::getTotalPrice)
                .sum();
        
        // 加上当月充值金额
        List<RechargeRecord> records = rechargeRecordMapper.selectList(null);
        Double rechargeAmount = records.stream()
                .filter(record -> record.getCreateTime() != null)
                .filter(record -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(record.getCreateTime());
                        return createTime.isAfter(startOfMonth) && createTime.isBefore(endOfMonth);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(RechargeRecord::getRechargeAmount)
                .sum();
        
        return cashIncome + rechargeAmount;
    }

    @Override
    public Double getCashIncome(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Order> orders = orderMapper.selectList(null);
        // 现金收入 = 当天现金支付的订单金额
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

        // 储值收入 = 当天客户充值的金额（从充值记录表统计）
        List<RechargeRecord> records = rechargeRecordMapper.selectList(null);
        return records.stream()
                .filter(record -> record.getCreateTime() != null)
                .filter(record -> {
                    try {
                        LocalDateTime createTime = LocalDateTime.parse(record.getCreateTime());
                        return createTime.isAfter(startOfDay) && createTime.isBefore(endOfDay);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .mapToDouble(RechargeRecord::getRechargeAmount)
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
    public Long getTodayOrderCount(LocalDate date) {
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
