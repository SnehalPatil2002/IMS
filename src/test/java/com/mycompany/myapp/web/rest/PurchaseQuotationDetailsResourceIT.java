package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PurchaseQuotation;
import com.mycompany.myapp.domain.PurchaseQuotationDetails;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.repository.PurchaseQuotationDetailsRepository;
import com.mycompany.myapp.service.criteria.PurchaseQuotationDetailsCriteria;
import com.mycompany.myapp.service.dto.PurchaseQuotationDetailsDTO;
import com.mycompany.myapp.service.mapper.PurchaseQuotationDetailsMapper;
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
 * Integration tests for the {@link PurchaseQuotationDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseQuotationDetailsResourceIT {

    private static final Long DEFAULT_QTY_ORDERED = 1L;
    private static final Long UPDATED_QTY_ORDERED = 2L;
    private static final Long SMALLER_QTY_ORDERED = 1L - 1L;

    private static final Integer DEFAULT_GST_TAX_PERCENTAGE = 1;
    private static final Integer UPDATED_GST_TAX_PERCENTAGE = 2;
    private static final Integer SMALLER_GST_TAX_PERCENTAGE = 1 - 1;

    private static final Double DEFAULT_PRICE_PER_UNIT = 1D;
    private static final Double UPDATED_PRICE_PER_UNIT = 2D;
    private static final Double SMALLER_PRICE_PER_UNIT = 1D - 1D;

    private static final Double DEFAULT_TOTAL_PRICE = 1D;
    private static final Double UPDATED_TOTAL_PRICE = 2D;
    private static final Double SMALLER_TOTAL_PRICE = 1D - 1D;

    private static final Double DEFAULT_DISCOUNT = 1D;
    private static final Double UPDATED_DISCOUNT = 2D;
    private static final Double SMALLER_DISCOUNT = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/purchase-quotation-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PurchaseQuotationDetailsRepository purchaseQuotationDetailsRepository;

    @Autowired
    private PurchaseQuotationDetailsMapper purchaseQuotationDetailsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseQuotationDetailsMockMvc;

    private PurchaseQuotationDetails purchaseQuotationDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseQuotationDetails createEntity(EntityManager em) {
        PurchaseQuotationDetails purchaseQuotationDetails = new PurchaseQuotationDetails()
            .qtyOrdered(DEFAULT_QTY_ORDERED)
            .gstTaxPercentage(DEFAULT_GST_TAX_PERCENTAGE)
            .pricePerUnit(DEFAULT_PRICE_PER_UNIT)
            .totalPrice(DEFAULT_TOTAL_PRICE)
            .discount(DEFAULT_DISCOUNT);
        return purchaseQuotationDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseQuotationDetails createUpdatedEntity(EntityManager em) {
        PurchaseQuotationDetails purchaseQuotationDetails = new PurchaseQuotationDetails()
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .gstTaxPercentage(UPDATED_GST_TAX_PERCENTAGE)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .discount(UPDATED_DISCOUNT);
        return purchaseQuotationDetails;
    }

    @BeforeEach
    public void initTest() {
        purchaseQuotationDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeCreate = purchaseQuotationDetailsRepository.findAll().size();
        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);
        restPurchaseQuotationDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        PurchaseQuotationDetails testPurchaseQuotationDetails = purchaseQuotationDetailsList.get(purchaseQuotationDetailsList.size() - 1);
        assertThat(testPurchaseQuotationDetails.getQtyOrdered()).isEqualTo(DEFAULT_QTY_ORDERED);
        assertThat(testPurchaseQuotationDetails.getGstTaxPercentage()).isEqualTo(DEFAULT_GST_TAX_PERCENTAGE);
        assertThat(testPurchaseQuotationDetails.getPricePerUnit()).isEqualTo(DEFAULT_PRICE_PER_UNIT);
        assertThat(testPurchaseQuotationDetails.getTotalPrice()).isEqualTo(DEFAULT_TOTAL_PRICE);
        assertThat(testPurchaseQuotationDetails.getDiscount()).isEqualTo(DEFAULT_DISCOUNT);
    }

    @Test
    @Transactional
    void createPurchaseQuotationDetailsWithExistingId() throws Exception {
        // Create the PurchaseQuotationDetails with an existing ID
        purchaseQuotationDetails.setId(1L);
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        int databaseSizeBeforeCreate = purchaseQuotationDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseQuotationDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetails() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList
        restPurchaseQuotationDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseQuotationDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].qtyOrdered").value(hasItem(DEFAULT_QTY_ORDERED.intValue())))
            .andExpect(jsonPath("$.[*].gstTaxPercentage").value(hasItem(DEFAULT_GST_TAX_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(DEFAULT_PRICE_PER_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())));
    }

    @Test
    @Transactional
    void getPurchaseQuotationDetails() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get the purchaseQuotationDetails
        restPurchaseQuotationDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseQuotationDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseQuotationDetails.getId().intValue()))
            .andExpect(jsonPath("$.qtyOrdered").value(DEFAULT_QTY_ORDERED.intValue()))
            .andExpect(jsonPath("$.gstTaxPercentage").value(DEFAULT_GST_TAX_PERCENTAGE))
            .andExpect(jsonPath("$.pricePerUnit").value(DEFAULT_PRICE_PER_UNIT.doubleValue()))
            .andExpect(jsonPath("$.totalPrice").value(DEFAULT_TOTAL_PRICE.doubleValue()))
            .andExpect(jsonPath("$.discount").value(DEFAULT_DISCOUNT.doubleValue()));
    }

    @Test
    @Transactional
    void getPurchaseQuotationDetailsByIdFiltering() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        Long id = purchaseQuotationDetails.getId();

        defaultPurchaseQuotationDetailsShouldBeFound("id.equals=" + id);
        defaultPurchaseQuotationDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultPurchaseQuotationDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPurchaseQuotationDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultPurchaseQuotationDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPurchaseQuotationDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered equals to DEFAULT_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.equals=" + DEFAULT_QTY_ORDERED);

        // Get all the purchaseQuotationDetailsList where qtyOrdered equals to UPDATED_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.equals=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered in DEFAULT_QTY_ORDERED or UPDATED_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.in=" + DEFAULT_QTY_ORDERED + "," + UPDATED_QTY_ORDERED);

        // Get all the purchaseQuotationDetailsList where qtyOrdered equals to UPDATED_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.in=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is not null
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.specified=true");

        // Get all the purchaseQuotationDetailsList where qtyOrdered is null
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is greater than or equal to DEFAULT_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.greaterThanOrEqual=" + DEFAULT_QTY_ORDERED);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is greater than or equal to UPDATED_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.greaterThanOrEqual=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is less than or equal to DEFAULT_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.lessThanOrEqual=" + DEFAULT_QTY_ORDERED);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is less than or equal to SMALLER_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.lessThanOrEqual=" + SMALLER_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is less than DEFAULT_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.lessThan=" + DEFAULT_QTY_ORDERED);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is less than UPDATED_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.lessThan=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByQtyOrderedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is greater than DEFAULT_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldNotBeFound("qtyOrdered.greaterThan=" + DEFAULT_QTY_ORDERED);

        // Get all the purchaseQuotationDetailsList where qtyOrdered is greater than SMALLER_QTY_ORDERED
        defaultPurchaseQuotationDetailsShouldBeFound("qtyOrdered.greaterThan=" + SMALLER_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage equals to DEFAULT_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldBeFound("gstTaxPercentage.equals=" + DEFAULT_GST_TAX_PERCENTAGE);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage equals to UPDATED_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.equals=" + UPDATED_GST_TAX_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage in DEFAULT_GST_TAX_PERCENTAGE or UPDATED_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldBeFound(
            "gstTaxPercentage.in=" + DEFAULT_GST_TAX_PERCENTAGE + "," + UPDATED_GST_TAX_PERCENTAGE
        );

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage equals to UPDATED_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.in=" + UPDATED_GST_TAX_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is not null
        defaultPurchaseQuotationDetailsShouldBeFound("gstTaxPercentage.specified=true");

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is null
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is greater than or equal to DEFAULT_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldBeFound("gstTaxPercentage.greaterThanOrEqual=" + DEFAULT_GST_TAX_PERCENTAGE);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is greater than or equal to UPDATED_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.greaterThanOrEqual=" + UPDATED_GST_TAX_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is less than or equal to DEFAULT_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldBeFound("gstTaxPercentage.lessThanOrEqual=" + DEFAULT_GST_TAX_PERCENTAGE);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is less than or equal to SMALLER_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.lessThanOrEqual=" + SMALLER_GST_TAX_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is less than DEFAULT_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.lessThan=" + DEFAULT_GST_TAX_PERCENTAGE);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is less than UPDATED_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldBeFound("gstTaxPercentage.lessThan=" + UPDATED_GST_TAX_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByGstTaxPercentageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is greater than DEFAULT_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldNotBeFound("gstTaxPercentage.greaterThan=" + DEFAULT_GST_TAX_PERCENTAGE);

        // Get all the purchaseQuotationDetailsList where gstTaxPercentage is greater than SMALLER_GST_TAX_PERCENTAGE
        defaultPurchaseQuotationDetailsShouldBeFound("gstTaxPercentage.greaterThan=" + SMALLER_GST_TAX_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit equals to DEFAULT_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.equals=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the purchaseQuotationDetailsList where pricePerUnit equals to UPDATED_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.equals=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit in DEFAULT_PRICE_PER_UNIT or UPDATED_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.in=" + DEFAULT_PRICE_PER_UNIT + "," + UPDATED_PRICE_PER_UNIT);

        // Get all the purchaseQuotationDetailsList where pricePerUnit equals to UPDATED_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.in=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is not null
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.specified=true");

        // Get all the purchaseQuotationDetailsList where pricePerUnit is null
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is greater than or equal to DEFAULT_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.greaterThanOrEqual=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is greater than or equal to UPDATED_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.greaterThanOrEqual=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is less than or equal to DEFAULT_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.lessThanOrEqual=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is less than or equal to SMALLER_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.lessThanOrEqual=" + SMALLER_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is less than DEFAULT_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.lessThan=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is less than UPDATED_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.lessThan=" + UPDATED_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPricePerUnitIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is greater than DEFAULT_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldNotBeFound("pricePerUnit.greaterThan=" + DEFAULT_PRICE_PER_UNIT);

        // Get all the purchaseQuotationDetailsList where pricePerUnit is greater than SMALLER_PRICE_PER_UNIT
        defaultPurchaseQuotationDetailsShouldBeFound("pricePerUnit.greaterThan=" + SMALLER_PRICE_PER_UNIT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice equals to DEFAULT_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.equals=" + DEFAULT_TOTAL_PRICE);

        // Get all the purchaseQuotationDetailsList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.equals=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice in DEFAULT_TOTAL_PRICE or UPDATED_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.in=" + DEFAULT_TOTAL_PRICE + "," + UPDATED_TOTAL_PRICE);

        // Get all the purchaseQuotationDetailsList where totalPrice equals to UPDATED_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.in=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice is not null
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.specified=true");

        // Get all the purchaseQuotationDetailsList where totalPrice is null
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice is greater than or equal to DEFAULT_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.greaterThanOrEqual=" + DEFAULT_TOTAL_PRICE);

        // Get all the purchaseQuotationDetailsList where totalPrice is greater than or equal to UPDATED_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.greaterThanOrEqual=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice is less than or equal to DEFAULT_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.lessThanOrEqual=" + DEFAULT_TOTAL_PRICE);

        // Get all the purchaseQuotationDetailsList where totalPrice is less than or equal to SMALLER_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.lessThanOrEqual=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice is less than DEFAULT_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.lessThan=" + DEFAULT_TOTAL_PRICE);

        // Get all the purchaseQuotationDetailsList where totalPrice is less than UPDATED_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.lessThan=" + UPDATED_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByTotalPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where totalPrice is greater than DEFAULT_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldNotBeFound("totalPrice.greaterThan=" + DEFAULT_TOTAL_PRICE);

        // Get all the purchaseQuotationDetailsList where totalPrice is greater than SMALLER_TOTAL_PRICE
        defaultPurchaseQuotationDetailsShouldBeFound("totalPrice.greaterThan=" + SMALLER_TOTAL_PRICE);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount equals to DEFAULT_DISCOUNT
        defaultPurchaseQuotationDetailsShouldBeFound("discount.equals=" + DEFAULT_DISCOUNT);

        // Get all the purchaseQuotationDetailsList where discount equals to UPDATED_DISCOUNT
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.equals=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount in DEFAULT_DISCOUNT or UPDATED_DISCOUNT
        defaultPurchaseQuotationDetailsShouldBeFound("discount.in=" + DEFAULT_DISCOUNT + "," + UPDATED_DISCOUNT);

        // Get all the purchaseQuotationDetailsList where discount equals to UPDATED_DISCOUNT
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.in=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount is not null
        defaultPurchaseQuotationDetailsShouldBeFound("discount.specified=true");

        // Get all the purchaseQuotationDetailsList where discount is null
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount is greater than or equal to DEFAULT_DISCOUNT
        defaultPurchaseQuotationDetailsShouldBeFound("discount.greaterThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the purchaseQuotationDetailsList where discount is greater than or equal to UPDATED_DISCOUNT
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.greaterThanOrEqual=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount is less than or equal to DEFAULT_DISCOUNT
        defaultPurchaseQuotationDetailsShouldBeFound("discount.lessThanOrEqual=" + DEFAULT_DISCOUNT);

        // Get all the purchaseQuotationDetailsList where discount is less than or equal to SMALLER_DISCOUNT
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.lessThanOrEqual=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount is less than DEFAULT_DISCOUNT
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.lessThan=" + DEFAULT_DISCOUNT);

        // Get all the purchaseQuotationDetailsList where discount is less than UPDATED_DISCOUNT
        defaultPurchaseQuotationDetailsShouldBeFound("discount.lessThan=" + UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByDiscountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        // Get all the purchaseQuotationDetailsList where discount is greater than DEFAULT_DISCOUNT
        defaultPurchaseQuotationDetailsShouldNotBeFound("discount.greaterThan=" + DEFAULT_DISCOUNT);

        // Get all the purchaseQuotationDetailsList where discount is greater than SMALLER_DISCOUNT
        defaultPurchaseQuotationDetailsShouldBeFound("discount.greaterThan=" + SMALLER_DISCOUNT);
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByRawMaterialIsEqualToSomething() throws Exception {
        RawMaterial rawMaterial;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);
            rawMaterial = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterial = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterial);
        em.flush();
        purchaseQuotationDetails.setRawMaterial(rawMaterial);
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);
        Long rawMaterialId = rawMaterial.getId();

        // Get all the purchaseQuotationDetailsList where rawMaterial equals to rawMaterialId
        defaultPurchaseQuotationDetailsShouldBeFound("rawMaterialId.equals=" + rawMaterialId);

        // Get all the purchaseQuotationDetailsList where rawMaterial equals to (rawMaterialId + 1)
        defaultPurchaseQuotationDetailsShouldNotBeFound("rawMaterialId.equals=" + (rawMaterialId + 1));
    }

    @Test
    @Transactional
    void getAllPurchaseQuotationDetailsByPurchaseQuotationIsEqualToSomething() throws Exception {
        PurchaseQuotation purchaseQuotation;
        if (TestUtil.findAll(em, PurchaseQuotation.class).isEmpty()) {
            purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);
            purchaseQuotation = PurchaseQuotationResourceIT.createEntity(em);
        } else {
            purchaseQuotation = TestUtil.findAll(em, PurchaseQuotation.class).get(0);
        }
        em.persist(purchaseQuotation);
        em.flush();
        purchaseQuotationDetails.setPurchaseQuotation(purchaseQuotation);
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);
        Long purchaseQuotationId = purchaseQuotation.getId();

        // Get all the purchaseQuotationDetailsList where purchaseQuotation equals to purchaseQuotationId
        defaultPurchaseQuotationDetailsShouldBeFound("purchaseQuotationId.equals=" + purchaseQuotationId);

        // Get all the purchaseQuotationDetailsList where purchaseQuotation equals to (purchaseQuotationId + 1)
        defaultPurchaseQuotationDetailsShouldNotBeFound("purchaseQuotationId.equals=" + (purchaseQuotationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchaseQuotationDetailsShouldBeFound(String filter) throws Exception {
        restPurchaseQuotationDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseQuotationDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].qtyOrdered").value(hasItem(DEFAULT_QTY_ORDERED.intValue())))
            .andExpect(jsonPath("$.[*].gstTaxPercentage").value(hasItem(DEFAULT_GST_TAX_PERCENTAGE)))
            .andExpect(jsonPath("$.[*].pricePerUnit").value(hasItem(DEFAULT_PRICE_PER_UNIT.doubleValue())))
            .andExpect(jsonPath("$.[*].totalPrice").value(hasItem(DEFAULT_TOTAL_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].discount").value(hasItem(DEFAULT_DISCOUNT.doubleValue())));

        // Check, that the count call also returns 1
        restPurchaseQuotationDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchaseQuotationDetailsShouldNotBeFound(String filter) throws Exception {
        restPurchaseQuotationDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchaseQuotationDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseQuotationDetails() throws Exception {
        // Get the purchaseQuotationDetails
        restPurchaseQuotationDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseQuotationDetails() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();

        // Update the purchaseQuotationDetails
        PurchaseQuotationDetails updatedPurchaseQuotationDetails = purchaseQuotationDetailsRepository
            .findById(purchaseQuotationDetails.getId())
            .get();
        // Disconnect from session so that the updates on updatedPurchaseQuotationDetails are not directly saved in db
        em.detach(updatedPurchaseQuotationDetails);
        updatedPurchaseQuotationDetails
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .gstTaxPercentage(UPDATED_GST_TAX_PERCENTAGE)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .discount(UPDATED_DISCOUNT);
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(updatedPurchaseQuotationDetails);

        restPurchaseQuotationDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseQuotationDetailsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
        PurchaseQuotationDetails testPurchaseQuotationDetails = purchaseQuotationDetailsList.get(purchaseQuotationDetailsList.size() - 1);
        assertThat(testPurchaseQuotationDetails.getQtyOrdered()).isEqualTo(UPDATED_QTY_ORDERED);
        assertThat(testPurchaseQuotationDetails.getGstTaxPercentage()).isEqualTo(UPDATED_GST_TAX_PERCENTAGE);
        assertThat(testPurchaseQuotationDetails.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
        assertThat(testPurchaseQuotationDetails.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
        assertThat(testPurchaseQuotationDetails.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();
        purchaseQuotationDetails.setId(count.incrementAndGet());

        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseQuotationDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseQuotationDetailsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();
        purchaseQuotationDetails.setId(count.incrementAndGet());

        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();
        purchaseQuotationDetails.setId(count.incrementAndGet());

        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationDetailsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseQuotationDetailsWithPatch() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();

        // Update the purchaseQuotationDetails using partial update
        PurchaseQuotationDetails partialUpdatedPurchaseQuotationDetails = new PurchaseQuotationDetails();
        partialUpdatedPurchaseQuotationDetails.setId(purchaseQuotationDetails.getId());

        partialUpdatedPurchaseQuotationDetails
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .gstTaxPercentage(UPDATED_GST_TAX_PERCENTAGE)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .discount(UPDATED_DISCOUNT);

        restPurchaseQuotationDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseQuotationDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseQuotationDetails))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
        PurchaseQuotationDetails testPurchaseQuotationDetails = purchaseQuotationDetailsList.get(purchaseQuotationDetailsList.size() - 1);
        assertThat(testPurchaseQuotationDetails.getQtyOrdered()).isEqualTo(UPDATED_QTY_ORDERED);
        assertThat(testPurchaseQuotationDetails.getGstTaxPercentage()).isEqualTo(UPDATED_GST_TAX_PERCENTAGE);
        assertThat(testPurchaseQuotationDetails.getPricePerUnit()).isEqualTo(DEFAULT_PRICE_PER_UNIT);
        assertThat(testPurchaseQuotationDetails.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
        assertThat(testPurchaseQuotationDetails.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void fullUpdatePurchaseQuotationDetailsWithPatch() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();

        // Update the purchaseQuotationDetails using partial update
        PurchaseQuotationDetails partialUpdatedPurchaseQuotationDetails = new PurchaseQuotationDetails();
        partialUpdatedPurchaseQuotationDetails.setId(purchaseQuotationDetails.getId());

        partialUpdatedPurchaseQuotationDetails
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .gstTaxPercentage(UPDATED_GST_TAX_PERCENTAGE)
            .pricePerUnit(UPDATED_PRICE_PER_UNIT)
            .totalPrice(UPDATED_TOTAL_PRICE)
            .discount(UPDATED_DISCOUNT);

        restPurchaseQuotationDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseQuotationDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseQuotationDetails))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
        PurchaseQuotationDetails testPurchaseQuotationDetails = purchaseQuotationDetailsList.get(purchaseQuotationDetailsList.size() - 1);
        assertThat(testPurchaseQuotationDetails.getQtyOrdered()).isEqualTo(UPDATED_QTY_ORDERED);
        assertThat(testPurchaseQuotationDetails.getGstTaxPercentage()).isEqualTo(UPDATED_GST_TAX_PERCENTAGE);
        assertThat(testPurchaseQuotationDetails.getPricePerUnit()).isEqualTo(UPDATED_PRICE_PER_UNIT);
        assertThat(testPurchaseQuotationDetails.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
        assertThat(testPurchaseQuotationDetails.getDiscount()).isEqualTo(UPDATED_DISCOUNT);
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();
        purchaseQuotationDetails.setId(count.incrementAndGet());

        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseQuotationDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseQuotationDetailsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();
        purchaseQuotationDetails.setId(count.incrementAndGet());

        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseQuotationDetails() throws Exception {
        int databaseSizeBeforeUpdate = purchaseQuotationDetailsRepository.findAll().size();
        purchaseQuotationDetails.setId(count.incrementAndGet());

        // Create the PurchaseQuotationDetails
        PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO = purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseQuotationDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseQuotationDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseQuotationDetails in the database
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseQuotationDetails() throws Exception {
        // Initialize the database
        purchaseQuotationDetailsRepository.saveAndFlush(purchaseQuotationDetails);

        int databaseSizeBeforeDelete = purchaseQuotationDetailsRepository.findAll().size();

        // Delete the purchaseQuotationDetails
        restPurchaseQuotationDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseQuotationDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PurchaseQuotationDetails> purchaseQuotationDetailsList = purchaseQuotationDetailsRepository.findAll();
        assertThat(purchaseQuotationDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
