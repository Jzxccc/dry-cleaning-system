package com.drycleaning.system.service;

import com.drycleaning.system.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    List<Order> getOrdersByCustomerId(Long customerId);
    List<Order> getOrdersByCustomerName(String customerName);
    List<Order> getOrdersByStatus(String status);
    Order createOrder(Order order);
    Order updateOrder(Long id, Order orderDetails);
    void deleteOrder(Long id);
    Order updateOrderStatus(Long id, String newStatus);
    
    /**
     * 模糊搜索订单
     * @param orderNo 订单号关键词（可选）
     * @param customerName 客户姓名关键词（可选）
     * @param clothesType 衣物类型关键词（可选）
     * @return 匹配的订单列表
     */
    List<Order> fuzzySearch(String orderNo, String customerName, String clothesType);
}