package com.drycleaning.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drycleaning.system.model.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    @Select("SELECT * FROM customer WHERE phone = #{phone}")
    Customer findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM customer WHERE name = #{name}")
    Customer findByName(@Param("name") String name);

    @Select("SELECT * FROM customer WHERE name LIKE '%' || #{name} || '%'")
    List<Customer> findByNameContaining(@Param("name") String name);
}
