package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.OrderMapper;
import com.drycleaning.system.model.Order;
import com.drycleaning.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>());
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return Optional.ofNullable(orderMapper.selectById(id));
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderMapper.findByCustomerId(customerId);
    }

    @Override
    public List<Order> getOrdersByCustomerName(String customerName) {
        return orderMapper.findByCustomerNameContaining(customerName);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderMapper.findByStatus(status);
    }

    @Override
    public Order createOrder(Order order) {
        order.setCreateTime(java.time.LocalDateTime.now().toString());
        orderMapper.insert(order);
        return order;
    }

    @Override
    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + id);
        }

        order.setOrderNo(orderDetails.getOrderNo());
        order.setCustomerId(orderDetails.getCustomerId());
        order.setTotalPrice(orderDetails.getTotalPrice());
        order.setPrepaid(orderDetails.getPrepaid());
        order.setPayType(orderDetails.getPayType());
        order.setUrgent(orderDetails.getUrgent());
        order.setStatus(orderDetails.getStatus());
        order.setExpectedTime(orderDetails.getExpectedTime());

        orderMapper.updateById(order);
        return order;
    }

    @Override
    public void deleteOrder(Long id) {
        orderMapper.deleteById(id);
    }

    @Override
    public Order updateOrderStatus(Long id, String newStatus) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("Order not found with id: " + id);
        }

        order.setStatus(newStatus);
        orderMapper.updateById(order);
        return order;
    }
}
