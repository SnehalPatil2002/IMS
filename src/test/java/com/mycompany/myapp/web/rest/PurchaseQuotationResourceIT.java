package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Clients;
import com.mycompany.myapp.domain.OrderRecieved;
import com.mycompany.myapp.domain.PurchaseQuotation;
import com.mycompany.myapp.domain.PurchaseQuotationDetails;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.PurchaseQuotationRepository;
import com.mycompany.myapp.service.criteria.PurchaseQuotationCriteria;
import com.mycompany.myapp.service.dto.PurchaseQuotationDTO;
import com.mycompany.myapp.service.mapper.PurchaseQuotationMapper;
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
 * Integration tests for the {@link PurchaseQuotationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseQuotationResourceIT {

    private static final String DEFAULT_REFERENCE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_NUMBER = "BBBBBBBBBB";

    private static final Double DEFAULT_TOTAL_PO_AMOUNT = 1D;
    private static final Double UPDATED_TOTAL_PO_AMOUNT = 2D;
    private static final Double SMALLER_TOTAL_PO_AMOUNT = 1D - 1D;

    private static final Double DEFAULT_TOTAL_GST_AMOUNT = 1D;
    private static final Double UPDATED_TOTAL_GST_AMOUNT = 2D;
    private static final Double SMALLER_TOTAL_GST_AMOUNT = 1D - 1D;

    private static final Instant DEFAULT_PO_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PO_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPECTED_DELIVERY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPECTED_DELIVERY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_ORDER_STATUS = Status.REQUESTED;
    private static final Status UPDATED_ORDER_STATUS = Status.APPROVED;

    private static final String ENTITY_API_URL = "/api/purchase-quotations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PurchaseQuotationRepository purchaseQuotationRepository;

    @Autowired
    private PurchaseQuotationMapper purchaseQuotationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseQuotationMockMvc;

    private PurchaseQuotation purchaseQuotation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseQuotation createEntity(EntityManager em) {
        PurchaseQuotation purchaseQuotation = new PurchaseQuotation()
            .referenceNumber(DEFAULT_REFERENCE_NUMBER)
            .totalPOAmount(DEFAULT_TOTAL_PO_AMOUNT)
            .totalGSTAmount(DEFAULT_TOTAL_GST_AMOUNT)
            .poDate(DEFAULT_PO_DATE)
            .expectedDeliveryDate(DEFAULT_EXPECTED_DELIVERY_DATE)
            .orderStatus(DEFAULT_ORDER_STATUS);
        return purchaseQuotation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseQuotation createUpdatedEntity(EntityManager em) {
        PurchaseQuotation purchaseQuotation = new PurchaseQuotation()
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .totalPOAmount(UPDATED_TOTAL_PO_AMOUNT)
            .totalGSTAmount(UPDATED_TOTAL_GST_AMOUNT)
            .poDate(UPDATED_PO_DATE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .orderStatus(UPDATED_ORDER_STATUS);
        return purchaseQuotation;
    }

    @BeforeEach
    public void initTest() {
        purchaseQuotation = createEntity(em);
    }

    @Test
    @Transactional
    void createPurchaseQuotation() throws Exception {
        int databaseSizeBeforeCreate = purchaseQuotationRepository.findAll().size();
        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);
        restPurchaseQuotationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeCreate + 1);
        PurchaseQuotation testPurchaseQuotation = purchaseQuotationList.get(purchaseQuotationList.size() - 1);
        assertThat(testPurchaseQuotation.getReferenceNumber()).isEqualTo(DEFAULT_REFERENCE_NUMBER);
        assertThat(testPurchaseQuotation.getTotalPOAmount()).isEqualTo(DEFAULT_TOTAL_PO_AMOUNT);
        assertThat(testPurchaseQuotation.getTotalGSTAmount()).isEqualTo(DEFAULT_TOTAL_GST_AMOUNT);
        assertThat(testPurchaseQuotation.getPoDate()).isEqualTo(DEFAULT_PO_DATE);
        assertThat(testPurchaseQuotation.getExpectedDeliveryDate()).isEqualTo(DEFAULT_EXPECTED_DELIVERY_DATE);
        assertThat(testPurchaseQuotation.getOrderStatus()).isEqualTo(DEFAULT_ORDER_STATUS);
    }

    @Test
    @Transactional
    void createPurchaseQuotationWithExistingId() throws Exception {
        // Create the PurchaseQuotation with an existing ID
        purchaseQuotation.setId(1L);
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        int databaseSizeBeforeCreate = purchaseQuotationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseQuotationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotations() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList
        restPurchaseQuotationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseQuotation.getId().intValue())))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].totalPOAmount").value(hasItem(DEFAULT_TOTAL_PO_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalGSTAmount").value(hasItem(DEFAULT_TOTAL_GST_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].poDate").value(hasItem(DEFAULT_PO_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedDeliveryDate").value(hasItem(DEFAULT_EXPECTED_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())));
    }

    @Test
    @Transactional
    void getPurchaseQuotation() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get the purchaseQuotation
        restPurchaseQuotationMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseQuotation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseQuotation.getId().intValue()))
            .andExpect(jsonPath("$.referenceNumber").value(DEFAULT_REFERENCE_NUMBER))
            .andExpect(jsonPath("$.totalPOAmount").value(DEFAULT_TOTAL_PO_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.totalGSTAmount").value(DEFAULT_TOTAL_GST_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.poDate").value(DEFAULT_PO_DATE.toString()))
            .andExpect(jsonPath("$.expectedDeliveryDate").value(DEFAULT_EXPECTED_DELIVERY_DATE.toString()))
            .andExpect(jsonPath("$.orderStatus").value(DEFAULT_ORDER_STATUS.toString()));
    }

    @Test
    @Transactional
    void getPurchaseQuotationsByIdFiltering() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        Long id = purchaseQuotation.getId();

        defaultPurchaseQuotationShouldBeFound("id.equals=" + id);
        defaultPurchaseQuotationShouldNotBeFound("id.notEquals=" + id);

        defaultPurchaseQuotationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPurchaseQuotationShouldNotBeFound("id.greaterThan=" + id);

        defaultPurchaseQuotationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPurchaseQuotationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByReferenceNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where referenceNumber equals to DEFAULT_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldBeFound("referenceNumber.equals=" + DEFAULT_REFERENCE_NUMBER);

        // Get all the purchaseQuotationList where referenceNumber equals to UPDATED_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldNotBeFound("referenceNumber.equals=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByReferenceNumberIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where referenceNumber in DEFAULT_REFERENCE_NUMBER or UPDATED_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldBeFound("referenceNumber.in=" + DEFAULT_REFERENCE_NUMBER + "," + UPDATED_REFERENCE_NUMBER);

        // Get all the purchaseQuotationList where referenceNumber equals to UPDATED_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldNotBeFound("referenceNumber.in=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByReferenceNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where referenceNumber is not null
        defaultPurchaseQuotationShouldBeFound("referenceNumber.specified=true");

        // Get all the purchaseQuotationList where referenceNumber is null
        defaultPurchaseQuotationShouldNotBeFound("referenceNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByReferenceNumberContainsSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where referenceNumber contains DEFAULT_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldBeFound("referenceNumber.contains=" + DEFAULT_REFERENCE_NUMBER);

        // Get all the purchaseQuotationList where referenceNumber contains UPDATED_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldNotBeFound("referenceNumber.contains=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByReferenceNumberNotContainsSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where referenceNumber does not contain DEFAULT_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldNotBeFound("referenceNumber.doesNotContain=" + DEFAULT_REFERENCE_NUMBER);

        // Get all the purchaseQuotationList where referenceNumber does not contain UPDATED_REFERENCE_NUMBER
        defaultPurchaseQuotationShouldBeFound("referenceNumber.doesNotContain=" + UPDATED_REFERENCE_NUMBER);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount equals to DEFAULT_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.equals=" + DEFAULT_TOTAL_PO_AMOUNT);

        // Get all the purchaseQuotationList where totalPOAmount equals to UPDATED_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.equals=" + UPDATED_TOTAL_PO_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount in DEFAULT_TOTAL_PO_AMOUNT or UPDATED_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.in=" + DEFAULT_TOTAL_PO_AMOUNT + "," + UPDATED_TOTAL_PO_AMOUNT);

        // Get all the purchaseQuotationList where totalPOAmount equals to UPDATED_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.in=" + UPDATED_TOTAL_PO_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount is not null
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.specified=true");

        // Get all the purchaseQuotationList where totalPOAmount is null
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount is greater than or equal to DEFAULT_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_PO_AMOUNT);

        // Get all the purchaseQuotationList where totalPOAmount is greater than or equal to UPDATED_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.greaterThanOrEqual=" + UPDATED_TOTAL_PO_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount is less than or equal to DEFAULT_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.lessThanOrEqual=" + DEFAULT_TOTAL_PO_AMOUNT);

        // Get all the purchaseQuotationList where totalPOAmount is less than or equal to SMALLER_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.lessThanOrEqual=" + SMALLER_TOTAL_PO_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount is less than DEFAULT_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.lessThan=" + DEFAULT_TOTAL_PO_AMOUNT);

        // Get all the purchaseQuotationList where totalPOAmount is less than UPDATED_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.lessThan=" + UPDATED_TOTAL_PO_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalPOAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalPOAmount is greater than DEFAULT_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalPOAmount.greaterThan=" + DEFAULT_TOTAL_PO_AMOUNT);

        // Get all the purchaseQuotationList where totalPOAmount is greater than SMALLER_TOTAL_PO_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalPOAmount.greaterThan=" + SMALLER_TOTAL_PO_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount equals to DEFAULT_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.equals=" + DEFAULT_TOTAL_GST_AMOUNT);

        // Get all the purchaseQuotationList where totalGSTAmount equals to UPDATED_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.equals=" + UPDATED_TOTAL_GST_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount in DEFAULT_TOTAL_GST_AMOUNT or UPDATED_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.in=" + DEFAULT_TOTAL_GST_AMOUNT + "," + UPDATED_TOTAL_GST_AMOUNT);

        // Get all the purchaseQuotationList where totalGSTAmount equals to UPDATED_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.in=" + UPDATED_TOTAL_GST_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount is not null
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.specified=true");

        // Get all the purchaseQuotationList where totalGSTAmount is null
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount is greater than or equal to DEFAULT_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_GST_AMOUNT);

        // Get all the purchaseQuotationList where totalGSTAmount is greater than or equal to UPDATED_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.greaterThanOrEqual=" + UPDATED_TOTAL_GST_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount is less than or equal to DEFAULT_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.lessThanOrEqual=" + DEFAULT_TOTAL_GST_AMOUNT);

        // Get all the purchaseQuotationList where totalGSTAmount is less than or equal to SMALLER_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.lessThanOrEqual=" + SMALLER_TOTAL_GST_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount is less than DEFAULT_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.lessThan=" + DEFAULT_TOTAL_GST_AMOUNT);

        // Get all the purchaseQuotationList where totalGSTAmount is less than UPDATED_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.lessThan=" + UPDATED_TOTAL_GST_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByTotalGSTAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where totalGSTAmount is greater than DEFAULT_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldNotBeFound("totalGSTAmount.greaterThan=" + DEFAULT_TOTAL_GST_AMOUNT);

        // Get all the purchaseQuotationList where totalGSTAmount is greater than SMALLER_TOTAL_GST_AMOUNT
        defaultPurchaseQuotationShouldBeFound("totalGSTAmount.greaterThan=" + SMALLER_TOTAL_GST_AMOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByPoDateIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where poDate equals to DEFAULT_PO_DATE
        defaultPurchaseQuotationShouldBeFound("poDate.equals=" + DEFAULT_PO_DATE);

        // Get all the purchaseQuotationList where poDate equals to UPDATED_PO_DATE
        defaultPurchaseQuotationShouldNotBeFound("poDate.equals=" + UPDATED_PO_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByPoDateIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where poDate in DEFAULT_PO_DATE or UPDATED_PO_DATE
        defaultPurchaseQuotationShouldBeFound("poDate.in=" + DEFAULT_PO_DATE + "," + UPDATED_PO_DATE);

        // Get all the purchaseQuotationList where poDate equals to UPDATED_PO_DATE
        defaultPurchaseQuotationShouldNotBeFound("poDate.in=" + UPDATED_PO_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByPoDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where poDate is not null
        defaultPurchaseQuotationShouldBeFound("poDate.specified=true");

        // Get all the purchaseQuotationList where poDate is null
        defaultPurchaseQuotationShouldNotBeFound("poDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByExpectedDeliveryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where expectedDeliveryDate equals to DEFAULT_EXPECTED_DELIVERY_DATE
        defaultPurchaseQuotationShouldBeFound("expectedDeliveryDate.equals=" + DEFAULT_EXPECTED_DELIVERY_DATE);

        // Get all the purchaseQuotationList where expectedDeliveryDate equals to UPDATED_EXPECTED_DELIVERY_DATE
        defaultPurchaseQuotationShouldNotBeFound("expectedDeliveryDate.equals=" + UPDATED_EXPECTED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByExpectedDeliveryDateIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where expectedDeliveryDate in DEFAULT_EXPECTED_DELIVERY_DATE or UPDATED_EXPECTED_DELIVERY_DATE
        defaultPurchaseQuotationShouldBeFound(
            "expectedDeliveryDate.in=" + DEFAULT_EXPECTED_DELIVERY_DATE + "," + UPDATED_EXPECTED_DELIVERY_DATE
        );

        // Get all the purchaseQuotationList where expectedDeliveryDate equals to UPDATED_EXPECTED_DELIVERY_DATE
        defaultPurchaseQuotationShouldNotBeFound("expectedDeliveryDate.in=" + UPDATED_EXPECTED_DELIVERY_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByExpectedDeliveryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where expectedDeliveryDate is not null
        defaultPurchaseQuotationShouldBeFound("expectedDeliveryDate.specified=true");

        // Get all the purchaseQuotationList where expectedDeliveryDate is null
        defaultPurchaseQuotationShouldNotBeFound("expectedDeliveryDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByOrderStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where orderStatus equals to DEFAULT_ORDER_STATUS
        defaultPurchaseQuotationShouldBeFound("orderStatus.equals=" + DEFAULT_ORDER_STATUS);

        // Get all the purchaseQuotationList where orderStatus equals to UPDATED_ORDER_STATUS
        defaultPurchaseQuotationShouldNotBeFound("orderStatus.equals=" + UPDATED_ORDER_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByOrderStatusIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where orderStatus in DEFAULT_ORDER_STATUS or UPDATED_ORDER_STATUS
        defaultPurchaseQuotationShouldBeFound("orderStatus.in=" + DEFAULT_ORDER_STATUS + "," + UPDATED_ORDER_STATUS);

        // Get all the purchaseQuotationList where orderStatus equals to UPDATED_ORDER_STATUS
        defaultPurchaseQuotationShouldNotBeFound("orderStatus.in=" + UPDATED_ORDER_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByOrderStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        // Get all the purchaseQuotationList where orderStatus is not null
        defaultPurchaseQuotationShouldBeFound("orderStatus.specified=true");

        // Get all the purchaseQuotationList where orderStatus is null
        defaultPurchaseQuotationShouldNotBeFound("orderStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByPurchaseQuotationDetailsIsEqualToSomething() throws Exception {
        PurchaseQuotationDetails purchaseQuotationDetails;
        if (TestUtil.findAll(em, PurchaseQuotationDetails.class).isEmpty()) {
            purchaseQuotationRepository.saveAndFlush(purchaseQuotation);
            purchaseQuotationDetails = PurchaseQuotationDetailsResourceIT.createEntity(em);
        } else {
            purchaseQuotationDetails = TestUtil.findAll(em, PurchaseQuotationDetails.class).get(0);
        }
        em.persist(purchaseQuotationDetails);
        em.flush();
        purchaseQuotation.addPurchaseQuotationDetails(purchaseQuotationDetails);
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);
        Long purchaseQuotationDetailsId = purchaseQuotationDetails.getId();

        // Get all the purchaseQuotationList where purchaseQuotationDetails equals to purchaseQuotationDetailsId
        defaultPurchaseQuotationShouldBeFound("purchaseQuotationDetailsId.equals=" + purchaseQuotationDetailsId);

        // Get all the purchaseQuotationList where purchaseQuotationDetails equals to (purchaseQuotationDetailsId + 1)
        defaultPurchaseQuotationShouldNotBeFound("purchaseQuotationDetailsId.equals=" + (purchaseQuotationDetailsId + 1));
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByOrderRecievedIsEqualToSomething() throws Exception {
        OrderRecieved orderRecieved;
        if (TestUtil.findAll(em, OrderRecieved.class).isEmpty()) {
            purchaseQuotationRepository.saveAndFlush(purchaseQuotation);
            orderRecieved = OrderRecievedResourceIT.createEntity(em);
        } else {
            orderRecieved = TestUtil.findAll(em, OrderRecieved.class).get(0);
        }
        em.persist(orderRecieved);
        em.flush();
        purchaseQuotation.addOrderRecieved(orderRecieved);
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);
        Long orderRecievedId = orderRecieved.getId();

        // Get all the purchaseQuotationList where orderRecieved equals to orderRecievedId
        defaultPurchaseQuotationShouldBeFound("orderRecievedId.equals=" + orderRecievedId);

        // Get all the purchaseQuotationList where orderRecieved equals to (orderRecievedId + 1)
        defaultPurchaseQuotationShouldNotBeFound("orderRecievedId.equals=" + (orderRecievedId + 1));
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationsByClientsIsEqualToSomething() throws Exception {
        Clients clients;
        if (TestUtil.findAll(em, Clients.class).isEmpty()) {
            purchaseQuotationRepository.saveAndFlush(purchaseQuotation);
            clients = ClientsResourceIT.createEntity(em);
        } else {
            clients = TestUtil.findAll(em, Clients.class).get(0);
        }
        em.persist(clients);
        em.flush();
        purchaseQuotation.setClients(clients);
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);
        Long clientsId = clients.getId();

        // Get all the purchaseQuotationList where clients equals to clientsId
        defaultPurchaseQuotationShouldBeFound("clientsId.equals=" + clientsId);

        // Get all the purchaseQuotationList where clients equals to (clientsId + 1)
        defaultPurchaseQuotationShouldNotBeFound("clientsId.equals=" + (clientsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchaseQuotationShouldBeFound(String filter) throws Exception {
        restPurchaseQuotationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseQuotation.getId().intValue())))
            .andExpect(jsonPath("$.[*].referenceNumber").value(hasItem(DEFAULT_REFERENCE_NUMBER)))
            .andExpect(jsonPath("$.[*].totalPOAmount").value(hasItem(DEFAULT_TOTAL_PO_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalGSTAmount").value(hasItem(DEFAULT_TOTAL_GST_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].poDate").value(hasItem(DEFAULT_PO_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedDeliveryDate").value(hasItem(DEFAULT_EXPECTED_DELIVERY_DATE.toString())))
            .andExpect(jsonPath("$.[*].orderStatus").value(hasItem(DEFAULT_ORDER_STATUS.toString())));

        // Check, that the count call also returns 1
        restPurchaseQuotationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchaseQuotationShouldNotBeFound(String filter) throws Exception {
        restPurchaseQuotationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchaseQuotationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseQuotation() throws Exception {
        // Get the purchaseQuotation
        restPurchaseQuotationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseQuotation() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();

        // Update the purchaseQuotation
        PurchaseQuotation updatedPurchaseQuotation = purchaseQuotationRepository.findById(purchaseQuotation.getId()).get();
        // Disconnect from session so that the updates on updatedPurchaseQuotation are not directly saved in db
        em.detach(updatedPurchaseQuotation);
        updatedPurchaseQuotation
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .totalPOAmount(UPDATED_TOTAL_PO_AMOUNT)
            .totalGSTAmount(UPDATED_TOTAL_GST_AMOUNT)
            .poDate(UPDATED_PO_DATE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .orderStatus(UPDATED_ORDER_STATUS);
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(updatedPurchaseQuotation);

        restPurchaseQuotationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseQuotationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
        PurchaseQuotation testPurchaseQuotation = purchaseQuotationList.get(purchaseQuotationList.size() - 1);
        assertThat(testPurchaseQuotation.getReferenceNumber()).isEqualTo(UPDATED_REFERENCE_NUMBER);
        assertThat(testPurchaseQuotation.getTotalPOAmount()).isEqualTo(UPDATED_TOTAL_PO_AMOUNT);
        assertThat(testPurchaseQuotation.getTotalGSTAmount()).isEqualTo(UPDATED_TOTAL_GST_AMOUNT);
        assertThat(testPurchaseQuotation.getPoDate()).isEqualTo(UPDATED_PO_DATE);
        assertThat(testPurchaseQuotation.getExpectedDeliveryDate()).isEqualTo(UPDATED_EXPECTED_DELIVERY_DATE);
        assertThat(testPurchaseQuotation.getOrderStatus()).isEqualTo(UPDATED_ORDER_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseQuotation() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();
        purchaseQuotation.setId(count.incrementAndGet());

        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseQuotationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseQuotationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseQuotation() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();
        purchaseQuotation.setId(count.incrementAndGet());

        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseQuotation() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();
        purchaseQuotation.setId(count.incrementAndGet());

        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseQuotationWithPatch() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();

        // Update the purchaseQuotation using partial update
        PurchaseQuotation partialUpdatedPurchaseQuotation = new PurchaseQuotation();
        partialUpdatedPurchaseQuotation.setId(purchaseQuotation.getId());

        partialUpdatedPurchaseQuotation.poDate(UPDATED_PO_DATE).orderStatus(UPDATED_ORDER_STATUS);

        restPurchaseQuotationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseQuotation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseQuotation))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
        PurchaseQuotation testPurchaseQuotation = purchaseQuotationList.get(purchaseQuotationList.size() - 1);
        assertThat(testPurchaseQuotation.getReferenceNumber()).isEqualTo(DEFAULT_REFERENCE_NUMBER);
        assertThat(testPurchaseQuotation.getTotalPOAmount()).isEqualTo(DEFAULT_TOTAL_PO_AMOUNT);
        assertThat(testPurchaseQuotation.getTotalGSTAmount()).isEqualTo(DEFAULT_TOTAL_GST_AMOUNT);
        assertThat(testPurchaseQuotation.getPoDate()).isEqualTo(UPDATED_PO_DATE);
        assertThat(testPurchaseQuotation.getExpectedDeliveryDate()).isEqualTo(DEFAULT_EXPECTED_DELIVERY_DATE);
        assertThat(testPurchaseQuotation.getOrderStatus()).isEqualTo(UPDATED_ORDER_STATUS);
    }

    @Test
    @Transactional
    void fullUpdatePurchaseQuotationWithPatch() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();

        // Update the purchaseQuotation using partial update
        PurchaseQuotation partialUpdatedPurchaseQuotation = new PurchaseQuotation();
        partialUpdatedPurchaseQuotation.setId(purchaseQuotation.getId());

        partialUpdatedPurchaseQuotation
            .referenceNumber(UPDATED_REFERENCE_NUMBER)
            .totalPOAmount(UPDATED_TOTAL_PO_AMOUNT)
            .totalGSTAmount(UPDATED_TOTAL_GST_AMOUNT)
            .poDate(UPDATED_PO_DATE)
            .expectedDeliveryDate(UPDATED_EXPECTED_DELIVERY_DATE)
            .orderStatus(UPDATED_ORDER_STATUS);

        restPurchaseQuotationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseQuotation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseQuotation))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
        PurchaseQuotation testPurchaseQuotation = purchaseQuotationList.get(purchaseQuotationList.size() - 1);
        assertThat(testPurchaseQuotation.getReferenceNumber()).isEqualTo(UPDATED_REFERENCE_NUMBER);
        assertThat(testPurchaseQuotation.getTotalPOAmount()).isEqualTo(UPDATED_TOTAL_PO_AMOUNT);
        assertThat(testPurchaseQuotation.getTotalGSTAmount()).isEqualTo(UPDATED_TOTAL_GST_AMOUNT);
        assertThat(testPurchaseQuotation.getPoDate()).isEqualTo(UPDATED_PO_DATE);
        assertThat(testPurchaseQuotation.getExpectedDeliveryDate()).isEqualTo(UPDATED_EXPECTED_DELIVERY_DATE);
        assertThat(testPurchaseQuotation.getOrderStatus()).isEqualTo(UPDATED_ORDER_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseQuotation() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();
        purchaseQuotation.setId(count.incrementAndGet());

        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseQuotationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseQuotationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseQuotation() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();
        purchaseQuotation.setId(count.incrementAndGet());

        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseQuotation() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationRepository.findAll().size();
        purchaseQuotation.setId(count.incrementAndGet());

        // Create the PurchaseQuotation
        PurchaseQuotationDTO purchaseQuotationDTO = purchaseQuotationMapper.toDto(purchaseQuotation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseQuotation in the database
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseQuotation() throws Exception {
        // Initialize the database
        purchaseQuotationRepository.saveAndFlush(purchaseQuotation);

        int databaseSizeBeforeDelete = purchaseQuotationRepository.findAll().size();

        // Delete the purchaseQuotation
        restPurchaseQuotationMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseQuotation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PurchaseQuotation> purchaseQuotationList = purchaseQuotationRepository.findAll();
        assertThat(purchaseQuotationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
