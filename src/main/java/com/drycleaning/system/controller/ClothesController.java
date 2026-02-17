package com.drycleaning.system.controller;

import com.drycleaning.system.model.Clothes;
import com.drycleaning.system.service.ClothesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clothes")
public class ClothesController {

    @Autowired
    private ClothesService clothesService;

    @GetMapping
    public ResponseEntity<List<Clothes>> getAllClothes() {
        List<Clothes> clothes = clothesService.getAllClothes();
        return ResponseEntity.ok(clothes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clothes> getClothesById(@PathVariable Long id) {
        Optional<Clothes> clothes = clothesService.getClothesById(id);
        return clothes.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Clothes> createClothes(@RequestBody Clothes clothes) {
        Clothes createdClothes = clothesService.createClothes(clothes);
        return ResponseEntity.ok(createdClothes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clothes> updateClothes(@PathVariable Long id, @RequestBody Clothes clothesDetails) {
        Clothes updatedClothes = clothesService.updateClothes(id, clothesDetails);
        return ResponseEntity.ok(updatedClothes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClothes(@PathVariable Long id) {
        clothesService.deleteClothes(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Clothes>> getClothesByOrderId(@PathVariable String orderId) {
        List<Clothes> clothes = clothesService.getClothesByOrderId(orderId);
        return ResponseEntity.ok(clothes);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Clothes>> getClothesByStatus(@PathVariable String status) {
        List<Clothes> clothes = clothesService.getClothesByStatus(status);
        return ResponseEntity.ok(clothes);
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<Clothes> updateClothesPrice(@PathVariable Long id, @RequestParam Double newPrice) {
        Clothes updatedClothes = clothesService.updateClothesPrice(id, newPrice);
        return ResponseEntity.ok(updatedClothes);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Clothes> updateClothesStatus(@PathVariable Long id, @RequestParam String newStatus) {
        Clothes updatedClothes = clothesService.updateClothesStatus(id, newStatus);
        return ResponseEntity.ok(updatedClothes);
    }
}