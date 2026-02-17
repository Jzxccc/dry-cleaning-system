package com.drycleaning.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.drycleaning.system.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM orders WHERE customer_id = #{customer_id}")
    List<Order> findByCustomerId(@Param("customer_id") Long customerId);

    @Select("SELECT * FROM orders WHERE customer_name LIKE CONCAT('%', #{customerName}, '%')")
    List<Order> findByCustomerNameContaining(@Param("customerName") String customerName);

    @Select("SELECT * FROM orders WHERE status = #{status}")
    List<Order> findByStatus(@Param("status") String status);

    /**
     * 模糊搜索订单 - 支持订单号的多条件组合查询
     * 注意：客户姓名和衣物类型的过滤在 Service 层进行内存过滤
     * @param orderNo 订单号关键词（可选）
     * @return 匹配的订单列表（仅按订单号过滤）
     */
    default List<Order> fuzzySearch(@Param("orderNo") String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            queryWrapper.like("order_no", orderNo.trim());
        }
        
        return this.selectList(queryWrapper);
    }
}
