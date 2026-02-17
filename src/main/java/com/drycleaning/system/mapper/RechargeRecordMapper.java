package com.drycleaning.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drycleaning.system.model.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

    @Select("SELECT * FROM recharge_record WHERE customer_id = #{customer_id}")
    List<RechargeRecord> findByCustomerId(@Param("customer_id") Long customerId);
}
