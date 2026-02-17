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
}