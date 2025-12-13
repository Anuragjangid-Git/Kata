package com.backend.Kata.services;

import com.backend.Kata.dto.*;

import java.util.List;

public interface SweetService {
    SweetResponse createSweet(CreateSweetRequest request);
    List<SweetResponse> getAllSweets();
    List<SweetResponse> searchSweets(SearchSweetRequest request);
    SweetResponse getSweetById(Long id);
    SweetResponse updateSweet(Long id, UpdateSweetRequest request);
    void deleteSweet(Long id);
    SweetResponse purchaseSweet(Long id, PurchaseRequest request);
    SweetResponse restockSweet(Long id, RestockRequest request);
}

