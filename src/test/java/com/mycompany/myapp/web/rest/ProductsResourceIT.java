package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.repository.ProductsRepository;
import com.mycompany.myapp.service.criteria.ProductsCriteria;
import com.mycompany.myapp.service.dto.ProductsDTO;
import com.mycompany.myapp.service.mapper.ProductsMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_PRODUCT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCT_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_AVAILABLE_QTY = 1D;
    private static final Double UPDATED_AVAILABLE_QTY = 2D;
    private static final Double SMALLER_AVAILABLE_QTY = 1D - 1D;

    private static final Double DEFAULT_MANUFACTURING_COST = 1D;
    private static final Double UPDATED_MANUFACTURING_COST = 2D;
    private static final Double SMALLER_MANUFACTURING_COST = 1D - 1D;

    private static final Double DEFAULT_LABOUR_COST = 1D;
    private static final Double UPDATED_LABOUR_COST = 2D;
    private static final Double SMALLER_LABOUR_COST = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductsMockMvc;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products()
            .productName(DEFAULT_PRODUCT_NAME)
            .availableQty(DEFAULT_AVAILABLE_QTY)
            .manufacturingCost(DEFAULT_MANUFACTURING_COST)
            .labourCost(DEFAULT_LABOUR_COST);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products()
            .productName(UPDATED_PRODUCT_NAME)
            .availableQty(UPDATED_AVAILABLE_QTY)
            .manufacturingCost(UPDATED_MANUFACTURING_COST)
            .labourCost(UPDATED_LABOUR_COST);
        return products;
    }

    @BeforeEach
    public void initTest() {
        products = createEntity(em);
    }

    @Test
    @Transactional
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().size();
        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isCreated());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getProductName()).isEqualTo(DEFAULT_PRODUCT_NAME);
        assertThat(testProducts.getAvailableQty()).isEqualTo(DEFAULT_AVAILABLE_QTY);
        assertThat(testProducts.getManufacturingCost()).isEqualTo(DEFAULT_MANUFACTURING_COST);
        assertThat(testProducts.getLabourCost()).isEqualTo(DEFAULT_LABOUR_COST);
    }

    @Test
    @Transactional
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId(1L);
        ProductsDTO productsDTO = productsMapper.toDto(products);

        int databaseSizeBeforeCreate = productsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].availableQty").value(hasItem(DEFAULT_AVAILABLE_QTY.doubleValue())))
            .andExpect(jsonPath("$.[*].manufacturingCost").value(hasItem(DEFAULT_MANUFACTURING_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].labourCost").value(hasItem(DEFAULT_LABOUR_COST.doubleValue())));
    }

    @Test
    @Transactional
    void getProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get the products
        restProductsMockMvc
            .perform(get(ENTITY_API_URL_ID, products.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(products.getId().intValue()))
            .andExpect(jsonPath("$.productName").value(DEFAULT_PRODUCT_NAME))
            .andExpect(jsonPath("$.availableQty").value(DEFAULT_AVAILABLE_QTY.doubleValue()))
            .andExpect(jsonPath("$.manufacturingCost").value(DEFAULT_MANUFACTURING_COST.doubleValue()))
            .andExpect(jsonPath("$.labourCost").value(DEFAULT_LABOUR_COST.doubleValue()));
    }

    @Test
    @Transactional
    void getProductsByIdFiltering() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        Long id = products.getId();

        defaultProductsShouldBeFound("id.equals=" + id);
        defaultProductsShouldNotBeFound("id.notEquals=" + id);

        defaultProductsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.greaterThan=" + id);

        defaultProductsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductsByProductNameIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where productName equals to DEFAULT_PRODUCT_NAME
        defaultProductsShouldBeFound("productName.equals=" + DEFAULT_PRODUCT_NAME);

        // Get all the productsList where productName equals to UPDATED_PRODUCT_NAME
        defaultProductsShouldNotBeFound("productName.equals=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByProductNameIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where productName in DEFAULT_PRODUCT_NAME or UPDATED_PRODUCT_NAME
        defaultProductsShouldBeFound("productName.in=" + DEFAULT_PRODUCT_NAME + "," + UPDATED_PRODUCT_NAME);

        // Get all the productsList where productName equals to UPDATED_PRODUCT_NAME
        defaultProductsShouldNotBeFound("productName.in=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByProductNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where productName is not null
        defaultProductsShouldBeFound("productName.specified=true");

        // Get all the productsList where productName is null
        defaultProductsShouldNotBeFound("productName.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByProductNameContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where productName contains DEFAULT_PRODUCT_NAME
        defaultProductsShouldBeFound("productName.contains=" + DEFAULT_PRODUCT_NAME);

        // Get all the productsList where productName contains UPDATED_PRODUCT_NAME
        defaultProductsShouldNotBeFound("productName.contains=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByProductNameNotContainsSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where productName does not contain DEFAULT_PRODUCT_NAME
        defaultProductsShouldNotBeFound("productName.doesNotContain=" + DEFAULT_PRODUCT_NAME);

        // Get all the productsList where productName does not contain UPDATED_PRODUCT_NAME
        defaultProductsShouldBeFound("productName.doesNotContain=" + UPDATED_PRODUCT_NAME);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty equals to DEFAULT_AVAILABLE_QTY
        defaultProductsShouldBeFound("availableQty.equals=" + DEFAULT_AVAILABLE_QTY);

        // Get all the productsList where availableQty equals to UPDATED_AVAILABLE_QTY
        defaultProductsShouldNotBeFound("availableQty.equals=" + UPDATED_AVAILABLE_QTY);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty in DEFAULT_AVAILABLE_QTY or UPDATED_AVAILABLE_QTY
        defaultProductsShouldBeFound("availableQty.in=" + DEFAULT_AVAILABLE_QTY + "," + UPDATED_AVAILABLE_QTY);

        // Get all the productsList where availableQty equals to UPDATED_AVAILABLE_QTY
        defaultProductsShouldNotBeFound("availableQty.in=" + UPDATED_AVAILABLE_QTY);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty is not null
        defaultProductsShouldBeFound("availableQty.specified=true");

        // Get all the productsList where availableQty is null
        defaultProductsShouldNotBeFound("availableQty.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty is greater than or equal to DEFAULT_AVAILABLE_QTY
        defaultProductsShouldBeFound("availableQty.greaterThanOrEqual=" + DEFAULT_AVAILABLE_QTY);

        // Get all the productsList where availableQty is greater than or equal to UPDATED_AVAILABLE_QTY
        defaultProductsShouldNotBeFound("availableQty.greaterThanOrEqual=" + UPDATED_AVAILABLE_QTY);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty is less than or equal to DEFAULT_AVAILABLE_QTY
        defaultProductsShouldBeFound("availableQty.lessThanOrEqual=" + DEFAULT_AVAILABLE_QTY);

        // Get all the productsList where availableQty is less than or equal to SMALLER_AVAILABLE_QTY
        defaultProductsShouldNotBeFound("availableQty.lessThanOrEqual=" + SMALLER_AVAILABLE_QTY);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty is less than DEFAULT_AVAILABLE_QTY
        defaultProductsShouldNotBeFound("availableQty.lessThan=" + DEFAULT_AVAILABLE_QTY);

        // Get all the productsList where availableQty is less than UPDATED_AVAILABLE_QTY
        defaultProductsShouldBeFound("availableQty.lessThan=" + UPDATED_AVAILABLE_QTY);
    }

    @Test
    @Transactional
    void getAllProductsByAvailableQtyIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where availableQty is greater than DEFAULT_AVAILABLE_QTY
        defaultProductsShouldNotBeFound("availableQty.greaterThan=" + DEFAULT_AVAILABLE_QTY);

        // Get all the productsList where availableQty is greater than SMALLER_AVAILABLE_QTY
        defaultProductsShouldBeFound("availableQty.greaterThan=" + SMALLER_AVAILABLE_QTY);
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost equals to DEFAULT_MANUFACTURING_COST
        defaultProductsShouldBeFound("manufacturingCost.equals=" + DEFAULT_MANUFACTURING_COST);

        // Get all the productsList where manufacturingCost equals to UPDATED_MANUFACTURING_COST
        defaultProductsShouldNotBeFound("manufacturingCost.equals=" + UPDATED_MANUFACTURING_COST);
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost in DEFAULT_MANUFACTURING_COST or UPDATED_MANUFACTURING_COST
        defaultProductsShouldBeFound("manufacturingCost.in=" + DEFAULT_MANUFACTURING_COST + "," + UPDATED_MANUFACTURING_COST);

        // Get all the productsList where manufacturingCost equals to UPDATED_MANUFACTURING_COST
        defaultProductsShouldNotBeFound("manufacturingCost.in=" + UPDATED_MANUFACTURING_COST);
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost is not null
        defaultProductsShouldBeFound("manufacturingCost.specified=true");

        // Get all the productsList where manufacturingCost is null
        defaultProductsShouldNotBeFound("manufacturingCost.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost is greater than or equal to DEFAULT_MANUFACTURING_COST
        defaultProductsShouldBeFound("manufacturingCost.greaterThanOrEqual=" + DEFAULT_MANUFACTURING_COST);

        // Get all the productsList where manufacturingCost is greater than or equal to UPDATED_MANUFACTURING_COST
        defaultProductsShouldNotBeFound("manufacturingCost.greaterThanOrEqual=" + UPDATED_MANUFACTURING_COST);
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost is less than or equal to DEFAULT_MANUFACTURING_COST
        defaultProductsShouldBeFound("manufacturingCost.lessThanOrEqual=" + DEFAULT_MANUFACTURING_COST);

        // Get all the productsList where manufacturingCost is less than or equal to SMALLER_MANUFACTURING_COST
        defaultProductsShouldNotBeFound("manufacturingCost.lessThanOrEqual=" + SMALLER_MANUFACTURING_COST);
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost is less than DEFAULT_MANUFACTURING_COST
        defaultProductsShouldNotBeFound("manufacturingCost.lessThan=" + DEFAULT_MANUFACTURING_COST);

        // Get all the productsList where manufacturingCost is less than UPDATED_MANUFACTURING_COST
        defaultProductsShouldBeFound("manufacturingCost.lessThan=" + UPDATED_MANUFACTURING_COST);
    }

    @Test
    @Transactional
    void getAllProductsByManufacturingCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where manufacturingCost is greater than DEFAULT_MANUFACTURING_COST
        defaultProductsShouldNotBeFound("manufacturingCost.greaterThan=" + DEFAULT_MANUFACTURING_COST);

        // Get all the productsList where manufacturingCost is greater than SMALLER_MANUFACTURING_COST
        defaultProductsShouldBeFound("manufacturingCost.greaterThan=" + SMALLER_MANUFACTURING_COST);
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost equals to DEFAULT_LABOUR_COST
        defaultProductsShouldBeFound("labourCost.equals=" + DEFAULT_LABOUR_COST);

        // Get all the productsList where labourCost equals to UPDATED_LABOUR_COST
        defaultProductsShouldNotBeFound("labourCost.equals=" + UPDATED_LABOUR_COST);
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsInShouldWork() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost in DEFAULT_LABOUR_COST or UPDATED_LABOUR_COST
        defaultProductsShouldBeFound("labourCost.in=" + DEFAULT_LABOUR_COST + "," + UPDATED_LABOUR_COST);

        // Get all the productsList where labourCost equals to UPDATED_LABOUR_COST
        defaultProductsShouldNotBeFound("labourCost.in=" + UPDATED_LABOUR_COST);
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost is not null
        defaultProductsShouldBeFound("labourCost.specified=true");

        // Get all the productsList where labourCost is null
        defaultProductsShouldNotBeFound("labourCost.specified=false");
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost is greater than or equal to DEFAULT_LABOUR_COST
        defaultProductsShouldBeFound("labourCost.greaterThanOrEqual=" + DEFAULT_LABOUR_COST);

        // Get all the productsList where labourCost is greater than or equal to UPDATED_LABOUR_COST
        defaultProductsShouldNotBeFound("labourCost.greaterThanOrEqual=" + UPDATED_LABOUR_COST);
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost is less than or equal to DEFAULT_LABOUR_COST
        defaultProductsShouldBeFound("labourCost.lessThanOrEqual=" + DEFAULT_LABOUR_COST);

        // Get all the productsList where labourCost is less than or equal to SMALLER_LABOUR_COST
        defaultProductsShouldNotBeFound("labourCost.lessThanOrEqual=" + SMALLER_LABOUR_COST);
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsLessThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost is less than DEFAULT_LABOUR_COST
        defaultProductsShouldNotBeFound("labourCost.lessThan=" + DEFAULT_LABOUR_COST);

        // Get all the productsList where labourCost is less than UPDATED_LABOUR_COST
        defaultProductsShouldBeFound("labourCost.lessThan=" + UPDATED_LABOUR_COST);
    }

    @Test
    @Transactional
    void getAllProductsByLabourCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        // Get all the productsList where labourCost is greater than DEFAULT_LABOUR_COST
        defaultProductsShouldNotBeFound("labourCost.greaterThan=" + DEFAULT_LABOUR_COST);

        // Get all the productsList where labourCost is greater than SMALLER_LABOUR_COST
        defaultProductsShouldBeFound("labourCost.greaterThan=" + SMALLER_LABOUR_COST);
    }

    @Test
    @Transactional
    void getAllProductsByRawMaterialIsEqualToSomething() throws Exception {
        RawMaterial rawMaterial;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            productsRepository.saveAndFlush(products);
            rawMaterial = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterial = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterial);
        em.flush();
        products.addRawMaterial(rawMaterial);
        productsRepository.saveAndFlush(products);
        Long rawMaterialId = rawMaterial.getId();

        // Get all the productsList where rawMaterial equals to rawMaterialId
        defaultProductsShouldBeFound("rawMaterialId.equals=" + rawMaterialId);

        // Get all the productsList where rawMaterial equals to (rawMaterialId + 1)
        defaultProductsShouldNotBeFound("rawMaterialId.equals=" + (rawMaterialId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductsShouldBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(products.getId().intValue())))
            .andExpect(jsonPath("$.[*].productName").value(hasItem(DEFAULT_PRODUCT_NAME)))
            .andExpect(jsonPath("$.[*].availableQty").value(hasItem(DEFAULT_AVAILABLE_QTY.doubleValue())))
            .andExpect(jsonPath("$.[*].manufacturingCost").value(hasItem(DEFAULT_MANUFACTURING_COST.doubleValue())))
            .andExpect(jsonPath("$.[*].labourCost").value(hasItem(DEFAULT_LABOUR_COST.doubleValue())));

        // Check, that the count call also returns 1
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductsShouldNotBeFound(String filter) throws Exception {
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProducts() throws Exception {
        // Get the products
        restProductsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).get();
        // Disconnect from session so that the updates on updatedProducts are not directly saved in db
        em.detach(updatedProducts);
        updatedProducts
            .productName(UPDATED_PRODUCT_NAME)
            .availableQty(UPDATED_AVAILABLE_QTY)
            .manufacturingCost(UPDATED_MANUFACTURING_COST)
            .labourCost(UPDATED_LABOUR_COST);
        ProductsDTO productsDTO = productsMapper.toDto(updatedProducts);

        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testProducts.getAvailableQty()).isEqualTo(UPDATED_AVAILABLE_QTY);
        assertThat(testProducts.getManufacturingCost()).isEqualTo(UPDATED_MANUFACTURING_COST);
        assertThat(testProducts.getLabourCost()).isEqualTo(UPDATED_LABOUR_COST);
    }

    @Test
    @Transactional
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.productName(UPDATED_PRODUCT_NAME).manufacturingCost(UPDATED_MANUFACTURING_COST);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testProducts.getAvailableQty()).isEqualTo(DEFAULT_AVAILABLE_QTY);
        assertThat(testProducts.getManufacturingCost()).isEqualTo(UPDATED_MANUFACTURING_COST);
        assertThat(testProducts.getLabourCost()).isEqualTo(DEFAULT_LABOUR_COST);
    }

    @Test
    @Transactional
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeUpdate = productsRepository.findAll().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts
            .productName(UPDATED_PRODUCT_NAME)
            .availableQty(UPDATED_AVAILABLE_QTY)
            .manufacturingCost(UPDATED_MANUFACTURING_COST)
            .labourCost(UPDATED_LABOUR_COST);

        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            )
            .andExpect(status().isOk());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getProductName()).isEqualTo(UPDATED_PRODUCT_NAME);
        assertThat(testProducts.getAvailableQty()).isEqualTo(UPDATED_AVAILABLE_QTY);
        assertThat(testProducts.getManufacturingCost()).isEqualTo(UPDATED_MANUFACTURING_COST);
        assertThat(testProducts.getLabourCost()).isEqualTo(UPDATED_LABOUR_COST);
    }

    @Test
    @Transactional
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(productsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProducts() throws Exception {
        // Initialize the database
        productsRepository.saveAndFlush(products);

        int databaseSizeBeforeDelete = productsRepository.findAll().size();

        // Delete the products
        restProductsMockMvc
            .perform(delete(ENTITY_API_URL_ID, products.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
