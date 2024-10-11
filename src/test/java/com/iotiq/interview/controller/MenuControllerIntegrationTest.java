package com.iotiq.interview.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iotiq.interview.controller.messages.MenuRequest;
import com.iotiq.interview.domain.Menu;
import com.iotiq.interview.repository.MenuRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        menuRepository.deleteAll();
    }

    @Test
    void testGetAllMenusWithFilter() throws Exception {
        // Arrange
        Menu menu1 = new Menu();
        menu1.setName("Italian Menu");
        Menu menu2 = new Menu();
        menu2.setName("French Menu");
        menuRepository.saveAll(Arrays.asList(menu1, menu2));
    
        // Act & Assert
        mockMvc.perform(get("/api/v1/menus?name=Italian"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)))
            .andExpect(jsonPath("$.content[0].name", is("Italian Menu")))
            .andExpect(jsonPath("$.totalElements", is(1)))
            .andExpect(jsonPath("$.totalPages", is(1)));
    }
    

    @Test
    void testCreateMenuWithDuplicateName() throws Exception {
        // Arrange
        Menu existingMenu = new Menu();
        existingMenu.setName("Existing Menu");
        menuRepository.save(existingMenu);

        MenuRequest newMenu = new MenuRequest();
        newMenu.setName("Existing Menu");

        // Act & Assert
        mockMvc.perform(post("/api/v1/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(newMenu)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAllMenusWithFilterPaginated() throws Exception {
        // Arrange
        Menu menu1 = new Menu();
        menu1.setName("Italian Menu");
        Menu menu2 = new Menu();
        menu2.setName("French Menu");
        menuRepository.saveAll(Arrays.asList(menu1, menu2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/menus?name=Italian&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Italian Menu")))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }
}