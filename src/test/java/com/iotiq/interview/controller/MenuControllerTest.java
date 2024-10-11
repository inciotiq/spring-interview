package com.iotiq.interview.controller;

import com.iotiq.interview.controller.messages.CreateResponse;
import com.iotiq.interview.controller.messages.MenuRequest;
import com.iotiq.interview.controller.messages.MenuResponse;
import com.iotiq.interview.domain.Menu;
import com.iotiq.interview.domain.MenuFilter;
import com.iotiq.interview.exception.DuplicateMenuNameException;
import com.iotiq.interview.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        // Arrange
        Menu menu1 = new Menu();
        menu1.setName("Menu 1");
        Menu menu2 = new Menu();
        menu2.setName("Menu 2");
        List<Menu> menus = Arrays.asList(menu1, menu2);
        Page<Menu> menuPage = new PageImpl<>(menus, PageRequest.of(0, 10), menus.size());
        when(menuService.getFiltered(null, PageRequest.of(0, 10))).thenReturn(menuPage);

        // Act
        Page<MenuResponse> result = menuController.getAll(null, PageRequest.of(0, 10));

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("Menu 1", result.getContent().get(0).getName());
        assertEquals("Menu 2", result.getContent().get(1).getName());
        verify(menuService, times(1)).getFiltered(null, PageRequest.of(0, 10));
    }

    @Test
    void testCreate() {
        // Arrange
        MenuRequest request = new MenuRequest();
        request.setName("New Menu");
        Menu createdMenu = new Menu();
        setEntityId(createdMenu, UUID.randomUUID());
        createdMenu.setName("New Menu");
        when(menuService.create(any(MenuRequest.class))).thenReturn(createdMenu);

        // Act
        CreateResponse response = menuController.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(createdMenu.getId(), response.getId());
        verify(menuService, times(1)).create(any(MenuRequest.class));
    }

    @Test
    void testCreate_InvalidRequest() {
        // Arrange
        MenuRequest request = new MenuRequest();
        // Name is left empty
        when(menuService.create(any(MenuRequest.class)))
                .thenThrow(new IllegalArgumentException("Menu name cannot be null or empty"));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> menuController.create(request));
        assertEquals("Menu name cannot be null or empty", exception.getMessage());
        verify(menuService, times(1)).create(any(MenuRequest.class));
    }

    private void setEntityId(AbstractPersistable<UUID> entity, UUID id) {
        try {
            Field idField = AbstractPersistable.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetAllMenusWithFilter() {
        // Arrange
        MenuFilter filter = new MenuFilter();
        filter.setName("Italian");
        Pageable pageable = PageRequest.of(0, 10);
        List<Menu> menus = Collections.singletonList(new Menu());
        Page<Menu> menuPage = new PageImpl<>(menus, pageable, menus.size());
        when(menuService.getFiltered(any(MenuFilter.class), any(Pageable.class))).thenReturn(menuPage);

        // Act
        Page<MenuResponse> result = menuController.getAll(filter, pageable);

        // Assert
        assertEquals(1, result.getContent().size());
        verify(menuService, times(1)).getFiltered(any(MenuFilter.class), any(Pageable.class));
    }

    @Test
    void testCreate_DuplicateName() {
        // Arrange
        MenuRequest request = new MenuRequest();
        request.setName("Existing Menu");

        when(menuService.create(any(MenuRequest.class)))
                .thenThrow(new DuplicateMenuNameException("Menu with name 'Existing Menu' already exists"));

        // Act & Assert
        Exception exception = assertThrows(DuplicateMenuNameException.class, () -> menuController.create(request));
        assertEquals("Menu with name 'Existing Menu' already exists", exception.getMessage());
        verify(menuService, times(1)).create(any(MenuRequest.class));
    }

    @Test
    void testUpdate_DuplicateName() {
        // Arrange
        UUID menuId = UUID.randomUUID();
        MenuRequest request = new MenuRequest();
        request.setName("Existing Menu");

        when(menuService.update(eq(menuId), any(MenuRequest.class)))
                .thenThrow(new DuplicateMenuNameException("Menu with name 'Existing Menu' already exists"));

        // Act & Assert
        Exception exception = assertThrows(DuplicateMenuNameException.class,
                () -> menuController.update(menuId, request));
        assertEquals("Menu with name 'Existing Menu' already exists", exception.getMessage());
        verify(menuService, times(1)).update(eq(menuId), any(MenuRequest.class));
    }
}