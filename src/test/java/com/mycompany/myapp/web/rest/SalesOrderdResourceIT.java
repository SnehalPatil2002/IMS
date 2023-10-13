package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Clients;
import com.mycompany.myapp.domain.SalesOrderd;
import com.mycompany.myapp.domain.enumeration.DeliveryStatus;
import com.mycompany.myapp.repository.SalesOrderdRepository;
import com.mycompany.myapp.service.criteria.SalesOrderdCriteria;
import com.mycompany.myapp.service.dto.SalesOrderdDTO;
import com.mycompany.myapp.service.mapper.SalesOrderdMapper;
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
 * Integration tests for the {@link SalesOrderdResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalesOrderdResourceIT {

    private static final Instant DEFAULT_ORDER_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ORDER_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_QUANTITY_SOLD = 1L;
    private static final Long UPDATED_QUANTITY_SOLD = 2L;
    private static final Long SMALLER_QUANTITY_SOLD = 1L - 1L;

    private static final Double DEFAULT_UNIT_PRICE = 1D;
    private static final Double UPDATED_UNIT_PRICE = 2D;
    private static final Double SMALLER_UNIT_PRICE = 1D - 1D;

    private static final Double DEFAULT_GST_PERCENTAGE = 1D;
    private static final Double UPDATED_GST_PERCENTAGE = 2D;
    private static final Double SMALLER_GST_PERCENTAGE = 1D - 1D;

    private static final Double DEFAULT_TOTAL_REVENUE = 1D;
    private static final Double UPDATED_TOTAL_REVENUE = 2D;
    private static final Double SMALLER_TOTAL_REVENUE = 1D - 1D;

    private static final DeliveryStatus DEFAULT_STATUS = DeliveryStatus.ORDERED;
    private static final DeliveryStatus UPDATED_STATUS = DeliveryStatus.SHIPPED;

    private static final String ENTITY_API_URL = "/api/sales-orderds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesOrderdRepository salesOrderdRepository;

    @Autowired
    private SalesOrderdMapper salesOrderdMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesOrderdMockMvc;

    private SalesOrderd salesOrderd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrderd createEntity(EntityManager em) {
        SalesOrderd salesOrderd = new SalesOrderd()
            .orderDate(DEFAULT_ORDER_DATE)
            .quantitySold(DEFAULT_QUANTITY_SOLD)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .gstPercentage(DEFAULT_GST_PERCENTAGE)
            .totalRevenue(DEFAULT_TOTAL_REVENUE)
            .status(DEFAULT_STATUS);
        return salesOrderd;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SalesOrderd createUpdatedEntity(EntityManager em) {
        SalesOrderd salesOrderd = new SalesOrderd()
            .orderDate(UPDATED_ORDER_DATE)
            .quantitySold(UPDATED_QUANTITY_SOLD)
            .unitPrice(UPDATED_UNIT_PRICE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .status(UPDATED_STATUS);
        return salesOrderd;
    }

    @BeforeEach
    public void initTest() {
        salesOrderd = createEntity(em);
    }

    @Test
    @Transactional
    void createSalesOrderd() throws Exception {
        int databaseSizeBeforeCreate = salesOrderdRepository.findAll().size();
        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);
        restSalesOrderdMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isCreated());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeCreate + 1);
        SalesOrderd testSalesOrderd = salesOrderdList.get(salesOrderdList.size() - 1);
        assertThat(testSalesOrderd.getOrderDate()).isEqualTo(DEFAULT_ORDER_DATE);
        assertThat(testSalesOrderd.getQuantitySold()).isEqualTo(DEFAULT_QUANTITY_SOLD);
        assertThat(testSalesOrderd.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testSalesOrderd.getGstPercentage()).isEqualTo(DEFAULT_GST_PERCENTAGE);
        assertThat(testSalesOrderd.getTotalRevenue()).isEqualTo(DEFAULT_TOTAL_REVENUE);
        assertThat(testSalesOrderd.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createSalesOrderdWithExistingId() throws Exception {
        // Create the SalesOrderd with an existing ID
        salesOrderd.setId(1L);
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        int databaseSizeBeforeCreate = salesOrderdRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesOrderdMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSalesOrderds() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList
        restSalesOrderdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesOrderd.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantitySold").value(hasItem(DEFAULT_QUANTITY_SOLD.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].gstPercentage").value(hasItem(DEFAULT_GST_PERCENTAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(DEFAULT_TOTAL_REVENUE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getSalesOrderd() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get the salesOrderd
        restSalesOrderdMockMvc
            .perform(get(ENTITY_API_URL_ID, salesOrderd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(salesOrderd.getId().intValue()))
            .andExpect(jsonPath("$.orderDate").value(DEFAULT_ORDER_DATE.toString()))
            .andExpect(jsonPath("$.quantitySold").value(DEFAULT_QUANTITY_SOLD.intValue()))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.gstPercentage").value(DEFAULT_GST_PERCENTAGE.doubleValue()))
            .andExpect(jsonPath("$.totalRevenue").value(DEFAULT_TOTAL_REVENUE.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getSalesOrderdsByIdFiltering() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        Long id = salesOrderd.getId();

        defaultSalesOrderdShouldBeFound("id.equals=" + id);
        defaultSalesOrderdShouldNotBeFound("id.notEquals=" + id);

        defaultSalesOrderdShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesOrderdShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesOrderdShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesOrderdShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByOrderDateIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where orderDate equals to DEFAULT_ORDER_DATE
        defaultSalesOrderdShouldBeFound("orderDate.equals=" + DEFAULT_ORDER_DATE);

        // Get all the salesOrderdList where orderDate equals to UPDATED_ORDER_DATE
        defaultSalesOrderdShouldNotBeFound("orderDate.equals=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByOrderDateIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where orderDate in DEFAULT_ORDER_DATE or UPDATED_ORDER_DATE
        defaultSalesOrderdShouldBeFound("orderDate.in=" + DEFAULT_ORDER_DATE + "," + UPDATED_ORDER_DATE);

        // Get all the salesOrderdList where orderDate equals to UPDATED_ORDER_DATE
        defaultSalesOrderdShouldNotBeFound("orderDate.in=" + UPDATED_ORDER_DATE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByOrderDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where orderDate is not null
        defaultSalesOrderdShouldBeFound("orderDate.specified=true");

        // Get all the salesOrderdList where orderDate is null
        defaultSalesOrderdShouldNotBeFound("orderDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold equals to DEFAULT_QUANTITY_SOLD
        defaultSalesOrderdShouldBeFound("quantitySold.equals=" + DEFAULT_QUANTITY_SOLD);

        // Get all the salesOrderdList where quantitySold equals to UPDATED_QUANTITY_SOLD
        defaultSalesOrderdShouldNotBeFound("quantitySold.equals=" + UPDATED_QUANTITY_SOLD);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold in DEFAULT_QUANTITY_SOLD or UPDATED_QUANTITY_SOLD
        defaultSalesOrderdShouldBeFound("quantitySold.in=" + DEFAULT_QUANTITY_SOLD + "," + UPDATED_QUANTITY_SOLD);

        // Get all the salesOrderdList where quantitySold equals to UPDATED_QUANTITY_SOLD
        defaultSalesOrderdShouldNotBeFound("quantitySold.in=" + UPDATED_QUANTITY_SOLD);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold is not null
        defaultSalesOrderdShouldBeFound("quantitySold.specified=true");

        // Get all the salesOrderdList where quantitySold is null
        defaultSalesOrderdShouldNotBeFound("quantitySold.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold is greater than or equal to DEFAULT_QUANTITY_SOLD
        defaultSalesOrderdShouldBeFound("quantitySold.greaterThanOrEqual=" + DEFAULT_QUANTITY_SOLD);

        // Get all the salesOrderdList where quantitySold is greater than or equal to UPDATED_QUANTITY_SOLD
        defaultSalesOrderdShouldNotBeFound("quantitySold.greaterThanOrEqual=" + UPDATED_QUANTITY_SOLD);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold is less than or equal to DEFAULT_QUANTITY_SOLD
        defaultSalesOrderdShouldBeFound("quantitySold.lessThanOrEqual=" + DEFAULT_QUANTITY_SOLD);

        // Get all the salesOrderdList where quantitySold is less than or equal to SMALLER_QUANTITY_SOLD
        defaultSalesOrderdShouldNotBeFound("quantitySold.lessThanOrEqual=" + SMALLER_QUANTITY_SOLD);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsLessThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold is less than DEFAULT_QUANTITY_SOLD
        defaultSalesOrderdShouldNotBeFound("quantitySold.lessThan=" + DEFAULT_QUANTITY_SOLD);

        // Get all the salesOrderdList where quantitySold is less than UPDATED_QUANTITY_SOLD
        defaultSalesOrderdShouldBeFound("quantitySold.lessThan=" + UPDATED_QUANTITY_SOLD);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByQuantitySoldIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where quantitySold is greater than DEFAULT_QUANTITY_SOLD
        defaultSalesOrderdShouldNotBeFound("quantitySold.greaterThan=" + DEFAULT_QUANTITY_SOLD);

        // Get all the salesOrderdList where quantitySold is greater than SMALLER_QUANTITY_SOLD
        defaultSalesOrderdShouldBeFound("quantitySold.greaterThan=" + SMALLER_QUANTITY_SOLD);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice equals to DEFAULT_UNIT_PRICE
        defaultSalesOrderdShouldBeFound("unitPrice.equals=" + DEFAULT_UNIT_PRICE);

        // Get all the salesOrderdList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultSalesOrderdShouldNotBeFound("unitPrice.equals=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice in DEFAULT_UNIT_PRICE or UPDATED_UNIT_PRICE
        defaultSalesOrderdShouldBeFound("unitPrice.in=" + DEFAULT_UNIT_PRICE + "," + UPDATED_UNIT_PRICE);

        // Get all the salesOrderdList where unitPrice equals to UPDATED_UNIT_PRICE
        defaultSalesOrderdShouldNotBeFound("unitPrice.in=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice is not null
        defaultSalesOrderdShouldBeFound("unitPrice.specified=true");

        // Get all the salesOrderdList where unitPrice is null
        defaultSalesOrderdShouldNotBeFound("unitPrice.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice is greater than or equal to DEFAULT_UNIT_PRICE
        defaultSalesOrderdShouldBeFound("unitPrice.greaterThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the salesOrderdList where unitPrice is greater than or equal to UPDATED_UNIT_PRICE
        defaultSalesOrderdShouldNotBeFound("unitPrice.greaterThanOrEqual=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice is less than or equal to DEFAULT_UNIT_PRICE
        defaultSalesOrderdShouldBeFound("unitPrice.lessThanOrEqual=" + DEFAULT_UNIT_PRICE);

        // Get all the salesOrderdList where unitPrice is less than or equal to SMALLER_UNIT_PRICE
        defaultSalesOrderdShouldNotBeFound("unitPrice.lessThanOrEqual=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice is less than DEFAULT_UNIT_PRICE
        defaultSalesOrderdShouldNotBeFound("unitPrice.lessThan=" + DEFAULT_UNIT_PRICE);

        // Get all the salesOrderdList where unitPrice is less than UPDATED_UNIT_PRICE
        defaultSalesOrderdShouldBeFound("unitPrice.lessThan=" + UPDATED_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByUnitPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where unitPrice is greater than DEFAULT_UNIT_PRICE
        defaultSalesOrderdShouldNotBeFound("unitPrice.greaterThan=" + DEFAULT_UNIT_PRICE);

        // Get all the salesOrderdList where unitPrice is greater than SMALLER_UNIT_PRICE
        defaultSalesOrderdShouldBeFound("unitPrice.greaterThan=" + SMALLER_UNIT_PRICE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage equals to DEFAULT_GST_PERCENTAGE
        defaultSalesOrderdShouldBeFound("gstPercentage.equals=" + DEFAULT_GST_PERCENTAGE);

        // Get all the salesOrderdList where gstPercentage equals to UPDATED_GST_PERCENTAGE
        defaultSalesOrderdShouldNotBeFound("gstPercentage.equals=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage in DEFAULT_GST_PERCENTAGE or UPDATED_GST_PERCENTAGE
        defaultSalesOrderdShouldBeFound("gstPercentage.in=" + DEFAULT_GST_PERCENTAGE + "," + UPDATED_GST_PERCENTAGE);

        // Get all the salesOrderdList where gstPercentage equals to UPDATED_GST_PERCENTAGE
        defaultSalesOrderdShouldNotBeFound("gstPercentage.in=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage is not null
        defaultSalesOrderdShouldBeFound("gstPercentage.specified=true");

        // Get all the salesOrderdList where gstPercentage is null
        defaultSalesOrderdShouldNotBeFound("gstPercentage.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage is greater than or equal to DEFAULT_GST_PERCENTAGE
        defaultSalesOrderdShouldBeFound("gstPercentage.greaterThanOrEqual=" + DEFAULT_GST_PERCENTAGE);

        // Get all the salesOrderdList where gstPercentage is greater than or equal to UPDATED_GST_PERCENTAGE
        defaultSalesOrderdShouldNotBeFound("gstPercentage.greaterThanOrEqual=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage is less than or equal to DEFAULT_GST_PERCENTAGE
        defaultSalesOrderdShouldBeFound("gstPercentage.lessThanOrEqual=" + DEFAULT_GST_PERCENTAGE);

        // Get all the salesOrderdList where gstPercentage is less than or equal to SMALLER_GST_PERCENTAGE
        defaultSalesOrderdShouldNotBeFound("gstPercentage.lessThanOrEqual=" + SMALLER_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsLessThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage is less than DEFAULT_GST_PERCENTAGE
        defaultSalesOrderdShouldNotBeFound("gstPercentage.lessThan=" + DEFAULT_GST_PERCENTAGE);

        // Get all the salesOrderdList where gstPercentage is less than UPDATED_GST_PERCENTAGE
        defaultSalesOrderdShouldBeFound("gstPercentage.lessThan=" + UPDATED_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByGstPercentageIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where gstPercentage is greater than DEFAULT_GST_PERCENTAGE
        defaultSalesOrderdShouldNotBeFound("gstPercentage.greaterThan=" + DEFAULT_GST_PERCENTAGE);

        // Get all the salesOrderdList where gstPercentage is greater than SMALLER_GST_PERCENTAGE
        defaultSalesOrderdShouldBeFound("gstPercentage.greaterThan=" + SMALLER_GST_PERCENTAGE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue equals to DEFAULT_TOTAL_REVENUE
        defaultSalesOrderdShouldBeFound("totalRevenue.equals=" + DEFAULT_TOTAL_REVENUE);

        // Get all the salesOrderdList where totalRevenue equals to UPDATED_TOTAL_REVENUE
        defaultSalesOrderdShouldNotBeFound("totalRevenue.equals=" + UPDATED_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue in DEFAULT_TOTAL_REVENUE or UPDATED_TOTAL_REVENUE
        defaultSalesOrderdShouldBeFound("totalRevenue.in=" + DEFAULT_TOTAL_REVENUE + "," + UPDATED_TOTAL_REVENUE);

        // Get all the salesOrderdList where totalRevenue equals to UPDATED_TOTAL_REVENUE
        defaultSalesOrderdShouldNotBeFound("totalRevenue.in=" + UPDATED_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue is not null
        defaultSalesOrderdShouldBeFound("totalRevenue.specified=true");

        // Get all the salesOrderdList where totalRevenue is null
        defaultSalesOrderdShouldNotBeFound("totalRevenue.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue is greater than or equal to DEFAULT_TOTAL_REVENUE
        defaultSalesOrderdShouldBeFound("totalRevenue.greaterThanOrEqual=" + DEFAULT_TOTAL_REVENUE);

        // Get all the salesOrderdList where totalRevenue is greater than or equal to UPDATED_TOTAL_REVENUE
        defaultSalesOrderdShouldNotBeFound("totalRevenue.greaterThanOrEqual=" + UPDATED_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue is less than or equal to DEFAULT_TOTAL_REVENUE
        defaultSalesOrderdShouldBeFound("totalRevenue.lessThanOrEqual=" + DEFAULT_TOTAL_REVENUE);

        // Get all the salesOrderdList where totalRevenue is less than or equal to SMALLER_TOTAL_REVENUE
        defaultSalesOrderdShouldNotBeFound("totalRevenue.lessThanOrEqual=" + SMALLER_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsLessThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue is less than DEFAULT_TOTAL_REVENUE
        defaultSalesOrderdShouldNotBeFound("totalRevenue.lessThan=" + DEFAULT_TOTAL_REVENUE);

        // Get all the salesOrderdList where totalRevenue is less than UPDATED_TOTAL_REVENUE
        defaultSalesOrderdShouldBeFound("totalRevenue.lessThan=" + UPDATED_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByTotalRevenueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where totalRevenue is greater than DEFAULT_TOTAL_REVENUE
        defaultSalesOrderdShouldNotBeFound("totalRevenue.greaterThan=" + DEFAULT_TOTAL_REVENUE);

        // Get all the salesOrderdList where totalRevenue is greater than SMALLER_TOTAL_REVENUE
        defaultSalesOrderdShouldBeFound("totalRevenue.greaterThan=" + SMALLER_TOTAL_REVENUE);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where status equals to DEFAULT_STATUS
        defaultSalesOrderdShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the salesOrderdList where status equals to UPDATED_STATUS
        defaultSalesOrderdShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultSalesOrderdShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the salesOrderdList where status equals to UPDATED_STATUS
        defaultSalesOrderdShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        // Get all the salesOrderdList where status is not null
        defaultSalesOrderdShouldBeFound("status.specified=true");

        // Get all the salesOrderdList where status is null
        defaultSalesOrderdShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesOrderdsByClientIsEqualToSomething() throws Exception {
        Clients client;
        if (TestUtil.findAll(em, Clients.class).isEmpty()) {
            salesOrderdRepository.saveAndFlush(salesOrderd);
            client = ClientsResourceIT.createEntity(em);
        } else {
            client = TestUtil.findAll(em, Clients.class).get(0);
        }
        em.persist(client);
        em.flush();
        salesOrderd.setClient(client);
        salesOrderdRepository.saveAndFlush(salesOrderd);
        Long clientId = client.getId();

        // Get all the salesOrderdList where client equals to clientId
        defaultSalesOrderdShouldBeFound("clientId.equals=" + clientId);

        // Get all the salesOrderdList where client equals to (clientId + 1)
        defaultSalesOrderdShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesOrderdShouldBeFound(String filter) throws Exception {
        restSalesOrderdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(salesOrderd.getId().intValue())))
            .andExpect(jsonPath("$.[*].orderDate").value(hasItem(DEFAULT_ORDER_DATE.toString())))
            .andExpect(jsonPath("$.[*].quantitySold").value(hasItem(DEFAULT_QUANTITY_SOLD.intValue())))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].gstPercentage").value(hasItem(DEFAULT_GST_PERCENTAGE.doubleValue())))
            .andExpect(jsonPath("$.[*].totalRevenue").value(hasItem(DEFAULT_TOTAL_REVENUE.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restSalesOrderdMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesOrderdShouldNotBeFound(String filter) throws Exception {
        restSalesOrderdMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesOrderdMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSalesOrderd() throws Exception {
        // Get the salesOrderd
        restSalesOrderdMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSalesOrderd() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();

        // Update the salesOrderd
        SalesOrderd updatedSalesOrderd = salesOrderdRepository.findById(salesOrderd.getId()).get();
        // Disconnect from session so that the updates on updatedSalesOrderd are not directly saved in db
        em.detach(updatedSalesOrderd);
        updatedSalesOrderd
            .orderDate(UPDATED_ORDER_DATE)
            .quantitySold(UPDATED_QUANTITY_SOLD)
            .unitPrice(UPDATED_UNIT_PRICE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .status(UPDATED_STATUS);
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(updatedSalesOrderd);

        restSalesOrderdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesOrderdDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
        SalesOrderd testSalesOrderd = salesOrderdList.get(salesOrderdList.size() - 1);
        assertThat(testSalesOrderd.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testSalesOrderd.getQuantitySold()).isEqualTo(UPDATED_QUANTITY_SOLD);
        assertThat(testSalesOrderd.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testSalesOrderd.getGstPercentage()).isEqualTo(UPDATED_GST_PERCENTAGE);
        assertThat(testSalesOrderd.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
        assertThat(testSalesOrderd.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingSalesOrderd() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();
        salesOrderd.setId(count.incrementAndGet());

        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesOrderdDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSalesOrderd() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();
        salesOrderd.setId(count.incrementAndGet());

        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderdMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSalesOrderd() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();
        salesOrderd.setId(count.incrementAndGet());

        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderdMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalesOrderdWithPatch() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();

        // Update the salesOrderd using partial update
        SalesOrderd partialUpdatedSalesOrderd = new SalesOrderd();
        partialUpdatedSalesOrderd.setId(salesOrderd.getId());

        partialUpdatedSalesOrderd.unitPrice(UPDATED_UNIT_PRICE).totalRevenue(UPDATED_TOTAL_REVENUE);

        restSalesOrderdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrderd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesOrderd))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
        SalesOrderd testSalesOrderd = salesOrderdList.get(salesOrderdList.size() - 1);
        assertThat(testSalesOrderd.getOrderDate()).isEqualTo(DEFAULT_ORDER_DATE);
        assertThat(testSalesOrderd.getQuantitySold()).isEqualTo(DEFAULT_QUANTITY_SOLD);
        assertThat(testSalesOrderd.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testSalesOrderd.getGstPercentage()).isEqualTo(DEFAULT_GST_PERCENTAGE);
        assertThat(testSalesOrderd.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
        assertThat(testSalesOrderd.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateSalesOrderdWithPatch() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();

        // Update the salesOrderd using partial update
        SalesOrderd partialUpdatedSalesOrderd = new SalesOrderd();
        partialUpdatedSalesOrderd.setId(salesOrderd.getId());

        partialUpdatedSalesOrderd
            .orderDate(UPDATED_ORDER_DATE)
            .quantitySold(UPDATED_QUANTITY_SOLD)
            .unitPrice(UPDATED_UNIT_PRICE)
            .gstPercentage(UPDATED_GST_PERCENTAGE)
            .totalRevenue(UPDATED_TOTAL_REVENUE)
            .status(UPDATED_STATUS);

        restSalesOrderdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSalesOrderd.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSalesOrderd))
            )
            .andExpect(status().isOk());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
        SalesOrderd testSalesOrderd = salesOrderdList.get(salesOrderdList.size() - 1);
        assertThat(testSalesOrderd.getOrderDate()).isEqualTo(UPDATED_ORDER_DATE);
        assertThat(testSalesOrderd.getQuantitySold()).isEqualTo(UPDATED_QUANTITY_SOLD);
        assertThat(testSalesOrderd.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testSalesOrderd.getGstPercentage()).isEqualTo(UPDATED_GST_PERCENTAGE);
        assertThat(testSalesOrderd.getTotalRevenue()).isEqualTo(UPDATED_TOTAL_REVENUE);
        assertThat(testSalesOrderd.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingSalesOrderd() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();
        salesOrderd.setId(count.incrementAndGet());

        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesOrderdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesOrderdDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSalesOrderd() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();
        salesOrderd.setId(count.incrementAndGet());

        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderdMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSalesOrderd() throws Exception {
        int databaseSizeBeforeUpdate = salesOrderdRepository.findAll().size();
        salesOrderd.setId(count.incrementAndGet());

        // Create the SalesOrderd
        SalesOrderdDTO salesOrderdDTO = salesOrderdMapper.toDto(salesOrderd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesOrderdMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(salesOrderdDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SalesOrderd in the database
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSalesOrderd() throws Exception {
        // Initialize the database
        salesOrderdRepository.saveAndFlush(salesOrderd);

        int databaseSizeBeforeDelete = salesOrderdRepository.findAll().size();

        // Delete the salesOrderd
        restSalesOrderdMockMvc
            .perform(delete(ENTITY_API_URL_ID, salesOrderd.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SalesOrderd> salesOrderdList = salesOrderdRepository.findAll();
        assertThat(salesOrderdList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
