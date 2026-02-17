package com.drycleaning.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    /**
     * 模糊搜索客户 - 支持姓名、手机号、备注的多条件组合查询
     * @param name 姓名关键词（可选）
     * @param phone 手机号关键词（可选）
     * @param note 备注关键词（可选）
     * @return 匹配的客户列表
     */
    default List<Customer> fuzzySearch(@Param("name") String name,
                                       @Param("phone") String phone,
                                       @Param("note") String note) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("name", name.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            queryWrapper.like("phone", phone.trim());
        }
        if (note != null && !note.trim().isEmpty()) {
            queryWrapper.like("note", note.trim());
        }
        
        return this.selectList(queryWrapper);
    }
}
