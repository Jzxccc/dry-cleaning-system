package com.drycleaning.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
}
