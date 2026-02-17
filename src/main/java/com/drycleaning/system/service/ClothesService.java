package com.drycleaning.system.service;

import com.drycleaning.system.model.Clothes;

import java.util.List;
import java.util.Optional;

public interface ClothesService {
    List<Clothes> getAllClothes();
    Optional<Clothes> getClothesById(Long id);
    List<Clothes> getClothesByOrderId(String orderId);
    List<Clothes> getClothesByStatus(String status);
    Clothes createClothes(Clothes clothes);
    Clothes updateClothes(Long id, Clothes clothesDetails);
    void deleteClothes(Long id);
    Clothes updateClothesPrice(Long id, Double newPrice);
    Clothes updateClothesStatus(Long id, String newStatus);
}