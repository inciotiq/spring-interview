package com.iotiq.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iotiq.interview.controller.messages.ProductRequest;
import com.iotiq.interview.domain.Category;
import com.iotiq.interview.domain.Menu;
import com.iotiq.interview.domain.Product;
import com.iotiq.interview.domain.ProductCategory;
import com.iotiq.interview.repository.CategoryRepository;
import com.iotiq.interview.repository.MenuRepository;
import com.iotiq.interview.repository.ProductCategoryRepository;
import com.iotiq.interview.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productCategoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void testCreateProduct() throws Exception {
        // Arrange
        Menu menu = new Menu();
        menu.setName("Test Menu");
        menu = menuRepository.save(menu);

        Category category = new Category();
        category.setName("Test Category");
        category.setMenu(menu);
        category = categoryRepository.save(category);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Test Product");
        Set<ProductRequest.ProductCategoryRequest> productCategories = new HashSet<>();
        ProductRequest.ProductCategoryRequest pcr = new ProductRequest.ProductCategoryRequest();
        pcr.setCategoryId(category.getId());
        pcr.setPrice(BigDecimal.valueOf(10.99));
        productCategories.add(pcr);
        productRequest.setProductCategories(productCategories);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testGetAllProductsWithFilter() throws Exception {
        // Arrange
        Menu menu = new Menu();
        menu.setName("Test Menu");
        menu = menuRepository.save(menu);

        Category category = new Category();
        category.setName("Test Category");
        category.setMenu(menu);
        category = categoryRepository.save(category);

        Product product1 = new Product();
        product1.setName("Italian Pizza");
        product1 = productRepository.save(product1);

        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setProduct(product1);
        productCategory1.setCategory(category);
        productCategory1.setPrice(BigDecimal.valueOf(10.99));
        productCategoryRepository.save(productCategory1);

        Product product2 = new Product();
        product2.setName("French Fries");
        product2 = productRepository.save(product2);

        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setProduct(product2);
        productCategory2.setCategory(category);
        productCategory2.setPrice(BigDecimal.valueOf(5.99));
        productCategoryRepository.save(productCategory2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products?name=Italian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Italian Pizza")))
                .andExpect(jsonPath("$[0].price", is(10.99)));
    }

    @Test
    void testCreateProductWithDuplicateNameInCategory() throws Exception {
        // Arrange
        Menu menu = new Menu();
        menu.setName("Test Menu");
        menu = menuRepository.save(menu);

        Category category = new Category();
        category.setName("Yemek");
        category.setMenu(menu);
        category = categoryRepository.save(category);

        // Create first product
        Product product1 = new Product();
        product1.setName("Tavuk");
        product1 = productRepository.save(product1);

        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setProduct(product1);
        productCategory1.setCategory(category);
        productCategory1.setPrice(BigDecimal.valueOf(29.21));
        productCategoryRepository.save(productCategory1);

        // Attempt to create second product with the same name
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Tavuk");
        Set<ProductRequest.ProductCategoryRequest> productCategories = new HashSet<>();
        ProductRequest.ProductCategoryRequest pcr = new ProductRequest.ProductCategoryRequest();
        pcr.setCategoryId(category.getId());
        pcr.setPrice(BigDecimal.valueOf(21.21));
        productCategories.add(pcr);
        productRequest.setProductCategories(productCategories);

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Product with name 'Tavuk' already exists in the category")));

    }
}