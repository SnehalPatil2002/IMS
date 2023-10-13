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
import com.mycompany.myapp.domain.RawMaterialConsumption;
import com.mycompany.myapp.repository.RawMaterialConsumptionRepository;
import com.mycompany.myapp.service.criteria.RawMaterialConsumptionCriteria;
import com.mycompany.myapp.service.dto.RawMaterialConsumptionDTO;
import com.mycompany.myapp.service.mapper.RawMaterialConsumptionMapper;
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
 * Integration tests for the {@link RawMaterialConsumptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RawMaterialConsumptionResourceIT {

    private static final Double DEFAULT_QUANTITY_CONSUMED = 1D;
    private static final Double UPDATED_QUANTITY_CONSUMED = 2D;
    private static final Double SMALLER_QUANTITY_CONSUMED = 1D - 1D;

    private static final Double DEFAULT_SCRAP_GENERATED = 1D;
    private static final Double UPDATED_SCRAP_GENERATED = 2D;
    private static final Double SMALLER_SCRAP_GENERATED = 1D - 1D;

    private static final Double DEFAULT_TOTAL_MATERIAL_COST = 1D;
    private static final Double UPDATED_TOTAL_MATERIAL_COST = 2D;
    private static final Double SMALLER_TOTAL_MATERIAL_COST = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/raw-material-consumptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RawMaterialConsumptionRepository rawMaterialConsumptionRepository;

    @Autowired
    private RawMaterialConsumptionMapper rawMaterialConsumptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRawMaterialConsumptionMockMvc;

    private RawMaterialConsumption rawMaterialConsumption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMaterialConsumption createEntity(EntityManager em) {
        RawMaterialConsumption rawMaterialConsumption = new RawMaterialConsumption()
            .quantityConsumed(DEFAULT_QUANTITY_CONSUMED)
            .scrapGenerated(DEFAULT_SCRAP_GENERATED)
            .totalMaterialCost(DEFAULT_TOTAL_MATERIAL_COST);
        return rawMaterialConsumption;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMaterialConsumption createUpdatedEntity(EntityManager em) {
        RawMaterialConsumption rawMaterialConsumption = new RawMaterialConsumption()
            .quantityConsumed(UPDATED_QUANTITY_CONSUMED)
            .scrapGenerated(UPDATED_SCRAP_GENERATED)
            .totalMaterialCost(UPDATED_TOTAL_MATERIAL_COST);
        return rawMaterialConsumption;
    }

    @BeforeEach
    public void initTest() {
        rawMaterialConsumption = createEntity(em);
    }

    @Test
    @Transactional
    void createRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeCreate = rawMaterialConsumptionRepository.findAll().size();
        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);
        restRawMaterialConsumptionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeCreate + 1);
        RawMaterialConsumption testRawMaterialConsumption = rawMaterialConsumptionList.get(rawMaterialConsumptionList.size() - 1);
        assertThat(testRawMaterialConsumption.getQuantityConsumed()).isEqualTo(DEFAULT_QUANTITY_CONSUMED);
        assertThat(testRawMaterialConsumption.getScrapGenerated()).isEqualTo(DEFAULT_SCRAP_GENERATED);
        assertThat(testRawMaterialConsumption.getTotalMaterialCost()).isEqualTo(DEFAULT_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void createRawMaterialConsumptionWithExistingId() throws Exception {
        // Create the RawMaterialConsumption with an existing ID
        rawMaterialConsumption.setId(1L);
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        int databaseSizeBeforeCreate = rawMaterialConsumptionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRawMaterialConsumptionMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptions() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList
        restRawMaterialConsumptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterialConsumption.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantityConsumed").value(hasItem(DEFAULT_QUANTITY_CONSUMED.doubleValue())))
            .andExpect(jsonPath("$.[*].scrapGenerated").value(hasItem(DEFAULT_SCRAP_GENERATED.doubleValue())))
            .andExpect(jsonPath("$.[*].totalMaterialCost").value(hasItem(DEFAULT_TOTAL_MATERIAL_COST.doubleValue())));
    }

    @Test
    @Transactional
    void getRawMaterialConsumption() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get the rawMaterialConsumption
        restRawMaterialConsumptionMockMvc
            .perform(get(ENTITY_API_URL_ID, rawMaterialConsumption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rawMaterialConsumption.getId().intValue()))
            .andExpect(jsonPath("$.quantityConsumed").value(DEFAULT_QUANTITY_CONSUMED.doubleValue()))
            .andExpect(jsonPath("$.scrapGenerated").value(DEFAULT_SCRAP_GENERATED.doubleValue()))
            .andExpect(jsonPath("$.totalMaterialCost").value(DEFAULT_TOTAL_MATERIAL_COST.doubleValue()));
    }

    @Test
    @Transactional
    void getRawMaterialConsumptionsByIdFiltering() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        Long id = rawMaterialConsumption.getId();

        defaultRawMaterialConsumptionShouldBeFound("id.equals=" + id);
        defaultRawMaterialConsumptionShouldNotBeFound("id.notEquals=" + id);

        defaultRawMaterialConsumptionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRawMaterialConsumptionShouldNotBeFound("id.greaterThan=" + id);

        defaultRawMaterialConsumptionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRawMaterialConsumptionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed equals to DEFAULT_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.equals=" + DEFAULT_QUANTITY_CONSUMED);

        // Get all the rawMaterialConsumptionList where quantityConsumed equals to UPDATED_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.equals=" + UPDATED_QUANTITY_CONSUMED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed in DEFAULT_QUANTITY_CONSUMED or UPDATED_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.in=" + DEFAULT_QUANTITY_CONSUMED + "," + UPDATED_QUANTITY_CONSUMED);

        // Get all the rawMaterialConsumptionList where quantityConsumed equals to UPDATED_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.in=" + UPDATED_QUANTITY_CONSUMED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed is not null
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.specified=true");

        // Get all the rawMaterialConsumptionList where quantityConsumed is null
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed is greater than or equal to DEFAULT_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.greaterThanOrEqual=" + DEFAULT_QUANTITY_CONSUMED);

        // Get all the rawMaterialConsumptionList where quantityConsumed is greater than or equal to UPDATED_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.greaterThanOrEqual=" + UPDATED_QUANTITY_CONSUMED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed is less than or equal to DEFAULT_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.lessThanOrEqual=" + DEFAULT_QUANTITY_CONSUMED);

        // Get all the rawMaterialConsumptionList where quantityConsumed is less than or equal to SMALLER_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.lessThanOrEqual=" + SMALLER_QUANTITY_CONSUMED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed is less than DEFAULT_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.lessThan=" + DEFAULT_QUANTITY_CONSUMED);

        // Get all the rawMaterialConsumptionList where quantityConsumed is less than UPDATED_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.lessThan=" + UPDATED_QUANTITY_CONSUMED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByQuantityConsumedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where quantityConsumed is greater than DEFAULT_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldNotBeFound("quantityConsumed.greaterThan=" + DEFAULT_QUANTITY_CONSUMED);

        // Get all the rawMaterialConsumptionList where quantityConsumed is greater than SMALLER_QUANTITY_CONSUMED
        defaultRawMaterialConsumptionShouldBeFound("quantityConsumed.greaterThan=" + SMALLER_QUANTITY_CONSUMED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated equals to DEFAULT_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.equals=" + DEFAULT_SCRAP_GENERATED);

        // Get all the rawMaterialConsumptionList where scrapGenerated equals to UPDATED_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.equals=" + UPDATED_SCRAP_GENERATED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated in DEFAULT_SCRAP_GENERATED or UPDATED_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.in=" + DEFAULT_SCRAP_GENERATED + "," + UPDATED_SCRAP_GENERATED);

        // Get all the rawMaterialConsumptionList where scrapGenerated equals to UPDATED_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.in=" + UPDATED_SCRAP_GENERATED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated is not null
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.specified=true");

        // Get all the rawMaterialConsumptionList where scrapGenerated is null
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated is greater than or equal to DEFAULT_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.greaterThanOrEqual=" + DEFAULT_SCRAP_GENERATED);

        // Get all the rawMaterialConsumptionList where scrapGenerated is greater than or equal to UPDATED_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.greaterThanOrEqual=" + UPDATED_SCRAP_GENERATED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated is less than or equal to DEFAULT_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.lessThanOrEqual=" + DEFAULT_SCRAP_GENERATED);

        // Get all the rawMaterialConsumptionList where scrapGenerated is less than or equal to SMALLER_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.lessThanOrEqual=" + SMALLER_SCRAP_GENERATED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated is less than DEFAULT_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.lessThan=" + DEFAULT_SCRAP_GENERATED);

        // Get all the rawMaterialConsumptionList where scrapGenerated is less than UPDATED_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.lessThan=" + UPDATED_SCRAP_GENERATED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByScrapGeneratedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where scrapGenerated is greater than DEFAULT_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldNotBeFound("scrapGenerated.greaterThan=" + DEFAULT_SCRAP_GENERATED);

        // Get all the rawMaterialConsumptionList where scrapGenerated is greater than SMALLER_SCRAP_GENERATED
        defaultRawMaterialConsumptionShouldBeFound("scrapGenerated.greaterThan=" + SMALLER_SCRAP_GENERATED);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost equals to DEFAULT_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldBeFound("totalMaterialCost.equals=" + DEFAULT_TOTAL_MATERIAL_COST);

        // Get all the rawMaterialConsumptionList where totalMaterialCost equals to UPDATED_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.equals=" + UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost in DEFAULT_TOTAL_MATERIAL_COST or UPDATED_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldBeFound(
            "totalMaterialCost.in=" + DEFAULT_TOTAL_MATERIAL_COST + "," + UPDATED_TOTAL_MATERIAL_COST
        );

        // Get all the rawMaterialConsumptionList where totalMaterialCost equals to UPDATED_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.in=" + UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is not null
        defaultRawMaterialConsumptionShouldBeFound("totalMaterialCost.specified=true");

        // Get all the rawMaterialConsumptionList where totalMaterialCost is null
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is greater than or equal to DEFAULT_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldBeFound("totalMaterialCost.greaterThanOrEqual=" + DEFAULT_TOTAL_MATERIAL_COST);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is greater than or equal to UPDATED_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.greaterThanOrEqual=" + UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is less than or equal to DEFAULT_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldBeFound("totalMaterialCost.lessThanOrEqual=" + DEFAULT_TOTAL_MATERIAL_COST);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is less than or equal to SMALLER_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.lessThanOrEqual=" + SMALLER_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is less than DEFAULT_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.lessThan=" + DEFAULT_TOTAL_MATERIAL_COST);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is less than UPDATED_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldBeFound("totalMaterialCost.lessThan=" + UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByTotalMaterialCostIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is greater than DEFAULT_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldNotBeFound("totalMaterialCost.greaterThan=" + DEFAULT_TOTAL_MATERIAL_COST);

        // Get all the rawMaterialConsumptionList where totalMaterialCost is greater than SMALLER_TOTAL_MATERIAL_COST
        defaultRawMaterialConsumptionShouldBeFound("totalMaterialCost.greaterThan=" + SMALLER_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByProjectsIsEqualToSomething() throws Exception {
        Projects projects;
        if (TestUtil.findAll(em, Projects.class).isEmpty()) {
            rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
            projects = ProjectsResourceIT.createEntity(em);
        } else {
            projects = TestUtil.findAll(em, Projects.class).get(0);
        }
        em.persist(projects);
        em.flush();
        rawMaterialConsumption.setProjects(projects);
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
        Long projectsId = projects.getId();

        // Get all the rawMaterialConsumptionList where projects equals to projectsId
        defaultRawMaterialConsumptionShouldBeFound("projectsId.equals=" + projectsId);

        // Get all the rawMaterialConsumptionList where projects equals to (projectsId + 1)
        defaultRawMaterialConsumptionShouldNotBeFound("projectsId.equals=" + (projectsId + 1));
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByProductsIsEqualToSomething() throws Exception {
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
            products = ProductsResourceIT.createEntity(em);
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(products);
        em.flush();
        rawMaterialConsumption.setProducts(products);
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
        Long productsId = products.getId();

        // Get all the rawMaterialConsumptionList where products equals to productsId
        defaultRawMaterialConsumptionShouldBeFound("productsId.equals=" + productsId);

        // Get all the rawMaterialConsumptionList where products equals to (productsId + 1)
        defaultRawMaterialConsumptionShouldNotBeFound("productsId.equals=" + (productsId + 1));
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByProductionLineIsEqualToSomething() throws Exception {
        ProductionLine productionLine;
        if (TestUtil.findAll(em, ProductionLine.class).isEmpty()) {
            rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
            productionLine = ProductionLineResourceIT.createEntity(em);
        } else {
            productionLine = TestUtil.findAll(em, ProductionLine.class).get(0);
        }
        em.persist(productionLine);
        em.flush();
        rawMaterialConsumption.setProductionLine(productionLine);
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
        Long productionLineId = productionLine.getId();

        // Get all the rawMaterialConsumptionList where productionLine equals to productionLineId
        defaultRawMaterialConsumptionShouldBeFound("productionLineId.equals=" + productionLineId);

        // Get all the rawMaterialConsumptionList where productionLine equals to (productionLineId + 1)
        defaultRawMaterialConsumptionShouldNotBeFound("productionLineId.equals=" + (productionLineId + 1));
    }

    @Test
    @Transactional
    void getAllRawMaterialConsumptionsByRawMaterialIsEqualToSomething() throws Exception {
        RawMaterial rawMaterial;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
            rawMaterial = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterial = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterial);
        em.flush();
        rawMaterialConsumption.setRawMaterial(rawMaterial);
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);
        Long rawMaterialId = rawMaterial.getId();

        // Get all the rawMaterialConsumptionList where rawMaterial equals to rawMaterialId
        defaultRawMaterialConsumptionShouldBeFound("rawMaterialId.equals=" + rawMaterialId);

        // Get all the rawMaterialConsumptionList where rawMaterial equals to (rawMaterialId + 1)
        defaultRawMaterialConsumptionShouldNotBeFound("rawMaterialId.equals=" + (rawMaterialId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRawMaterialConsumptionShouldBeFound(String filter) throws Exception {
        restRawMaterialConsumptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterialConsumption.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantityConsumed").value(hasItem(DEFAULT_QUANTITY_CONSUMED.doubleValue())))
            .andExpect(jsonPath("$.[*].scrapGenerated").value(hasItem(DEFAULT_SCRAP_GENERATED.doubleValue())))
            .andExpect(jsonPath("$.[*].totalMaterialCost").value(hasItem(DEFAULT_TOTAL_MATERIAL_COST.doubleValue())));

        // Check, that the count call also returns 1
        restRawMaterialConsumptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRawMaterialConsumptionShouldNotBeFound(String filter) throws Exception {
        restRawMaterialConsumptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRawMaterialConsumptionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRawMaterialConsumption() throws Exception {
        // Get the rawMaterialConsumption
        restRawMaterialConsumptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRawMaterialConsumption() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();

        // Update the rawMaterialConsumption
        RawMaterialConsumption updatedRawMaterialConsumption = rawMaterialConsumptionRepository
            .findById(rawMaterialConsumption.getId())
            .get();
        // Disconnect from session so that the updates on updatedRawMaterialConsumption are not directly saved in db
        em.detach(updatedRawMaterialConsumption);
        updatedRawMaterialConsumption
            .quantityConsumed(UPDATED_QUANTITY_CONSUMED)
            .scrapGenerated(UPDATED_SCRAP_GENERATED)
            .totalMaterialCost(UPDATED_TOTAL_MATERIAL_COST);
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(updatedRawMaterialConsumption);

        restRawMaterialConsumptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rawMaterialConsumptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
        RawMaterialConsumption testRawMaterialConsumption = rawMaterialConsumptionList.get(rawMaterialConsumptionList.size() - 1);
        assertThat(testRawMaterialConsumption.getQuantityConsumed()).isEqualTo(UPDATED_QUANTITY_CONSUMED);
        assertThat(testRawMaterialConsumption.getScrapGenerated()).isEqualTo(UPDATED_SCRAP_GENERATED);
        assertThat(testRawMaterialConsumption.getTotalMaterialCost()).isEqualTo(UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void putNonExistingRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();
        rawMaterialConsumption.setId(count.incrementAndGet());

        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRawMaterialConsumptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rawMaterialConsumptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();
        rawMaterialConsumption.setId(count.incrementAndGet());

        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialConsumptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();
        rawMaterialConsumption.setId(count.incrementAndGet());

        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialConsumptionMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRawMaterialConsumptionWithPatch() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();

        // Update the rawMaterialConsumption using partial update
        RawMaterialConsumption partialUpdatedRawMaterialConsumption = new RawMaterialConsumption();
        partialUpdatedRawMaterialConsumption.setId(rawMaterialConsumption.getId());

        partialUpdatedRawMaterialConsumption.quantityConsumed(UPDATED_QUANTITY_CONSUMED).totalMaterialCost(UPDATED_TOTAL_MATERIAL_COST);

        restRawMaterialConsumptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRawMaterialConsumption.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRawMaterialConsumption))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
        RawMaterialConsumption testRawMaterialConsumption = rawMaterialConsumptionList.get(rawMaterialConsumptionList.size() - 1);
        assertThat(testRawMaterialConsumption.getQuantityConsumed()).isEqualTo(UPDATED_QUANTITY_CONSUMED);
        assertThat(testRawMaterialConsumption.getScrapGenerated()).isEqualTo(DEFAULT_SCRAP_GENERATED);
        assertThat(testRawMaterialConsumption.getTotalMaterialCost()).isEqualTo(UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void fullUpdateRawMaterialConsumptionWithPatch() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();

        // Update the rawMaterialConsumption using partial update
        RawMaterialConsumption partialUpdatedRawMaterialConsumption = new RawMaterialConsumption();
        partialUpdatedRawMaterialConsumption.setId(rawMaterialConsumption.getId());

        partialUpdatedRawMaterialConsumption
            .quantityConsumed(UPDATED_QUANTITY_CONSUMED)
            .scrapGenerated(UPDATED_SCRAP_GENERATED)
            .totalMaterialCost(UPDATED_TOTAL_MATERIAL_COST);

        restRawMaterialConsumptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRawMaterialConsumption.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRawMaterialConsumption))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
        RawMaterialConsumption testRawMaterialConsumption = rawMaterialConsumptionList.get(rawMaterialConsumptionList.size() - 1);
        assertThat(testRawMaterialConsumption.getQuantityConsumed()).isEqualTo(UPDATED_QUANTITY_CONSUMED);
        assertThat(testRawMaterialConsumption.getScrapGenerated()).isEqualTo(UPDATED_SCRAP_GENERATED);
        assertThat(testRawMaterialConsumption.getTotalMaterialCost()).isEqualTo(UPDATED_TOTAL_MATERIAL_COST);
    }

    @Test
    @Transactional
    void patchNonExistingRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();
        rawMaterialConsumption.setId(count.incrementAndGet());

        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRawMaterialConsumptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rawMaterialConsumptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();
        rawMaterialConsumption.setId(count.incrementAndGet());

        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialConsumptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRawMaterialConsumption() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialConsumptionRepository.findAll().size();
        rawMaterialConsumption.setId(count.incrementAndGet());

        // Create the RawMaterialConsumption
        RawMaterialConsumptionDTO rawMaterialConsumptionDTO = rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialConsumptionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialConsumptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RawMaterialConsumption in the database
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRawMaterialConsumption() throws Exception {
        // Initialize the database
        rawMaterialConsumptionRepository.saveAndFlush(rawMaterialConsumption);

        int databaseSizeBeforeDelete = rawMaterialConsumptionRepository.findAll().size();

        // Delete the rawMaterialConsumption
        restRawMaterialConsumptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, rawMaterialConsumption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RawMaterialConsumption> rawMaterialConsumptionList = rawMaterialConsumptionRepository.findAll();
        assertThat(rawMaterialConsumptionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
