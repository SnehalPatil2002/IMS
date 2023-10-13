package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.StockRequest;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.StockRequestRepository;
import com.mycompany.myapp.service.criteria.StockRequestCriteria;
import com.mycompany.myapp.service.dto.StockRequestDTO;
import com.mycompany.myapp.service.mapper.StockRequestMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link StockRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockRequestResourceIT {

    private static final Double DEFAULT_QTY_REQUIRED = 1D;
    private static final Double UPDATED_QTY_REQUIRED = 2D;
    private static final Double SMALLER_QTY_REQUIRED = 1D - 1D;

    private static final Instant DEFAULT_REQ_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQ_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_PROD = false;
    private static final Boolean UPDATED_IS_PROD = true;

    private static final Status DEFAULT_STATUS = Status.REQUESTED;
    private static final Status UPDATED_STATUS = Status.APPROVED;

    private static final String ENTITY_API_URL = "/api/stock-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StockRequestRepository stockRequestRepository;

    @Autowired
    private StockRequestMapper stockRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockRequestMockMvc;

    private StockRequest stockRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockRequest createEntity(EntityManager em) {
        StockRequest stockRequest = new StockRequest()
            .qtyRequired(DEFAULT_QTY_REQUIRED)
            .reqDate(DEFAULT_REQ_DATE)
            .isProd(DEFAULT_IS_PROD)
            .status(DEFAULT_STATUS);
        return stockRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockRequest createUpdatedEntity(EntityManager em) {
        StockRequest stockRequest = new StockRequest()
            .qtyRequired(UPDATED_QTY_REQUIRED)
            .reqDate(UPDATED_REQ_DATE)
            .isProd(UPDATED_IS_PROD)
            .status(UPDATED_STATUS);
        return stockRequest;
    }

    @BeforeEach
    public void initTest() {
        stockRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createStockRequest() throws Exception {
        int databaseSizeBeforeCreate = stockRequestRepository.findAll().size();
        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);
        restStockRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isCreated());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeCreate + 1);
        StockRequest testStockRequest = stockRequestList.get(stockRequestList.size() - 1);
        assertThat(testStockRequest.getQtyRequired()).isEqualTo(DEFAULT_QTY_REQUIRED);
        assertThat(testStockRequest.getReqDate()).isEqualTo(DEFAULT_REQ_DATE);
        assertThat(testStockRequest.getIsProd()).isEqualTo(DEFAULT_IS_PROD);
        assertThat(testStockRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createStockRequestWithExistingId() throws Exception {
        // Create the StockRequest with an existing ID
        stockRequest.setId(1L);
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        int databaseSizeBeforeCreate = stockRequestRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStockRequests() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList
        restStockRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].qtyRequired").value(hasItem(DEFAULT_QTY_REQUIRED.doubleValue())))
            .andExpect(jsonPath("$.[*].reqDate").value(hasItem(DEFAULT_REQ_DATE.toString())))
            .andExpect(jsonPath("$.[*].isProd").value(hasItem(DEFAULT_IS_PROD.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getStockRequest() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get the stockRequest
        restStockRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, stockRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockRequest.getId().intValue()))
            .andExpect(jsonPath("$.qtyRequired").value(DEFAULT_QTY_REQUIRED.doubleValue()))
            .andExpect(jsonPath("$.reqDate").value(DEFAULT_REQ_DATE.toString()))
            .andExpect(jsonPath("$.isProd").value(DEFAULT_IS_PROD.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getStockRequestsByIdFiltering() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        Long id = stockRequest.getId();

        defaultStockRequestShouldBeFound("id.equals=" + id);
        defaultStockRequestShouldNotBeFound("id.notEquals=" + id);

        defaultStockRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStockRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultStockRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStockRequestShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired equals to DEFAULT_QTY_REQUIRED
        defaultStockRequestShouldBeFound("qtyRequired.equals=" + DEFAULT_QTY_REQUIRED);

        // Get all the stockRequestList where qtyRequired equals to UPDATED_QTY_REQUIRED
        defaultStockRequestShouldNotBeFound("qtyRequired.equals=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsInShouldWork() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired in DEFAULT_QTY_REQUIRED or UPDATED_QTY_REQUIRED
        defaultStockRequestShouldBeFound("qtyRequired.in=" + DEFAULT_QTY_REQUIRED + "," + UPDATED_QTY_REQUIRED);

        // Get all the stockRequestList where qtyRequired equals to UPDATED_QTY_REQUIRED
        defaultStockRequestShouldNotBeFound("qtyRequired.in=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired is not null
        defaultStockRequestShouldBeFound("qtyRequired.specified=true");

        // Get all the stockRequestList where qtyRequired is null
        defaultStockRequestShouldNotBeFound("qtyRequired.specified=false");
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired is greater than or equal to DEFAULT_QTY_REQUIRED
        defaultStockRequestShouldBeFound("qtyRequired.greaterThanOrEqual=" + DEFAULT_QTY_REQUIRED);

        // Get all the stockRequestList where qtyRequired is greater than or equal to UPDATED_QTY_REQUIRED
        defaultStockRequestShouldNotBeFound("qtyRequired.greaterThanOrEqual=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired is less than or equal to DEFAULT_QTY_REQUIRED
        defaultStockRequestShouldBeFound("qtyRequired.lessThanOrEqual=" + DEFAULT_QTY_REQUIRED);

        // Get all the stockRequestList where qtyRequired is less than or equal to SMALLER_QTY_REQUIRED
        defaultStockRequestShouldNotBeFound("qtyRequired.lessThanOrEqual=" + SMALLER_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsLessThanSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired is less than DEFAULT_QTY_REQUIRED
        defaultStockRequestShouldNotBeFound("qtyRequired.lessThan=" + DEFAULT_QTY_REQUIRED);

        // Get all the stockRequestList where qtyRequired is less than UPDATED_QTY_REQUIRED
        defaultStockRequestShouldBeFound("qtyRequired.lessThan=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllStockRequestsByQtyRequiredIsGreaterThanSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where qtyRequired is greater than DEFAULT_QTY_REQUIRED
        defaultStockRequestShouldNotBeFound("qtyRequired.greaterThan=" + DEFAULT_QTY_REQUIRED);

        // Get all the stockRequestList where qtyRequired is greater than SMALLER_QTY_REQUIRED
        defaultStockRequestShouldBeFound("qtyRequired.greaterThan=" + SMALLER_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllStockRequestsByReqDateIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where reqDate equals to DEFAULT_REQ_DATE
        defaultStockRequestShouldBeFound("reqDate.equals=" + DEFAULT_REQ_DATE);

        // Get all the stockRequestList where reqDate equals to UPDATED_REQ_DATE
        defaultStockRequestShouldNotBeFound("reqDate.equals=" + UPDATED_REQ_DATE);
    }

    @Test
    @Transactional
    void getAllStockRequestsByReqDateIsInShouldWork() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where reqDate in DEFAULT_REQ_DATE or UPDATED_REQ_DATE
        defaultStockRequestShouldBeFound("reqDate.in=" + DEFAULT_REQ_DATE + "," + UPDATED_REQ_DATE);

        // Get all the stockRequestList where reqDate equals to UPDATED_REQ_DATE
        defaultStockRequestShouldNotBeFound("reqDate.in=" + UPDATED_REQ_DATE);
    }

    @Test
    @Transactional
    void getAllStockRequestsByReqDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where reqDate is not null
        defaultStockRequestShouldBeFound("reqDate.specified=true");

        // Get all the stockRequestList where reqDate is null
        defaultStockRequestShouldNotBeFound("reqDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockRequestsByIsProdIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where isProd equals to DEFAULT_IS_PROD
        defaultStockRequestShouldBeFound("isProd.equals=" + DEFAULT_IS_PROD);

        // Get all the stockRequestList where isProd equals to UPDATED_IS_PROD
        defaultStockRequestShouldNotBeFound("isProd.equals=" + UPDATED_IS_PROD);
    }

    @Test
    @Transactional
    void getAllStockRequestsByIsProdIsInShouldWork() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where isProd in DEFAULT_IS_PROD or UPDATED_IS_PROD
        defaultStockRequestShouldBeFound("isProd.in=" + DEFAULT_IS_PROD + "," + UPDATED_IS_PROD);

        // Get all the stockRequestList where isProd equals to UPDATED_IS_PROD
        defaultStockRequestShouldNotBeFound("isProd.in=" + UPDATED_IS_PROD);
    }

    @Test
    @Transactional
    void getAllStockRequestsByIsProdIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where isProd is not null
        defaultStockRequestShouldBeFound("isProd.specified=true");

        // Get all the stockRequestList where isProd is null
        defaultStockRequestShouldNotBeFound("isProd.specified=false");
    }

    @Test
    @Transactional
    void getAllStockRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where status equals to DEFAULT_STATUS
        defaultStockRequestShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the stockRequestList where status equals to UPDATED_STATUS
        defaultStockRequestShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultStockRequestShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the stockRequestList where status equals to UPDATED_STATUS
        defaultStockRequestShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        // Get all the stockRequestList where status is not null
        defaultStockRequestShouldBeFound("status.specified=true");

        // Get all the stockRequestList where status is null
        defaultStockRequestShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllStockRequestsByRawMaterialIsEqualToSomething() throws Exception {
        RawMaterial rawMaterial;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            stockRequestRepository.saveAndFlush(stockRequest);
            rawMaterial = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterial = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterial);
        em.flush();
        stockRequest.setRawMaterial(rawMaterial);
        stockRequestRepository.saveAndFlush(stockRequest);
        Long rawMaterialId = rawMaterial.getId();

        // Get all the stockRequestList where rawMaterial equals to rawMaterialId
        defaultStockRequestShouldBeFound("rawMaterialId.equals=" + rawMaterialId);

        // Get all the stockRequestList where rawMaterial equals to (rawMaterialId + 1)
        defaultStockRequestShouldNotBeFound("rawMaterialId.equals=" + (rawMaterialId + 1));
    }

    @Test
    @Transactional
    void getAllStockRequestsByProductIsEqualToSomething() throws Exception {
        Products product;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            stockRequestRepository.saveAndFlush(stockRequest);
            product = ProductsResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(product);
        em.flush();
        stockRequest.setProduct(product);
        stockRequestRepository.saveAndFlush(stockRequest);
        Long productId = product.getId();

        // Get all the stockRequestList where product equals to productId
        defaultStockRequestShouldBeFound("productId.equals=" + productId);

        // Get all the stockRequestList where product equals to (productId + 1)
        defaultStockRequestShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    @Test
    @Transactional
    void getAllStockRequestsByProductsIsEqualToSomething() throws Exception {
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            stockRequestRepository.saveAndFlush(stockRequest);
            products = ProductsResourceIT.createEntity(em);
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(products);
        em.flush();
        stockRequest.setProducts(products);
        stockRequestRepository.saveAndFlush(stockRequest);
        Long productsId = products.getId();

        // Get all the stockRequestList where products equals to productsId
        defaultStockRequestShouldBeFound("productsId.equals=" + productsId);

        // Get all the stockRequestList where products equals to (productsId + 1)
        defaultStockRequestShouldNotBeFound("productsId.equals=" + (productsId + 1));
    }

    @Test
    @Transactional
    void getAllStockRequestsByProductionLineIsEqualToSomething() throws Exception {
        ProductionLine productionLine;
        if (TestUtil.findAll(em, ProductionLine.class).isEmpty()) {
            stockRequestRepository.saveAndFlush(stockRequest);
            productionLine = ProductionLineResourceIT.createEntity(em);
        } else {
            productionLine = TestUtil.findAll(em, ProductionLine.class).get(0);
        }
        em.persist(productionLine);
        em.flush();
        stockRequest.setProductionLine(productionLine);
        stockRequestRepository.saveAndFlush(stockRequest);
        Long productionLineId = productionLine.getId();

        // Get all the stockRequestList where productionLine equals to productionLineId
        defaultStockRequestShouldBeFound("productionLineId.equals=" + productionLineId);

        // Get all the stockRequestList where productionLine equals to (productionLineId + 1)
        defaultStockRequestShouldNotBeFound("productionLineId.equals=" + (productionLineId + 1));
    }

    @Test
    @Transactional
    void getAllStockRequestsByProjectsIsEqualToSomething() throws Exception {
        Projects projects;
        if (TestUtil.findAll(em, Projects.class).isEmpty()) {
            stockRequestRepository.saveAndFlush(stockRequest);
            projects = ProjectsResourceIT.createEntity(em);
        } else {
            projects = TestUtil.findAll(em, Projects.class).get(0);
        }
        em.persist(projects);
        em.flush();
        stockRequest.setProjects(projects);
        stockRequestRepository.saveAndFlush(stockRequest);
        Long projectsId = projects.getId();

        // Get all the stockRequestList where projects equals to projectsId
        defaultStockRequestShouldBeFound("projectsId.equals=" + projectsId);

        // Get all the stockRequestList where projects equals to (projectsId + 1)
        defaultStockRequestShouldNotBeFound("projectsId.equals=" + (projectsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockRequestShouldBeFound(String filter) throws Exception {
        restStockRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].qtyRequired").value(hasItem(DEFAULT_QTY_REQUIRED.doubleValue())))
            .andExpect(jsonPath("$.[*].reqDate").value(hasItem(DEFAULT_REQ_DATE.toString())))
            .andExpect(jsonPath("$.[*].isProd").value(hasItem(DEFAULT_IS_PROD.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restStockRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockRequestShouldNotBeFound(String filter) throws Exception {
        restStockRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockRequest() throws Exception {
        // Get the stockRequest
        restStockRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockRequest() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();

        // Update the stockRequest
        StockRequest updatedStockRequest = stockRequestRepository.findById(stockRequest.getId()).get();
        // Disconnect from session so that the updates on updatedStockRequest are not directly saved in db
        em.detach(updatedStockRequest);
        updatedStockRequest.qtyRequired(UPDATED_QTY_REQUIRED).reqDate(UPDATED_REQ_DATE).isProd(UPDATED_IS_PROD).status(UPDATED_STATUS);
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(updatedStockRequest);

        restStockRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
        StockRequest testStockRequest = stockRequestList.get(stockRequestList.size() - 1);
        assertThat(testStockRequest.getQtyRequired()).isEqualTo(UPDATED_QTY_REQUIRED);
        assertThat(testStockRequest.getReqDate()).isEqualTo(UPDATED_REQ_DATE);
        assertThat(testStockRequest.getIsProd()).isEqualTo(UPDATED_IS_PROD);
        assertThat(testStockRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingStockRequest() throws Exception {
        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();
        stockRequest.setId(count.incrementAndGet());

        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockRequest() throws Exception {
        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();
        stockRequest.setId(count.incrementAndGet());

        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockRequest() throws Exception {
        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();
        stockRequest.setId(count.incrementAndGet());

        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRequestMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockRequestWithPatch() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();

        // Update the stockRequest using partial update
        StockRequest partialUpdatedStockRequest = new StockRequest();
        partialUpdatedStockRequest.setId(stockRequest.getId());

        partialUpdatedStockRequest.isProd(UPDATED_IS_PROD);

        restStockRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockRequest))
            )
            .andExpect(status().isOk());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
        StockRequest testStockRequest = stockRequestList.get(stockRequestList.size() - 1);
        assertThat(testStockRequest.getQtyRequired()).isEqualTo(DEFAULT_QTY_REQUIRED);
        assertThat(testStockRequest.getReqDate()).isEqualTo(DEFAULT_REQ_DATE);
        assertThat(testStockRequest.getIsProd()).isEqualTo(UPDATED_IS_PROD);
        assertThat(testStockRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateStockRequestWithPatch() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();

        // Update the stockRequest using partial update
        StockRequest partialUpdatedStockRequest = new StockRequest();
        partialUpdatedStockRequest.setId(stockRequest.getId());

        partialUpdatedStockRequest
            .qtyRequired(UPDATED_QTY_REQUIRED)
            .reqDate(UPDATED_REQ_DATE)
            .isProd(UPDATED_IS_PROD)
            .status(UPDATED_STATUS);

        restStockRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStockRequest))
            )
            .andExpect(status().isOk());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
        StockRequest testStockRequest = stockRequestList.get(stockRequestList.size() - 1);
        assertThat(testStockRequest.getQtyRequired()).isEqualTo(UPDATED_QTY_REQUIRED);
        assertThat(testStockRequest.getReqDate()).isEqualTo(UPDATED_REQ_DATE);
        assertThat(testStockRequest.getIsProd()).isEqualTo(UPDATED_IS_PROD);
        assertThat(testStockRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingStockRequest() throws Exception {
        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();
        stockRequest.setId(count.incrementAndGet());

        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockRequest() throws Exception {
        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();
        stockRequest.setId(count.incrementAndGet());

        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockRequest() throws Exception {
        int databaseSizeBeforeUpdate = stockRequestRepository.findAll().size();
        stockRequest.setId(count.incrementAndGet());

        // Create the StockRequest
        StockRequestDTO stockRequestDTO = stockRequestMapper.toDto(stockRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stockRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockRequest in the database
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockRequest() throws Exception {
        // Initialize the database
        stockRequestRepository.saveAndFlush(stockRequest);

        int databaseSizeBeforeDelete = stockRequestRepository.findAll().size();

        // Delete the stockRequest
        restStockRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<StockRequest> stockRequestList = stockRequestRepository.findAll();
        assertThat(stockRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
