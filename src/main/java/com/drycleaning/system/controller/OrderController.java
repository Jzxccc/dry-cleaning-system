package com.drycleaning.system.controller;

import com.drycleaning.system.model.Customer;
import com.drycleaning.system.model.Order;
import com.drycleaning.system.service.CustomerService;
import com.drycleaning.system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        // 后端验证：如果是储值支付，检查余额
        if ("PREPAID".equals(order.getPayType())) {
            Optional<Customer> customerOpt = customerService.getCustomerById(order.getCustomerId());
            if (!customerOpt.isPresent()) {
                return ResponseEntity.badRequest().body("客户不存在");
            }
            
            Customer customer = customerOpt.get();
            if (customer.getBalance() < order.getTotalPrice()) {
                return ResponseEntity.badRequest()
                    .body("储值余额不足！当前余额：¥" + customer.getBalance() + "，订单金额：¥" + order.getTotalPrice());
            }
            
            // 扣除余额
            Double newBalance = customer.getBalance() - order.getTotalPrice();
            customerService.updateCustomerBalance(order.getCustomerId(), newBalance);
        }
        
        // 验证必填字段
        if (order.getOrderNo() == null || order.getOrderNo().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("订单号不能为空");
        }
        
        if (order.getCustomerId() == null) {
            return ResponseEntity.badRequest().body("客户 ID 不能为空");
        }
        
        if (order.getTotalPrice() == null || order.getTotalPrice() <= 0) {
            return ResponseEntity.badRequest().body("订单金额必须大于 0");
        }
        
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/name/{customerName}")
    public ResponseEntity<List<Order>> getOrdersByCustomerName(@PathVariable String customerName) {
        List<Order> orders = orderService.getOrdersByCustomerName(customerName);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam String newStatus) {
        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * 模糊搜索订单 - 支持订单号、客户姓名、衣物类型的多条件组合查询
     * @param orderNo 订单号关键词（可选）
     * @param customerName 客户姓名关键词（可选）
     * @param clothesType 衣物类型关键词（可选）
     * @return 匹配的订单列表
     */
    @GetMapping("/search/fuzzy")
    public ResponseEntity<List<Order>> fuzzySearchOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String clothesType) {
        List<Order> orders = orderService.fuzzySearch(orderNo, customerName, clothesType);
        return ResponseEntity.ok(orders);
    }
}