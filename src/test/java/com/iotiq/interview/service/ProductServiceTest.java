package com.iotiq.interview.service;

import com.iotiq.interview.controller.messages.ProductRequest;
import com.iotiq.interview.domain.Category;
import com.iotiq.interview.domain.Product;
import com.iotiq.interview.domain.ProductCategory;
import com.iotiq.interview.repository.CategoryRepository;
import com.iotiq.interview.repository.ProductCategoryRepository;
import com.iotiq.interview.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        Set<ProductRequest.ProductCategoryRequest> productCategories = new HashSet<>();
        ProductRequest.ProductCategoryRequest pcr = new ProductRequest.ProductCategoryRequest();
        pcr.setCategoryId(UUID.randomUUID());
        pcr.setPrice(BigDecimal.valueOf(10.99));
        productCategories.add(pcr);
        request.setProductCategories(productCategories);

        Category category = new Category();
        setEntityId(category, pcr.getCategoryId());

        Product product = new Product();
        product.setName("Test Product");
        setEntityId(product, UUID.randomUUID());

        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(new ProductCategory());

        // Act
        Product result = productService.create(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productCategoryRepository, times(1)).save(any(ProductCategory.class));
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(
                new Product(),
                new Product());
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAll();

        // Assert
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetFilteredProducts() {
        // Arrange
        String name = "Test";
        List<Product> filteredProducts = Collections.singletonList(new Product());
        when(productRepository.findAllByNameContainingIgnoreCase(name)).thenReturn(filteredProducts);

        // Act
        List<Product> result = productService.getFiltered(name);

        // Assert
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAllByNameContainingIgnoreCase(name);
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");
        Set<ProductRequest.ProductCategoryRequest> productCategories = new HashSet<>();
        ProductRequest.ProductCategoryRequest pcr = new ProductRequest.ProductCategoryRequest();
        pcr.setCategoryId(UUID.randomUUID());
        pcr.setPrice(BigDecimal.valueOf(15.99));
        productCategories.add(pcr);
        request.setProductCategories(productCategories);

        Product existingProduct = new Product();
        setEntityId(existingProduct, productId);
        existingProduct.setName("Original Product");

        Category category = new Category();
        setEntityId(category, pcr.getCategoryId());

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        Product result = productService.update(productId, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
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
    void testCreateProductWithDuplicateNameInCategory() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setName("Tavuk");
        Set<ProductRequest.ProductCategoryRequest> productCategories = new HashSet<>();
        ProductRequest.ProductCategoryRequest pcr = new ProductRequest.ProductCategoryRequest();
        UUID categoryId = UUID.randomUUID();
        pcr.setCategoryId(categoryId);
        pcr.setPrice(BigDecimal.valueOf(21.21));
        productCategories.add(pcr);
        request.setProductCategories(productCategories);

        Category category = new Category();
        setEntityId(category, categoryId);

        Product existingProduct = new Product();
        existingProduct.setName("Tavuk");

        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));
        when(productRepository.findByNameAndProductCategories_Category_Id("Tavuk", categoryId)).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.create(request));
        verify(productRepository, never()).save(any(Product.class));
    }

}
