package com.drycleaning.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drycleaning.system.model.Clothes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClothesMapper extends BaseMapper<Clothes> {

    @Select("SELECT * FROM clothes WHERE order_id = #{order_id}")
    List<Clothes> findByOrderId(@Param("order_id") String orderId);

    @Select("SELECT * FROM clothes WHERE status = #{status}")
    List<Clothes> findByStatus(@Param("status") String status);
}
