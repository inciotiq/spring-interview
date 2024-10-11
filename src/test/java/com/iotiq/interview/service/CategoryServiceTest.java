package com.iotiq.interview.service;

import com.iotiq.interview.controller.messages.CategoryRequest;
import com.iotiq.interview.domain.Category;
import com.iotiq.interview.domain.CategoryFilter;
import com.iotiq.interview.domain.Menu;
import com.iotiq.interview.domain.Product;
import com.iotiq.interview.domain.ProductCategory;
import com.iotiq.interview.exception.MenuNotFoundException;
import com.iotiq.interview.repository.CategoryRepository;
import com.iotiq.interview.repository.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        // Arrange
        UUID menuId = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest();
        request.setName("Test Category");
        request.setMenuId(menuId);

        Menu menu = new Menu();
        menu.setName("Test Menu");
        setEntityId(menu, menuId);

        Category category = new Category();
        category.setName("Test Category");
        category.setMenu(menu);
        setEntityId(category, UUID.randomUUID());

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        assertEquals(menu, result.getMenu());
        verify(menuRepository, times(1)).findById(menuId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_MenuNotFound() {
        // Arrange
        UUID menuId = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest();
        request.setName("Test Category");
        request.setMenuId(menuId);

        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MenuNotFoundException.class, () -> categoryService.create(request));
        verify(menuRepository, times(1)).findById(menuId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testGetAllCategories() {
        // Arrange
        Category category1 = new Category();
        category1.setName("Category 1");
        setEntityId(category1, UUID.randomUUID());

        Category category2 = new Category();
        category2.setName("Category 2");
        setEntityId(category2, UUID.randomUUID());

        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetFilteredCategories() {
        // Arrange
        Category category1 = new Category();
        category1.setName("Desserts");
        Category category2 = new Category();
        category2.setName("Main Course");

        CategoryFilter filter = new CategoryFilter();
        filter.setName("Desserts");

        when(categoryRepository.findAll(any(Specification.class)))
                .thenReturn(Collections.singletonList(category1));

        // Act
        List<Category> result = categoryService.getFiltered(filter);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Desserts", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll(any(Specification.class));
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
}
