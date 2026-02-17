package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.OrderMapper;
import com.drycleaning.system.model.Customer;
import com.drycleaning.system.model.Order;
import com.drycleaning.system.service.CustomerService;
import com.drycleaning.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CustomerService customerService;

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

    @Override
    public List<Order> fuzzySearch(String orderNo, String customerName, String clothesType) {
        // 第一步：使用 Mapper 进行订单号模糊查询
        List<Order> orders = orderMapper.fuzzySearch(orderNo);
        
        // 第二步：在内存中过滤客户姓名和衣物类型
        return orders.stream()
            .filter(order -> {
                // 过滤客户姓名
                if (customerName != null && !customerName.trim().isEmpty()) {
                    Optional<Customer> customerOpt = customerService.getCustomerById(order.getCustomerId());
                    if (customerOpt.isPresent()) {
                        String custName = customerOpt.get().getName();
                        if (custName == null || !custName.contains(customerName.trim())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                // 注意：衣物类型需要关联 clothes 表，这里简化处理
                // 如需完整实现，需要添加 ClothesMapper 并查询关联数据
                return true;
            })
            .collect(Collectors.toList());
    }
}
