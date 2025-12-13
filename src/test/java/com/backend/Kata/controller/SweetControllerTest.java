package com.backend.Kata.controller;

import com.backend.Kata.dto.*;
import com.backend.Kata.services.SweetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SweetController.class)
class SweetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SweetService sweetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateSweet() throws Exception {
        CreateSweetRequest request = new CreateSweetRequest();
        request.setName("Chocolate Bar");
        request.setCategory("Chocolate");
        request.setPrice(new BigDecimal("2.50"));
        request.setQuantity(100);

        SweetResponse response = new SweetResponse(1L, "Chocolate Bar", "Chocolate", 
                new BigDecimal("2.50"), 100);

        when(sweetService.createSweet(any(CreateSweetRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/sweets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Chocolate Bar"))
                .andExpect(jsonPath("$.category").value("Chocolate"));

        verify(sweetService, times(1)).createSweet(any(CreateSweetRequest.class));
    }

    @Test
    @WithMockUser
    void testGetAllSweets() throws Exception {
        SweetResponse sweet1 = new SweetResponse(1L, "Chocolate Bar", "Chocolate", 
                new BigDecimal("2.50"), 100);
        SweetResponse sweet2 = new SweetResponse(2L, "Candy", "Candy", 
                new BigDecimal("1.00"), 50);

        when(sweetService.getAllSweets()).thenReturn(Arrays.asList(sweet1, sweet2));

        mockMvc.perform(get("/api/sweets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(sweetService, times(1)).getAllSweets();
    }

    @Test
    @WithMockUser
    void testGetSweetById() throws Exception {
        SweetResponse response = new SweetResponse(1L, "Chocolate Bar", "Chocolate", 
                new BigDecimal("2.50"), 100);

        when(sweetService.getSweetById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/sweets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chocolate Bar"));

        verify(sweetService, times(1)).getSweetById(1L);
    }

    @Test
    @WithMockUser
    void testUpdateSweet() throws Exception {
        UpdateSweetRequest request = new UpdateSweetRequest();
        request.setName("Updated Chocolate");
        request.setPrice(new BigDecimal("3.00"));

        SweetResponse response = new SweetResponse(1L, "Updated Chocolate", "Chocolate", 
                new BigDecimal("3.00"), 100);

        when(sweetService.updateSweet(eq(1L), any(UpdateSweetRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/sweets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Chocolate"));

        verify(sweetService, times(1)).updateSweet(eq(1L), any(UpdateSweetRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteSweet() throws Exception {
        doNothing().when(sweetService).deleteSweet(1L);

        mockMvc.perform(delete("/api/sweets/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(sweetService, times(1)).deleteSweet(1L);
    }

    @Test
    @WithMockUser
    void testPurchaseSweet() throws Exception {
        PurchaseRequest request = new PurchaseRequest();
        request.setQuantity(10);

        SweetResponse response = new SweetResponse(1L, "Chocolate Bar", "Chocolate", 
                new BigDecimal("2.50"), 90);

        when(sweetService.purchaseSweet(eq(1L), any(PurchaseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/sweets/1/purchase")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(90));

        verify(sweetService, times(1)).purchaseSweet(eq(1L), any(PurchaseRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRestockSweet() throws Exception {
        RestockRequest request = new RestockRequest();
        request.setQuantity(50);

        SweetResponse response = new SweetResponse(1L, "Chocolate Bar", "Chocolate", 
                new BigDecimal("2.50"), 150);

        when(sweetService.restockSweet(eq(1L), any(RestockRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/sweets/1/restock")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(150));

        verify(sweetService, times(1)).restockSweet(eq(1L), any(RestockRequest.class));
    }
}

