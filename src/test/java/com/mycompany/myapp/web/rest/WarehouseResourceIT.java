package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.Warehouse;
import com.mycompany.myapp.repository.WarehouseRepository;
import com.mycompany.myapp.service.criteria.WarehouseCriteria;
import com.mycompany.myapp.service.dto.WarehouseDTO;
import com.mycompany.myapp.service.mapper.WarehouseMapper;
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
 * Integration tests for the {@link WarehouseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WarehouseResourceIT {

    private static final String DEFAULT_WH_NAME = "AAAAAAAAAA";
    private static final String UPDATED_WH_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final Long DEFAULT_PINCODE = 1L;
    private static final Long UPDATED_PINCODE = 2L;
    private static final Long SMALLER_PINCODE = 1L - 1L;

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_MANAGER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MANAGER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MANAGER_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_MANAGER_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/warehouses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseMapper warehouseMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWarehouseMockMvc;

    private Warehouse warehouse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createEntity(EntityManager em) {
        Warehouse warehouse = new Warehouse()
            .whName(DEFAULT_WH_NAME)
            .address(DEFAULT_ADDRESS)
            .pincode(DEFAULT_PINCODE)
            .city(DEFAULT_CITY)
            .managerName(DEFAULT_MANAGER_NAME)
            .managerEmail(DEFAULT_MANAGER_EMAIL);
        return warehouse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Warehouse createUpdatedEntity(EntityManager em) {
        Warehouse warehouse = new Warehouse()
            .whName(UPDATED_WH_NAME)
            .address(UPDATED_ADDRESS)
            .pincode(UPDATED_PINCODE)
            .city(UPDATED_CITY)
            .managerName(UPDATED_MANAGER_NAME)
            .managerEmail(UPDATED_MANAGER_EMAIL);
        return warehouse;
    }

    @BeforeEach
    public void initTest() {
        warehouse = createEntity(em);
    }

    @Test
    @Transactional
    void createWarehouse() throws Exception {
        int databaseSizeBeforeCreate = warehouseRepository.findAll().size();
        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isCreated());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeCreate + 1);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getWhName()).isEqualTo(DEFAULT_WH_NAME);
        assertThat(testWarehouse.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testWarehouse.getPincode()).isEqualTo(DEFAULT_PINCODE);
        assertThat(testWarehouse.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testWarehouse.getManagerName()).isEqualTo(DEFAULT_MANAGER_NAME);
        assertThat(testWarehouse.getManagerEmail()).isEqualTo(DEFAULT_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void createWarehouseWithExistingId() throws Exception {
        // Create the Warehouse with an existing ID
        warehouse.setId(1L);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        int databaseSizeBeforeCreate = warehouseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWarehouseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllWarehouses() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].whName").value(hasItem(DEFAULT_WH_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].pincode").value(hasItem(DEFAULT_PINCODE.intValue())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].managerName").value(hasItem(DEFAULT_MANAGER_NAME)))
            .andExpect(jsonPath("$.[*].managerEmail").value(hasItem(DEFAULT_MANAGER_EMAIL)));
    }

    @Test
    @Transactional
    void getWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get the warehouse
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL_ID, warehouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(warehouse.getId().intValue()))
            .andExpect(jsonPath("$.whName").value(DEFAULT_WH_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.pincode").value(DEFAULT_PINCODE.intValue()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.managerName").value(DEFAULT_MANAGER_NAME))
            .andExpect(jsonPath("$.managerEmail").value(DEFAULT_MANAGER_EMAIL));
    }

    @Test
    @Transactional
    void getWarehousesByIdFiltering() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        Long id = warehouse.getId();

        defaultWarehouseShouldBeFound("id.equals=" + id);
        defaultWarehouseShouldNotBeFound("id.notEquals=" + id);

        defaultWarehouseShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWarehouseShouldNotBeFound("id.greaterThan=" + id);

        defaultWarehouseShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWarehouseShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWarehousesByWhNameIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where whName equals to DEFAULT_WH_NAME
        defaultWarehouseShouldBeFound("whName.equals=" + DEFAULT_WH_NAME);

        // Get all the warehouseList where whName equals to UPDATED_WH_NAME
        defaultWarehouseShouldNotBeFound("whName.equals=" + UPDATED_WH_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByWhNameIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where whName in DEFAULT_WH_NAME or UPDATED_WH_NAME
        defaultWarehouseShouldBeFound("whName.in=" + DEFAULT_WH_NAME + "," + UPDATED_WH_NAME);

        // Get all the warehouseList where whName equals to UPDATED_WH_NAME
        defaultWarehouseShouldNotBeFound("whName.in=" + UPDATED_WH_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByWhNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where whName is not null
        defaultWarehouseShouldBeFound("whName.specified=true");

        // Get all the warehouseList where whName is null
        defaultWarehouseShouldNotBeFound("whName.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByWhNameContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where whName contains DEFAULT_WH_NAME
        defaultWarehouseShouldBeFound("whName.contains=" + DEFAULT_WH_NAME);

        // Get all the warehouseList where whName contains UPDATED_WH_NAME
        defaultWarehouseShouldNotBeFound("whName.contains=" + UPDATED_WH_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByWhNameNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where whName does not contain DEFAULT_WH_NAME
        defaultWarehouseShouldNotBeFound("whName.doesNotContain=" + DEFAULT_WH_NAME);

        // Get all the warehouseList where whName does not contain UPDATED_WH_NAME
        defaultWarehouseShouldBeFound("whName.doesNotContain=" + UPDATED_WH_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address equals to DEFAULT_ADDRESS
        defaultWarehouseShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the warehouseList where address equals to UPDATED_ADDRESS
        defaultWarehouseShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultWarehouseShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the warehouseList where address equals to UPDATED_ADDRESS
        defaultWarehouseShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address is not null
        defaultWarehouseShouldBeFound("address.specified=true");

        // Get all the warehouseList where address is null
        defaultWarehouseShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address contains DEFAULT_ADDRESS
        defaultWarehouseShouldBeFound("address.contains=" + DEFAULT_ADDRESS);

        // Get all the warehouseList where address contains UPDATED_ADDRESS
        defaultWarehouseShouldNotBeFound("address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where address does not contain DEFAULT_ADDRESS
        defaultWarehouseShouldNotBeFound("address.doesNotContain=" + DEFAULT_ADDRESS);

        // Get all the warehouseList where address does not contain UPDATED_ADDRESS
        defaultWarehouseShouldBeFound("address.doesNotContain=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode equals to DEFAULT_PINCODE
        defaultWarehouseShouldBeFound("pincode.equals=" + DEFAULT_PINCODE);

        // Get all the warehouseList where pincode equals to UPDATED_PINCODE
        defaultWarehouseShouldNotBeFound("pincode.equals=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode in DEFAULT_PINCODE or UPDATED_PINCODE
        defaultWarehouseShouldBeFound("pincode.in=" + DEFAULT_PINCODE + "," + UPDATED_PINCODE);

        // Get all the warehouseList where pincode equals to UPDATED_PINCODE
        defaultWarehouseShouldNotBeFound("pincode.in=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode is not null
        defaultWarehouseShouldBeFound("pincode.specified=true");

        // Get all the warehouseList where pincode is null
        defaultWarehouseShouldNotBeFound("pincode.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode is greater than or equal to DEFAULT_PINCODE
        defaultWarehouseShouldBeFound("pincode.greaterThanOrEqual=" + DEFAULT_PINCODE);

        // Get all the warehouseList where pincode is greater than or equal to UPDATED_PINCODE
        defaultWarehouseShouldNotBeFound("pincode.greaterThanOrEqual=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode is less than or equal to DEFAULT_PINCODE
        defaultWarehouseShouldBeFound("pincode.lessThanOrEqual=" + DEFAULT_PINCODE);

        // Get all the warehouseList where pincode is less than or equal to SMALLER_PINCODE
        defaultWarehouseShouldNotBeFound("pincode.lessThanOrEqual=" + SMALLER_PINCODE);
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsLessThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode is less than DEFAULT_PINCODE
        defaultWarehouseShouldNotBeFound("pincode.lessThan=" + DEFAULT_PINCODE);

        // Get all the warehouseList where pincode is less than UPDATED_PINCODE
        defaultWarehouseShouldBeFound("pincode.lessThan=" + UPDATED_PINCODE);
    }

    @Test
    @Transactional
    void getAllWarehousesByPincodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where pincode is greater than DEFAULT_PINCODE
        defaultWarehouseShouldNotBeFound("pincode.greaterThan=" + DEFAULT_PINCODE);

        // Get all the warehouseList where pincode is greater than SMALLER_PINCODE
        defaultWarehouseShouldBeFound("pincode.greaterThan=" + SMALLER_PINCODE);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city equals to DEFAULT_CITY
        defaultWarehouseShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the warehouseList where city equals to UPDATED_CITY
        defaultWarehouseShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city in DEFAULT_CITY or UPDATED_CITY
        defaultWarehouseShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the warehouseList where city equals to UPDATED_CITY
        defaultWarehouseShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city is not null
        defaultWarehouseShouldBeFound("city.specified=true");

        // Get all the warehouseList where city is null
        defaultWarehouseShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByCityContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city contains DEFAULT_CITY
        defaultWarehouseShouldBeFound("city.contains=" + DEFAULT_CITY);

        // Get all the warehouseList where city contains UPDATED_CITY
        defaultWarehouseShouldNotBeFound("city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where city does not contain DEFAULT_CITY
        defaultWarehouseShouldNotBeFound("city.doesNotContain=" + DEFAULT_CITY);

        // Get all the warehouseList where city does not contain UPDATED_CITY
        defaultWarehouseShouldBeFound("city.doesNotContain=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerNameIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerName equals to DEFAULT_MANAGER_NAME
        defaultWarehouseShouldBeFound("managerName.equals=" + DEFAULT_MANAGER_NAME);

        // Get all the warehouseList where managerName equals to UPDATED_MANAGER_NAME
        defaultWarehouseShouldNotBeFound("managerName.equals=" + UPDATED_MANAGER_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerNameIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerName in DEFAULT_MANAGER_NAME or UPDATED_MANAGER_NAME
        defaultWarehouseShouldBeFound("managerName.in=" + DEFAULT_MANAGER_NAME + "," + UPDATED_MANAGER_NAME);

        // Get all the warehouseList where managerName equals to UPDATED_MANAGER_NAME
        defaultWarehouseShouldNotBeFound("managerName.in=" + UPDATED_MANAGER_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerName is not null
        defaultWarehouseShouldBeFound("managerName.specified=true");

        // Get all the warehouseList where managerName is null
        defaultWarehouseShouldNotBeFound("managerName.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerNameContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerName contains DEFAULT_MANAGER_NAME
        defaultWarehouseShouldBeFound("managerName.contains=" + DEFAULT_MANAGER_NAME);

        // Get all the warehouseList where managerName contains UPDATED_MANAGER_NAME
        defaultWarehouseShouldNotBeFound("managerName.contains=" + UPDATED_MANAGER_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerNameNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerName does not contain DEFAULT_MANAGER_NAME
        defaultWarehouseShouldNotBeFound("managerName.doesNotContain=" + DEFAULT_MANAGER_NAME);

        // Get all the warehouseList where managerName does not contain UPDATED_MANAGER_NAME
        defaultWarehouseShouldBeFound("managerName.doesNotContain=" + UPDATED_MANAGER_NAME);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerEmail equals to DEFAULT_MANAGER_EMAIL
        defaultWarehouseShouldBeFound("managerEmail.equals=" + DEFAULT_MANAGER_EMAIL);

        // Get all the warehouseList where managerEmail equals to UPDATED_MANAGER_EMAIL
        defaultWarehouseShouldNotBeFound("managerEmail.equals=" + UPDATED_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerEmailIsInShouldWork() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerEmail in DEFAULT_MANAGER_EMAIL or UPDATED_MANAGER_EMAIL
        defaultWarehouseShouldBeFound("managerEmail.in=" + DEFAULT_MANAGER_EMAIL + "," + UPDATED_MANAGER_EMAIL);

        // Get all the warehouseList where managerEmail equals to UPDATED_MANAGER_EMAIL
        defaultWarehouseShouldNotBeFound("managerEmail.in=" + UPDATED_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerEmail is not null
        defaultWarehouseShouldBeFound("managerEmail.specified=true");

        // Get all the warehouseList where managerEmail is null
        defaultWarehouseShouldNotBeFound("managerEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerEmailContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerEmail contains DEFAULT_MANAGER_EMAIL
        defaultWarehouseShouldBeFound("managerEmail.contains=" + DEFAULT_MANAGER_EMAIL);

        // Get all the warehouseList where managerEmail contains UPDATED_MANAGER_EMAIL
        defaultWarehouseShouldNotBeFound("managerEmail.contains=" + UPDATED_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void getAllWarehousesByManagerEmailNotContainsSomething() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        // Get all the warehouseList where managerEmail does not contain DEFAULT_MANAGER_EMAIL
        defaultWarehouseShouldNotBeFound("managerEmail.doesNotContain=" + DEFAULT_MANAGER_EMAIL);

        // Get all the warehouseList where managerEmail does not contain UPDATED_MANAGER_EMAIL
        defaultWarehouseShouldBeFound("managerEmail.doesNotContain=" + UPDATED_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void getAllWarehousesByRawMaterialIsEqualToSomething() throws Exception {
        RawMaterial rawMaterial;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            warehouseRepository.saveAndFlush(warehouse);
            rawMaterial = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterial = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterial);
        em.flush();
        warehouse.addRawMaterial(rawMaterial);
        warehouseRepository.saveAndFlush(warehouse);
        Long rawMaterialId = rawMaterial.getId();

        // Get all the warehouseList where rawMaterial equals to rawMaterialId
        defaultWarehouseShouldBeFound("rawMaterialId.equals=" + rawMaterialId);

        // Get all the warehouseList where rawMaterial equals to (rawMaterialId + 1)
        defaultWarehouseShouldNotBeFound("rawMaterialId.equals=" + (rawMaterialId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWarehouseShouldBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(warehouse.getId().intValue())))
            .andExpect(jsonPath("$.[*].whName").value(hasItem(DEFAULT_WH_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].pincode").value(hasItem(DEFAULT_PINCODE.intValue())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].managerName").value(hasItem(DEFAULT_MANAGER_NAME)))
            .andExpect(jsonPath("$.[*].managerEmail").value(hasItem(DEFAULT_MANAGER_EMAIL)));

        // Check, that the count call also returns 1
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWarehouseShouldNotBeFound(String filter) throws Exception {
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWarehouseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWarehouse() throws Exception {
        // Get the warehouse
        restWarehouseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse
        Warehouse updatedWarehouse = warehouseRepository.findById(warehouse.getId()).get();
        // Disconnect from session so that the updates on updatedWarehouse are not directly saved in db
        em.detach(updatedWarehouse);
        updatedWarehouse
            .whName(UPDATED_WH_NAME)
            .address(UPDATED_ADDRESS)
            .pincode(UPDATED_PINCODE)
            .city(UPDATED_CITY)
            .managerName(UPDATED_MANAGER_NAME)
            .managerEmail(UPDATED_MANAGER_EMAIL);
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(updatedWarehouse);

        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getWhName()).isEqualTo(UPDATED_WH_NAME);
        assertThat(testWarehouse.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testWarehouse.getPincode()).isEqualTo(UPDATED_PINCODE);
        assertThat(testWarehouse.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testWarehouse.getManagerName()).isEqualTo(UPDATED_MANAGER_NAME);
        assertThat(testWarehouse.getManagerEmail()).isEqualTo(UPDATED_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void putNonExistingWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(warehouseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse.address(UPDATED_ADDRESS);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getWhName()).isEqualTo(DEFAULT_WH_NAME);
        assertThat(testWarehouse.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testWarehouse.getPincode()).isEqualTo(DEFAULT_PINCODE);
        assertThat(testWarehouse.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testWarehouse.getManagerName()).isEqualTo(DEFAULT_MANAGER_NAME);
        assertThat(testWarehouse.getManagerEmail()).isEqualTo(DEFAULT_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void fullUpdateWarehouseWithPatch() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();

        // Update the warehouse using partial update
        Warehouse partialUpdatedWarehouse = new Warehouse();
        partialUpdatedWarehouse.setId(warehouse.getId());

        partialUpdatedWarehouse
            .whName(UPDATED_WH_NAME)
            .address(UPDATED_ADDRESS)
            .pincode(UPDATED_PINCODE)
            .city(UPDATED_CITY)
            .managerName(UPDATED_MANAGER_NAME)
            .managerEmail(UPDATED_MANAGER_EMAIL);

        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWarehouse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWarehouse))
            )
            .andExpect(status().isOk());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
        Warehouse testWarehouse = warehouseList.get(warehouseList.size() - 1);
        assertThat(testWarehouse.getWhName()).isEqualTo(UPDATED_WH_NAME);
        assertThat(testWarehouse.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testWarehouse.getPincode()).isEqualTo(UPDATED_PINCODE);
        assertThat(testWarehouse.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testWarehouse.getManagerName()).isEqualTo(UPDATED_MANAGER_NAME);
        assertThat(testWarehouse.getManagerEmail()).isEqualTo(UPDATED_MANAGER_EMAIL);
    }

    @Test
    @Transactional
    void patchNonExistingWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, warehouseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWarehouse() throws Exception {
        int databaseSizeBeforeUpdate = warehouseRepository.findAll().size();
        warehouse.setId(count.incrementAndGet());

        // Create the Warehouse
        WarehouseDTO warehouseDTO = warehouseMapper.toDto(warehouse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWarehouseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(warehouseDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Warehouse in the database
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWarehouse() throws Exception {
        // Initialize the database
        warehouseRepository.saveAndFlush(warehouse);

        int databaseSizeBeforeDelete = warehouseRepository.findAll().size();

        // Delete the warehouse
        restWarehouseMockMvc
            .perform(delete(ENTITY_API_URL_ID, warehouse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Warehouse> warehouseList = warehouseRepository.findAll();
        assertThat(warehouseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
