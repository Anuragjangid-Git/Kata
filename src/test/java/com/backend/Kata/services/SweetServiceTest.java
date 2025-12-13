package com.backend.Kata.services;

import com.backend.Kata.dto.*;
import com.backend.Kata.entities.Sweet;
import com.backend.Kata.repository.SweetRepository;
import com.backend.Kata.services.impl.SweetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SweetServiceTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private SweetServiceImpl sweetService;

    private Sweet testSweet;

    @BeforeEach
    void setUp() {
        testSweet = new Sweet();
        testSweet.setId(1L);
        testSweet.setName("Chocolate Bar");
        testSweet.setCategory("Chocolate");
        testSweet.setPrice(new BigDecimal("2.50"));
        testSweet.setQuantity(100);
    }

    @Test
    void testCreateSweet() {
        // Given
        CreateSweetRequest request = new CreateSweetRequest();
        request.setName("Chocolate Bar");
        request.setCategory("Chocolate");
        request.setPrice(new BigDecimal("2.50"));
        request.setQuantity(100);

        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        SweetResponse response = sweetService.createSweet(request);

        // Then
        assertNotNull(response);
        assertEquals("Chocolate Bar", response.getName());
        assertEquals("Chocolate", response.getCategory());
        assertEquals(new BigDecimal("2.50"), response.getPrice());
        assertEquals(100, response.getQuantity());
        verify(sweetRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    void testGetAllSweets() {
        // Given
        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Candy");
        sweet2.setCategory("Candy");
        sweet2.setPrice(new BigDecimal("1.00"));
        sweet2.setQuantity(50);

        when(sweetRepository.findAll()).thenReturn(Arrays.asList(testSweet, sweet2));

        // When
        List<SweetResponse> responses = sweetService.getAllSweets();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(sweetRepository, times(1)).findAll();
    }

    @Test
    void testGetSweetById() {
        // Given
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When
        SweetResponse response = sweetService.getSweetById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Chocolate Bar", response.getName());
        verify(sweetRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSweetByIdNotFound() {
        // Given
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> sweetService.getSweetById(999L));
        verify(sweetRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateSweet() {
        // Given
        UpdateSweetRequest request = new UpdateSweetRequest();
        request.setName("Updated Chocolate");
        request.setPrice(new BigDecimal("3.00"));

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // When
        SweetResponse response = sweetService.updateSweet(1L, request);

        // Then
        assertNotNull(response);
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    void testDeleteSweet() {
        // Given
        when(sweetRepository.existsById(1L)).thenReturn(true);

        // When
        sweetService.deleteSweet(1L);

        // Then
        verify(sweetRepository, times(1)).existsById(1L);
        verify(sweetRepository, times(1)).deleteById(1L);
    }

    @Test
    void testPurchaseSweet() {
        // Given
        PurchaseRequest request = new PurchaseRequest();
        request.setQuantity(10);

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
            Sweet saved = invocation.getArgument(0);
            assertEquals(90, saved.getQuantity());
            return saved;
        });

        // When
        SweetResponse response = sweetService.purchaseSweet(1L, request);

        // Then
        assertNotNull(response);
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    void testPurchaseSweetInsufficientStock() {
        // Given
        PurchaseRequest request = new PurchaseRequest();
        request.setQuantity(150); // More than available

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // When & Then
        assertThrows(RuntimeException.class, () -> sweetService.purchaseSweet(1L, request));
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, never()).save(any(Sweet.class));
    }

    @Test
    void testRestockSweet() {
        // Given
        RestockRequest request = new RestockRequest();
        request.setQuantity(50);

        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenAnswer(invocation -> {
            Sweet saved = invocation.getArgument(0);
            assertEquals(150, saved.getQuantity());
            return saved;
        });

        // When
        SweetResponse response = sweetService.restockSweet(1L, request);

        // Then
        assertNotNull(response);
        verify(sweetRepository, times(1)).findById(1L);
        verify(sweetRepository, times(1)).save(any(Sweet.class));
    }

    @Test
    void testSearchSweets() {
        // Given
        SearchSweetRequest request = new SearchSweetRequest();
        request.setName("chocolate");
        request.setCategory("Chocolate");
        request.setMinPrice(new BigDecimal("1.00"));
        request.setMaxPrice(new BigDecimal("5.00"));

        when(sweetRepository.searchSweets(
                "chocolate", "Chocolate", 
                new BigDecimal("1.00"), 
                new BigDecimal("5.00")
        )).thenReturn(Arrays.asList(testSweet));

        // When
        List<SweetResponse> responses = sweetService.searchSweets(request);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(sweetRepository, times(1)).searchSweets(
                "chocolate", "Chocolate",
                new BigDecimal("1.00"),
                new BigDecimal("5.00")
        );
    }
}

