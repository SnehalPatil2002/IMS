package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.Warehouse;
import com.mycompany.myapp.domain.enumeration.Unit;
import com.mycompany.myapp.repository.RawMaterialRepository;
import com.mycompany.myapp.service.RawMaterialService;
import com.mycompany.myapp.service.criteria.RawMaterialCriteria;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
import com.mycompany.myapp.service.mapper.RawMaterialMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RawMaterialResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RawMaterialResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BARCODE = "AAAAAAAAAA";
    private static final String UPDATED_BARCODE = "BBBBBBBBBB";

    private static final Long DEFAULT_QUANTITY = 1L;
    private static final Long UPDATED_QUANTITY = 2L;
    private static final Long SMALLER_QUANTITY = 1L - 1L;

    private static final Double DEFAULT_UNIT_PRICE = 1D;
    private static final Double UPDATED_UNIT_PRICE = 2D;
    private static final Double SMALLER_UNIT_PRICE = 1D - 1D;

    private static final Unit DEFAULT_UNIT_MEASURE = Unit.UNIT;
    private static final Unit UPDATED_UNIT_MEASURE = Unit.KG;

    private static final Double DEFAULT_GST_PERCENTAGE = 1D;
    private static final Double UPDATED_GST_PERCENTAGE = 2D;
    private static final Double SMALLER_GST_PERCENTAGE = 1D - 1D;

    private static final Long DEFAULT_REORDER_POINT = 1L;
    private static final Long UPDATED_REORDER_POINT = 2L;
    private static final Long SMALLER_REORDER_POINT = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/raw-materials";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepositoryMock;

    @Autowired
    private RawMaterialMapper rawMaterialMapper;

    @Mock
    private RawMaterialService rawMaterialServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRawMaterialMockMvc;

    private RawMaterial rawMaterial;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMaterial createEntity(EntityManager em) {
        RawMaterial rawMaterial = new RawMaterial()
            .name(DEFAULT_NAME)
            .barcode(DEFAULT_BARCODE)
            .quantity(DEFAULT_QUANTITY)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .unitMeasure(DEFAULT_UNIT_MEASURE)
            .gstPercentage(DEFAULT_GST_PERCENTAGE)
            .reorderPoint(DEFAULT_REORDER_POINT);
        return rawMaterial;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RawMaterial createUpdatedEntity(EntityManager em) {
        RawMaterial rawMaterial = new RawMaterial()
            .name(UPDATED_NAME)
            .barcode(UPDATED_BARCODE)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .unitMeasure(UPDATED_UNIT_MEASURE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .reorderPoint(UPDATED_REORDER_POINT);
        return rawMaterial;
    }

    @BeforeEach
    public void initTest() {
        rawMaterial = createEntity(em);
    }

    @Test
    @Transactional
    void createRawMaterial() throws Exception {
        int databaseSizeBeforeCreate = rawMaterialRepository.findAll().size();
        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);
        restRawMaterialMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isCreated());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeCreate + 1);
        RawMaterial testRawMaterial = rawMaterialList.get(rawMaterialList.size() - 1);
        assertThat(testRawMaterial.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRawMaterial.getBarcode()).isEqualTo(DEFAULT_BARCODE);
        assertThat(testRawMaterial.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testRawMaterial.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testRawMaterial.getUnitMeasure()).isEqualTo(DEFAULT_UNIT_MEASURE);
        assertThat(testRawMaterial.getGstPercentage()).isEqualTo(DEFAULT_GST_PERCENTAGE);
        assertThat(testRawMaterial.getReorderPoint()).isEqualTo(DEFAULT_REORDER_POINT);
    }

    @Test
    @Transactional
    void createRawMaterialWithExistingId() throws Exception {
        // Create the RawMaterial with an existing ID
        rawMaterial.setId(1L);
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        int databaseSizeBeforeCreate = rawMaterialRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRawMaterialMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRawMaterials() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].unitMeasure").value(hasItem(DEFAULT_UNIT_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].gstPercentage").value(hasItem(DEFAULT_GST_PERCENTAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].reorderPoint").value(hasItem(DEFAULT_REORDER_POINT.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRawMaterialsWithEagerRelationshipsIsEnabled() throws Exception {
        when(rawMaterialServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRawMaterialMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(rawMaterialServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRawMaterialsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(rawMaterialServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRawMaterialMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(rawMaterialRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRawMaterial() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get the rawMaterial
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL_ID, rawMaterial.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rawMaterial.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.barcode").value(DEFAULT_BARCODE))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.intValue()))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.unitMeasure").value(DEFAULT_UNIT_MEASURE.toString()))
            .andExpect(jsonPath("$.gstPercentage").value(DEFAULT_GST_PERCENTAGE.doubleValue()))
            .andExpect(jsonPath("$.reorderPoint").value(DEFAULT_REORDER_POINT.intValue()));
    }

    @Test
    @Transactional
    void getRawMaterialsByIdFiltering() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        Long id = rawMaterial.getId();

        defaultRawMaterialShouldBeFound("id.equals=" + id);
        defaultRawMaterialShouldNotBeFound("id.notEquals=" + id);

        defaultRawMaterialShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRawMaterialShouldNotBeFound("id.greaterThan=" + id);

        defaultRawMaterialShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRawMaterialShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name equals to DEFAULT_NAME
        defaultRawMaterialShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the rawMaterialList where name equals to UPDATED_NAME
        defaultRawMaterialShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRawMaterialShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the rawMaterialList where name equals to UPDATED_NAME
        defaultRawMaterialShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name is not null
        defaultRawMaterialShouldBeFound("name.specified=true");

        // Get all the rawMaterialList where name is null
        defaultRawMaterialShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameContainsSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name contains DEFAULT_NAME
        defaultRawMaterialShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the rawMaterialList where name contains UPDATED_NAME
        defaultRawMaterialShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where name does not contain DEFAULT_NAME
        defaultRawMaterialShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the rawMaterialList where name does not contain UPDATED_NAME
        defaultRawMaterialShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByBarcodeIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where barcode equals to DEFAULT_BARCODE
        defaultRawMaterialShouldBeFound("barcode.equals=" + DEFAULT_BARCODE);

        // Get all the rawMaterialList where barcode equals to UPDATED_BARCODE
        defaultRawMaterialShouldNotBeFound("barcode.equals=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByBarcodeIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where barcode in DEFAULT_BARCODE or UPDATED_BARCODE
        defaultRawMaterialShouldBeFound("barcode.in=" + DEFAULT_BARCODE + "," + UPDATED_BARCODE);

        // Get all the rawMaterialList where barcode equals to UPDATED_BARCODE
        defaultRawMaterialShouldNotBeFound("barcode.in=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByBarcodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where barcode is not null
        defaultRawMaterialShouldBeFound("barcode.specified=true");

        // Get all the rawMaterialList where barcode is null
        defaultRawMaterialShouldNotBeFound("barcode.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByBarcodeContainsSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where barcode contains DEFAULT_BARCODE
        defaultRawMaterialShouldBeFound("barcode.contains=" + DEFAULT_BARCODE);

        // Get all the rawMaterialList where barcode contains UPDATED_BARCODE
        defaultRawMaterialShouldNotBeFound("barcode.contains=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByBarcodeNotContainsSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where barcode does not contain DEFAULT_BARCODE
        defaultRawMaterialShouldNotBeFound("barcode.doesNotContain=" + DEFAULT_BARCODE);

        // Get all the rawMaterialList where barcode does not contain UPDATED_BARCODE
        defaultRawMaterialShouldBeFound("barcode.doesNotContain=" + UPDATED_BARCODE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity equals to DEFAULT_QUANTITY
        defaultRawMaterialShouldBeFound("quantity.equals=" + DEFAULT_QUANTITY);

        // Get all the rawMaterialList where quantity equals to UPDATED_QUANTITY
        defaultRawMaterialShouldNotBeFound("quantity.equals=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity in DEFAULT_QUANTITY or UPDATED_QUANTITY
        defaultRawMaterialShouldBeFound("quantity.in=" + DEFAULT_QUANTITY + "," + UPDATED_QUANTITY);

        // Get all the rawMaterialList where quantity equals to UPDATED_QUANTITY
        defaultRawMaterialShouldNotBeFound("quantity.in=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity is not null
        defaultRawMaterialShouldBeFound("quantity.specified=true");

        // Get all the rawMaterialList where quantity is null
        defaultRawMaterialShouldNotBeFound("quantity.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity is greater than or equal to DEFAULT_QUANTITY
        defaultRawMaterialShouldBeFound("quantity.greaterThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the rawMaterialList where quantity is greater than or equal to UPDATED_QUANTITY
        defaultRawMaterialShouldNotBeFound("quantity.greaterThanOrEqual=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity is less than or equal to DEFAULT_QUANTITY
        defaultRawMaterialShouldBeFound("quantity.lessThanOrEqual=" + DEFAULT_QUANTITY);

        // Get all the rawMaterialList where quantity is less than or equal to SMALLER_QUANTITY
        defaultRawMaterialShouldNotBeFound("quantity.lessThanOrEqual=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity is less than DEFAULT_QUANTITY
        defaultRawMaterialShouldNotBeFound("quantity.lessThan=" + DEFAULT_QUANTITY);

        // Get all the rawMaterialList where quantity is less than UPDATED_QUANTITY
        defaultRawMaterialShouldBeFound("quantity.lessThan=" + UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByQuantityIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where quantity is greater than DEFAULT_QUANTITY
        defaultRawMaterialShouldNotBeFound("quantity.greaterThan=" + DEFAULT_QUANTITY);

        // Get all the rawMaterialList where quantity is greater than SMALLER_QUANTITY
        defaultRawMaterialShouldBeFound("quantity.greaterThan=" + SMALLER_QUANTITY);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice equals to DEFAULT_UNIT_PRICE
        defaultRawMaterialShouldBeFound("unitPrice.equals=" + DEFAULT_UNIT_PRICE);

        // Get all the rawMaterialList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultRawMaterialShouldNotBeFound("unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice in DEFAULT_UNIT_PRICE or UPDATED_UNIT_PRICE
        defaultRawMaterialShouldBeFound("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE);

        // Get all the rawMaterialList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultRawMaterialShouldNotBeFound("unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice is not null
        defaultRawMaterialShouldBeFound("unitPrice.specified=true");

        // Get all the rawMaterialList where unitPrice is null
        defaultRawMaterialShouldNotBeFound("unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice is greater than or equal to DEFAULT_UNIT_PRICE
        defaultRawMaterialShouldBeFound("unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the rawMaterialList where unitPrice is greater than or equal to UPDATED_UNIT_PRICE
        defaultRawMaterialShouldNotBeFound("unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice is less than or equal to DEFAULT_UNIT_PRICE
        defaultRawMaterialShouldBeFound("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the rawMaterialList where unitPrice is less than or equal to SMALLER_UNIT_PRICE
        defaultRawMaterialShouldNotBeFound("unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice is less than DEFAULT_UNIT_PRICE
        defaultRawMaterialShouldNotBeFound("unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);

        // Get all the rawMaterialList where unitPrice is less than UPDATED_UNIT_PRICE
        defaultRawMaterialShouldBeFound("unitPrice.lessThan=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitPrice is greater than DEFAULT_UNIT_PRICE
        defaultRawMaterialShouldNotBeFound("unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);

        // Get all the rawMaterialList where unitPrice is greater than SMALLER_UNIT_PRICE
        defaultRawMaterialShouldBeFound("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitMeasureIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitMeasure equals to DEFAULT_UNIT_MEASURE
        defaultRawMaterialShouldBeFound("unitMeasure.equals=" + DEFAULT_UNIT_MEASURE);

        // Get all the rawMaterialList where unitMeasure equals to UPDATED_UNIT_MEASURE
        defaultRawMaterialShouldNotBeFound("unitMeasure.equals=" + UPDATED_UNIT_MEASURE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitMeasureIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitMeasure in DEFAULT_UNIT_MEASURE or UPDATED_UNIT_MEASURE
        defaultRawMaterialShouldBeFound("unitMeasure.in=" + DEFAULT_UNIT_MEASURE + "," + UPDATED_UNIT_MEASURE);

        // Get all the rawMaterialList where unitMeasure equals to UPDATED_UNIT_MEASURE
        defaultRawMaterialShouldNotBeFound("unitMeasure.in=" + UPDATED_UNIT_MEASURE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByUnitMeasureIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where unitMeasure is not null
        defaultRawMaterialShouldBeFound("unitMeasure.specified=true");

        // Get all the rawMaterialList where unitMeasure is null
        defaultRawMaterialShouldNotBeFound("unitMeasure.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage equals to DEFAULT_GST_PERCENTAGE
        defaultRawMaterialShouldBeFound("gstPercentage.equals=" + DEFAULT_GST_PERCENTAGE);

        // Get all the rawMaterialList where gstPercentage equals to UPDATED_GST_PERCENTAGE
        defaultRawMaterialShouldNotBeFound("gstPercentage.equals=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage in DEFAULT_GST_PERCENTAGE or UPDATED_GST_PERCENTAGE
        defaultRawMaterialShouldBeFound("gstPercentage.in=" + DEFAULT_GST_PERCENTAGE + "," + UPDATED_GST_PERCENTAGE);

        // Get all the rawMaterialList where gstPercentage equals to UPDATED_GST_PERCENTAGE
        defaultRawMaterialShouldNotBeFound("gstPercentage.in=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage is not null
        defaultRawMaterialShouldBeFound("gstPercentage.specified=true");

        // Get all the rawMaterialList where gstPercentage is null
        defaultRawMaterialShouldNotBeFound("gstPercentage.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage is greater than or equal to DEFAULT_GST_PERCENTAGE
        defaultRawMaterialShouldBeFound("gstPercentage.greaterThanOrEqual=" + DEFAULT_GST_PERCENTAGE);

        // Get all the rawMaterialList where gstPercentage is greater than or equal to UPDATED_GST_PERCENTAGE
        defaultRawMaterialShouldNotBeFound("gstPercentage.greaterThanOrEqual=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage is less than or equal to DEFAULT_GST_PERCENTAGE
        defaultRawMaterialShouldBeFound("gstPercentage.lessThanOrEqual=" + DEFAULT_GST_PERCENTAGE);

        // Get all the rawMaterialList where gstPercentage is less than or equal to SMALLER_GST_PERCENTAGE
        defaultRawMaterialShouldNotBeFound("gstPercentage.lessThanOrEqual=" + SMALLER_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage is less than DEFAULT_GST_PERCENTAGE
        defaultRawMaterialShouldNotBeFound("gstPercentage.lessThan=" + DEFAULT_GST_PERCENTAGE);

        // Get all the rawMaterialList where gstPercentage is less than UPDATED_GST_PERCENTAGE
        defaultRawMaterialShouldBeFound("gstPercentage.lessThan=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByGstPercentageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where gstPercentage is greater than DEFAULT_GST_PERCENTAGE
        defaultRawMaterialShouldNotBeFound("gstPercentage.greaterThan=" + DEFAULT_GST_PERCENTAGE);

        // Get all the rawMaterialList where gstPercentage is greater than SMALLER_GST_PERCENTAGE
        defaultRawMaterialShouldBeFound("gstPercentage.greaterThan=" + SMALLER_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint equals to DEFAULT_REORDER_POINT
        defaultRawMaterialShouldBeFound("reorderPoint.equals=" + DEFAULT_REORDER_POINT);

        // Get all the rawMaterialList where reorderPoint equals to UPDATED_REORDER_POINT
        defaultRawMaterialShouldNotBeFound("reorderPoint.equals=" + UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsInShouldWork() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint in DEFAULT_REORDER_POINT or UPDATED_REORDER_POINT
        defaultRawMaterialShouldBeFound("reorderPoint.in=" + DEFAULT_REORDER_POINT + "," + UPDATED_REORDER_POINT);

        // Get all the rawMaterialList where reorderPoint equals to UPDATED_REORDER_POINT
        defaultRawMaterialShouldNotBeFound("reorderPoint.in=" + UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsNullOrNotNull() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint is not null
        defaultRawMaterialShouldBeFound("reorderPoint.specified=true");

        // Get all the rawMaterialList where reorderPoint is null
        defaultRawMaterialShouldNotBeFound("reorderPoint.specified=false");
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint is greater than or equal to DEFAULT_REORDER_POINT
        defaultRawMaterialShouldBeFound("reorderPoint.greaterThanOrEqual=" + DEFAULT_REORDER_POINT);

        // Get all the rawMaterialList where reorderPoint is greater than or equal to UPDATED_REORDER_POINT
        defaultRawMaterialShouldNotBeFound("reorderPoint.greaterThanOrEqual=" + UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint is less than or equal to DEFAULT_REORDER_POINT
        defaultRawMaterialShouldBeFound("reorderPoint.lessThanOrEqual=" + DEFAULT_REORDER_POINT);

        // Get all the rawMaterialList where reorderPoint is less than or equal to SMALLER_REORDER_POINT
        defaultRawMaterialShouldNotBeFound("reorderPoint.lessThanOrEqual=" + SMALLER_REORDER_POINT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsLessThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint is less than DEFAULT_REORDER_POINT
        defaultRawMaterialShouldNotBeFound("reorderPoint.lessThan=" + DEFAULT_REORDER_POINT);

        // Get all the rawMaterialList where reorderPoint is less than UPDATED_REORDER_POINT
        defaultRawMaterialShouldBeFound("reorderPoint.lessThan=" + UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByReorderPointIsGreaterThanSomething() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        // Get all the rawMaterialList where reorderPoint is greater than DEFAULT_REORDER_POINT
        defaultRawMaterialShouldNotBeFound("reorderPoint.greaterThan=" + DEFAULT_REORDER_POINT);

        // Get all the rawMaterialList where reorderPoint is greater than SMALLER_REORDER_POINT
        defaultRawMaterialShouldBeFound("reorderPoint.greaterThan=" + SMALLER_REORDER_POINT);
    }

    @Test
    @Transactional
    void getAllRawMaterialsByProductsIsEqualToSomething() throws Exception {
        Products products;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            rawMaterialRepository.saveAndFlush(rawMaterial);
            products = ProductsResourceIT.createEntity(em);
        } else {
            products = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(products);
        em.flush();
        rawMaterial.addProducts(products);
        rawMaterialRepository.saveAndFlush(rawMaterial);
        Long productsId = products.getId();

        // Get all the rawMaterialList where products equals to productsId
        defaultRawMaterialShouldBeFound("productsId.equals=" + productsId);

        // Get all the rawMaterialList where products equals to (productsId + 1)
        defaultRawMaterialShouldNotBeFound("productsId.equals=" + (productsId + 1));
    }

    @Test
    @Transactional
    void getAllRawMaterialsByWarehouseIsEqualToSomething() throws Exception {
        Warehouse warehouse;
        if (TestUtil.findAll(em, Warehouse.class).isEmpty()) {
            rawMaterialRepository.saveAndFlush(rawMaterial);
            warehouse = WarehouseResourceIT.createEntity(em);
        } else {
            warehouse = TestUtil.findAll(em, Warehouse.class).get(0);
        }
        em.persist(warehouse);
        em.flush();
        rawMaterial.setWarehouse(warehouse);
        rawMaterialRepository.saveAndFlush(rawMaterial);
        Long warehouseId = warehouse.getId();

        // Get all the rawMaterialList where warehouse equals to warehouseId
        defaultRawMaterialShouldBeFound("warehouseId.equals=" + warehouseId);

        // Get all the rawMaterialList where warehouse equals to (warehouseId + 1)
        defaultRawMaterialShouldNotBeFound("warehouseId.equals=" + (warehouseId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRawMaterialShouldBeFound(String filter) throws Exception {
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rawMaterial.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].barcode").value(hasItem(DEFAULT_BARCODE)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].unitMeasure").value(hasItem(DEFAULT_UNIT_MEASURE.toString())))
            .andExpect(jsonPath("$.[*].gstPercentage").value(hasItem(DEFAULT_GST_PERCENTAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].reorderPoint").value(hasItem(DEFAULT_REORDER_POINT.intValue())));

        // Check, that the count call also returns 1
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRawMaterialShouldNotBeFound(String filter) throws Exception {
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRawMaterialMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRawMaterial() throws Exception {
        // Get the rawMaterial
        restRawMaterialMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRawMaterial() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();

        // Update the rawMaterial
        RawMaterial updatedRawMaterial = rawMaterialRepository.findById(rawMaterial.getId()).get();
        // Disconnect from session so that the updates on updatedRawMaterial are not directly saved in db
        em.detach(updatedRawMaterial);
        updatedRawMaterial
            .name(UPDATED_NAME)
            .barcode(UPDATED_BARCODE)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .unitMeasure(UPDATED_UNIT_MEASURE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .reorderPoint(UPDATED_REORDER_POINT);
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(updatedRawMaterial);

        restRawMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rawMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
        RawMaterial testRawMaterial = rawMaterialList.get(rawMaterialList.size() - 1);
        assertThat(testRawMaterial.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRawMaterial.getBarcode()).isEqualTo(UPDATED_BARCODE);
        assertThat(testRawMaterial.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testRawMaterial.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testRawMaterial.getUnitMeasure()).isEqualTo(UPDATED_UNIT_MEASURE);
        assertThat(testRawMaterial.getGstPercentage()).isEqualTo(UPDATED_GST_PERCENTAGE);
        assertThat(testRawMaterial.getReorderPoint()).isEqualTo(UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void putNonExistingRawMaterial() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();
        rawMaterial.setId(count.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rawMaterialDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRawMaterial() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();
        rawMaterial.setId(count.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRawMaterial() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();
        rawMaterial.setId(count.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRawMaterialWithPatch() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();

        // Update the rawMaterial using partial update
        RawMaterial partialUpdatedRawMaterial = new RawMaterial();
        partialUpdatedRawMaterial.setId(rawMaterial.getId());

        partialUpdatedRawMaterial
            .quantity(UPDATED_QUANTITY)
            .unitMeasure(UPDATED_UNIT_MEASURE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .reorderPoint(UPDATED_REORDER_POINT);

        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRawMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRawMaterial))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
        RawMaterial testRawMaterial = rawMaterialList.get(rawMaterialList.size() - 1);
        assertThat(testRawMaterial.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRawMaterial.getBarcode()).isEqualTo(DEFAULT_BARCODE);
        assertThat(testRawMaterial.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testRawMaterial.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testRawMaterial.getUnitMeasure()).isEqualTo(UPDATED_UNIT_MEASURE);
        assertThat(testRawMaterial.getGstPercentage()).isEqualTo(UPDATED_GST_PERCENTAGE);
        assertThat(testRawMaterial.getReorderPoint()).isEqualTo(UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void fullUpdateRawMaterialWithPatch() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();

        // Update the rawMaterial using partial update
        RawMaterial partialUpdatedRawMaterial = new RawMaterial();
        partialUpdatedRawMaterial.setId(rawMaterial.getId());

        partialUpdatedRawMaterial
            .name(UPDATED_NAME)
            .barcode(UPDATED_BARCODE)
            .quantity(UPDATED_QUANTITY)
            .unitPrice(UPDATED_UNIT_PRICE)
            .unitMeasure(UPDATED_UNIT_MEASURE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .reorderPoint(UPDATED_REORDER_POINT);

        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRawMaterial.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRawMaterial))
            )
            .andExpect(status().isOk());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
        RawMaterial testRawMaterial = rawMaterialList.get(rawMaterialList.size() - 1);
        assertThat(testRawMaterial.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRawMaterial.getBarcode()).isEqualTo(UPDATED_BARCODE);
        assertThat(testRawMaterial.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testRawMaterial.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testRawMaterial.getUnitMeasure()).isEqualTo(UPDATED_UNIT_MEASURE);
        assertThat(testRawMaterial.getGstPercentage()).isEqualTo(UPDATED_GST_PERCENTAGE);
        assertThat(testRawMaterial.getReorderPoint()).isEqualTo(UPDATED_REORDER_POINT);
    }

    @Test
    @Transactional
    void patchNonExistingRawMaterial() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();
        rawMaterial.setId(count.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rawMaterialDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRawMaterial() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();
        rawMaterial.setId(count.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRawMaterial() throws Exception {
        int databaseSizeBeforeUpdate = rawMaterialRepository.findAll().size();
        rawMaterial.setId(count.incrementAndGet());

        // Create the RawMaterial
        RawMaterialDTO rawMaterialDTO = rawMaterialMapper.toDto(rawMaterial);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRawMaterialMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(rawMaterialDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RawMaterial in the database
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRawMaterial() throws Exception {
        // Initialize the database
        rawMaterialRepository.saveAndFlush(rawMaterial);

        int databaseSizeBeforeDelete = rawMaterialRepository.findAll().size();

        // Delete the rawMaterial
        restRawMaterialMockMvc
            .perform(delete(ENTITY_API_URL_ID, rawMaterial.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RawMaterial> rawMaterialList = rawMaterialRepository.findAll();
        assertThat(rawMaterialList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
