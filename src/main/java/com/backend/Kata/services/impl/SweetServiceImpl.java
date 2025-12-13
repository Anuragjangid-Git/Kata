package com.backend.Kata.services.impl;

import com.backend.Kata.dto.*;
import com.backend.Kata.entities.Sweet;
import com.backend.Kata.repository.SweetRepository;
import com.backend.Kata.services.SweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SweetServiceImpl implements SweetService {

    private final SweetRepository sweetRepository;

    @Override
    @Transactional
    public SweetResponse createSweet(CreateSweetRequest request) {
        Sweet sweet = new Sweet();
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());

        Sweet savedSweet = sweetRepository.save(sweet);
        return mapToResponse(savedSweet);
    }

    @Override
    public List<SweetResponse> getAllSweets() {
        return sweetRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SweetResponse> searchSweets(SearchSweetRequest request) {
        List<Sweet> sweets = sweetRepository.searchSweets(
                request.getName(),
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice()
        );
        return sweets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SweetResponse getSweetById(Long id) {
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));
        return mapToResponse(sweet);
    }

    @Override
    @Transactional
    public SweetResponse updateSweet(Long id, UpdateSweetRequest request) {
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));

        if (request.getName() != null) {
            sweet.setName(request.getName());
        }
        if (request.getCategory() != null) {
            sweet.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            sweet.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            sweet.setQuantity(request.getQuantity());
        }

        Sweet updatedSweet = sweetRepository.save(sweet);
        return mapToResponse(updatedSweet);
    }

    @Override
    @Transactional
    public void deleteSweet(Long id) {
        if (!sweetRepository.existsById(id)) {
            throw new RuntimeException("Sweet not found with id: " + id);
        }
        sweetRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SweetResponse purchaseSweet(Long id, PurchaseRequest request) {
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));

        int newQuantity = sweet.getQuantity() - request.getQuantity();
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock. Available: " + sweet.getQuantity() + ", Requested: " + request.getQuantity());
        }

        sweet.setQuantity(newQuantity);
        Sweet updatedSweet = sweetRepository.save(sweet);
        return mapToResponse(updatedSweet);
    }

    @Override
    @Transactional
    public SweetResponse restockSweet(Long id, RestockRequest request) {
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sweet not found with id: " + id));

        sweet.setQuantity(sweet.getQuantity() + request.getQuantity());
        Sweet updatedSweet = sweetRepository.save(sweet);
        return mapToResponse(updatedSweet);
    }

    private SweetResponse mapToResponse(Sweet sweet) {
        return new SweetResponse(
                sweet.getId(),
                sweet.getName(),
                sweet.getCategory(),
                sweet.getPrice(),
                sweet.getQuantity()
        );
    }
}

