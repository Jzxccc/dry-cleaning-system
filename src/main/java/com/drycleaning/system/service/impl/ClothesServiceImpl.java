package com.drycleaning.system.service.impl;

import com.drycleaning.system.mapper.ClothesMapper;
import com.drycleaning.system.model.Clothes;
import com.drycleaning.system.service.ClothesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClothesServiceImpl implements ClothesService {

    @Autowired
    private ClothesMapper clothesMapper;

    @Override
    public List<Clothes> getAllClothes() {
        return clothesMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>());
    }

    @Override
    public Optional<Clothes> getClothesById(Long id) {
        return Optional.ofNullable(clothesMapper.selectById(id));
    }

    @Override
    public List<Clothes> getClothesByOrderId(Long orderId) {
        return clothesMapper.findByOrderId(orderId);
    }

    @Override
    public List<Clothes> getClothesByStatus(String status) {
        return clothesMapper.findByStatus(status);
    }

    @Override
    public Clothes createClothes(Clothes clothes) {
        clothes.setCreateTime(java.time.LocalDateTime.now().toString());
        clothesMapper.insert(clothes);
        return clothes;
    }

    @Override
    public Clothes updateClothes(Long id, Clothes clothesDetails) {
        Clothes clothes = clothesMapper.selectById(id);
        if (clothes == null) {
            throw new RuntimeException("Clothes not found with id: " + id);
        }

        clothes.setOrderId(clothesDetails.getOrderId());
        clothes.setType(clothesDetails.getType());
        clothes.setPrice(clothesDetails.getPrice());
        clothes.setDamageRemark(clothesDetails.getDamageRemark());
        clothes.setDamageImage(clothesDetails.getDamageImage());
        clothes.setStatus(clothesDetails.getStatus());

        clothesMapper.updateById(clothes);
        return clothes;
    }

    @Override
    public void deleteClothes(Long id) {
        clothesMapper.deleteById(id);
    }

    @Override
    public Clothes updateClothesPrice(Long id, Double newPrice) {
        Clothes clothes = clothesMapper.selectById(id);
        if (clothes == null) {
            throw new RuntimeException("Clothes not found with id: " + id);
        }

        clothes.setPrice(newPrice);
        clothesMapper.updateById(clothes);
        return clothes;
    }

    @Override
    public Clothes updateClothesStatus(Long id, String newStatus) {
        Clothes clothes = clothesMapper.selectById(id);
        if (clothes == null) {
            throw new RuntimeException("Clothes not found with id: " + id);
        }

        clothes.setStatus(newStatus);
        clothesMapper.updateById(clothes);
        return clothes;
    }
}
