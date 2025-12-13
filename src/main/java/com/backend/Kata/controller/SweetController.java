package com.backend.Kata.controller;

import com.backend.Kata.dto.*;
import com.backend.Kata.services.SweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sweets")
@RequiredArgsConstructor
public class SweetController {

    private final SweetService sweetService;

    @PostMapping
    public ResponseEntity<SweetResponse> createSweet(@Valid @RequestBody CreateSweetRequest request) {
        SweetResponse response = sweetService.createSweet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SweetResponse>> getAllSweets() {
        List<SweetResponse> sweets = sweetService.getAllSweets();
        return ResponseEntity.ok(sweets);
    }

    @GetMapping("/search")
    public ResponseEntity<List<SweetResponse>> searchSweets(@Valid @ModelAttribute SearchSweetRequest request) {
        List<SweetResponse> sweets = sweetService.searchSweets(request);
        return ResponseEntity.ok(sweets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SweetResponse> getSweetById(@PathVariable Long id) {
        SweetResponse sweet = sweetService.getSweetById(id);
        return ResponseEntity.ok(sweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SweetResponse> updateSweet(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSweetRequest request) {
        SweetResponse response = sweetService.updateSweet(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<SweetResponse> purchaseSweet(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequest request) {
        SweetResponse response = sweetService.purchaseSweet(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<SweetResponse> restockSweet(
            @PathVariable Long id,
            @Valid @RequestBody RestockRequest request) {
        SweetResponse response = sweetService.restockSweet(id, request);
        return ResponseEntity.ok(response);
    }
}

