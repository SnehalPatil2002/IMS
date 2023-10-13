package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.OrderRecieved;
import com.mycompany.myapp.domain.PurchaseQuotation;
import com.mycompany.myapp.repository.OrderRecievedRepository;
import com.mycompany.myapp.service.criteria.OrderRecievedCriteria;
import com.mycompany.myapp.service.dto.OrderRecievedDTO;
import com.mycompany.myapp.service.mapper.OrderRecievedMapper;
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
 * Integration tests for the {@link OrderRecievedResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderRecievedResourceIT {

    private static final Instant DEFAULT_OR_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_OR_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_QTY_ORDERED = 1L;
    private static final Long UPDATED_QTY_ORDERED = 2L;
    private static final Long SMALLER_QTY_ORDERED = 1L - 1L;

    private static final Long DEFAULT_QTY_RECIEVED = 1L;
    private static final Long UPDATED_QTY_RECIEVED = 2L;
    private static final Long SMALLER_QTY_RECIEVED = 1L - 1L;

    private static final Instant DEFAULT_MANUFACTURING_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MANUFACTURING_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_QTY_APPROVED = 1L;
    private static final Long UPDATED_QTY_APPROVED = 2L;
    private static final Long SMALLER_QTY_APPROVED = 1L - 1L;

    private static final Long DEFAULT_QTY_REJECTED = 1L;
    private static final Long UPDATED_QTY_REJECTED = 2L;
    private static final Long SMALLER_QTY_REJECTED = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/order-recieveds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderRecievedRepository orderRecievedRepository;

    @Autowired
    private OrderRecievedMapper orderRecievedMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderRecievedMockMvc;

    private OrderRecieved orderRecieved;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderRecieved createEntity(EntityManager em) {
        OrderRecieved orderRecieved = new OrderRecieved()
            .orDate(DEFAULT_OR_DATE)
            .qtyOrdered(DEFAULT_QTY_ORDERED)
            .qtyRecieved(DEFAULT_QTY_RECIEVED)
            .manufacturingDate(DEFAULT_MANUFACTURING_DATE)
            .expiryDate(DEFAULT_EXPIRY_DATE)
            .qtyApproved(DEFAULT_QTY_APPROVED)
            .qtyRejected(DEFAULT_QTY_REJECTED);
        return orderRecieved;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderRecieved createUpdatedEntity(EntityManager em) {
        OrderRecieved orderRecieved = new OrderRecieved()
            .orDate(UPDATED_OR_DATE)
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .qtyRecieved(UPDATED_QTY_RECIEVED)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qtyApproved(UPDATED_QTY_APPROVED)
            .qtyRejected(UPDATED_QTY_REJECTED);
        return orderRecieved;
    }

    @BeforeEach
    public void initTest() {
        orderRecieved = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderRecieved() throws Exception {
        int databaseSizeBeforeCreate = orderRecievedRepository.findAll().size();
        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);
        restOrderRecievedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeCreate + 1);
        OrderRecieved testOrderRecieved = orderRecievedList.get(orderRecievedList.size() - 1);
        assertThat(testOrderRecieved.getOrDate()).isEqualTo(DEFAULT_OR_DATE);
        assertThat(testOrderRecieved.getQtyOrdered()).isEqualTo(DEFAULT_QTY_ORDERED);
        assertThat(testOrderRecieved.getQtyRecieved()).isEqualTo(DEFAULT_QTY_RECIEVED);
        assertThat(testOrderRecieved.getManufacturingDate()).isEqualTo(DEFAULT_MANUFACTURING_DATE);
        assertThat(testOrderRecieved.getExpiryDate()).isEqualTo(DEFAULT_EXPIRY_DATE);
        assertThat(testOrderRecieved.getQtyApproved()).isEqualTo(DEFAULT_QTY_APPROVED);
        assertThat(testOrderRecieved.getQtyRejected()).isEqualTo(DEFAULT_QTY_REJECTED);
    }

    @Test
    @Transactional
    void createOrderRecievedWithExistingId() throws Exception {
        // Create the OrderRecieved with an existing ID
        orderRecieved.setId(1L);
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        int databaseSizeBeforeCreate = orderRecievedRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderRecievedMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllOrderRecieveds() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList
        restOrderRecievedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderRecieved.getId().intValue())))
            .andExpect(jsonPath("$.[*].orDate").value(hasItem(DEFAULT_OR_DATE.toString())))
            .andExpect(jsonPath("$.[*].qtyOrdered").value(hasItem(DEFAULT_QTY_ORDERED.intValue())))
            .andExpect(jsonPath("$.[*].qtyRecieved").value(hasItem(DEFAULT_QTY_RECIEVED.intValue())))
            .andExpect(jsonPath("$.[*].manufacturingDate").value(hasItem(DEFAULT_MANUFACTURING_DATE.toString())))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].qtyApproved").value(hasItem(DEFAULT_QTY_APPROVED.intValue())))
            .andExpect(jsonPath("$.[*].qtyRejected").value(hasItem(DEFAULT_QTY_REJECTED.intValue())));
    }

    @Test
    @Transactional
    void getOrderRecieved() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get the orderRecieved
        restOrderRecievedMockMvc
            .perform(get(ENTITY_API_URL_ID, orderRecieved.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderRecieved.getId().intValue()))
            .andExpect(jsonPath("$.orDate").value(DEFAULT_OR_DATE.toString()))
            .andExpect(jsonPath("$.qtyOrdered").value(DEFAULT_QTY_ORDERED.intValue()))
            .andExpect(jsonPath("$.qtyRecieved").value(DEFAULT_QTY_RECIEVED.intValue()))
            .andExpect(jsonPath("$.manufacturingDate").value(DEFAULT_MANUFACTURING_DATE.toString()))
            .andExpect(jsonPath("$.expiryDate").value(DEFAULT_EXPIRY_DATE.toString()))
            .andExpect(jsonPath("$.qtyApproved").value(DEFAULT_QTY_APPROVED.intValue()))
            .andExpect(jsonPath("$.qtyRejected").value(DEFAULT_QTY_REJECTED.intValue()));
    }

    @Test
    @Transactional
    void getOrderRecievedsByIdFiltering() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        Long id = orderRecieved.getId();

        defaultOrderRecievedShouldBeFound("id.equals=" + id);
        defaultOrderRecievedShouldNotBeFound("id.notEquals=" + id);

        defaultOrderRecievedShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderRecievedShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderRecievedShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderRecievedShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByOrDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where orDate equals to DEFAULT_OR_DATE
        defaultOrderRecievedShouldBeFound("orDate.equals=" + DEFAULT_OR_DATE);

        // Get all the orderRecievedList where orDate equals to UPDATED_OR_DATE
        defaultOrderRecievedShouldNotBeFound("orDate.equals=" + UPDATED_OR_DATE);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByOrDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where orDate in DEFAULT_OR_DATE or UPDATED_OR_DATE
        defaultOrderRecievedShouldBeFound("orDate.in=" + DEFAULT_OR_DATE + "," + UPDATED_OR_DATE);

        // Get all the orderRecievedList where orDate equals to UPDATED_OR_DATE
        defaultOrderRecievedShouldNotBeFound("orDate.in=" + UPDATED_OR_DATE);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByOrDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where orDate is not null
        defaultOrderRecievedShouldBeFound("orDate.specified=true");

        // Get all the orderRecievedList where orDate is null
        defaultOrderRecievedShouldNotBeFound("orDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered equals to DEFAULT_QTY_ORDERED
        defaultOrderRecievedShouldBeFound("qtyOrdered.equals=" + DEFAULT_QTY_ORDERED);

        // Get all the orderRecievedList where qtyOrdered equals to UPDATED_QTY_ORDERED
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.equals=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered in DEFAULT_QTY_ORDERED or UPDATED_QTY_ORDERED
        defaultOrderRecievedShouldBeFound("qtyOrdered.in=" + DEFAULT_QTY_ORDERED + "," + UPDATED_QTY_ORDERED);

        // Get all the orderRecievedList where qtyOrdered equals to UPDATED_QTY_ORDERED
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.in=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered is not null
        defaultOrderRecievedShouldBeFound("qtyOrdered.specified=true");

        // Get all the orderRecievedList where qtyOrdered is null
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered is greater than or equal to DEFAULT_QTY_ORDERED
        defaultOrderRecievedShouldBeFound("qtyOrdered.greaterThanOrEqual=" + DEFAULT_QTY_ORDERED);

        // Get all the orderRecievedList where qtyOrdered is greater than or equal to UPDATED_QTY_ORDERED
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.greaterThanOrEqual=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered is less than or equal to DEFAULT_QTY_ORDERED
        defaultOrderRecievedShouldBeFound("qtyOrdered.lessThanOrEqual=" + DEFAULT_QTY_ORDERED);

        // Get all the orderRecievedList where qtyOrdered is less than or equal to SMALLER_QTY_ORDERED
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.lessThanOrEqual=" + SMALLER_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered is less than DEFAULT_QTY_ORDERED
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.lessThan=" + DEFAULT_QTY_ORDERED);

        // Get all the orderRecievedList where qtyOrdered is less than UPDATED_QTY_ORDERED
        defaultOrderRecievedShouldBeFound("qtyOrdered.lessThan=" + UPDATED_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyOrderedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyOrdered is greater than DEFAULT_QTY_ORDERED
        defaultOrderRecievedShouldNotBeFound("qtyOrdered.greaterThan=" + DEFAULT_QTY_ORDERED);

        // Get all the orderRecievedList where qtyOrdered is greater than SMALLER_QTY_ORDERED
        defaultOrderRecievedShouldBeFound("qtyOrdered.greaterThan=" + SMALLER_QTY_ORDERED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved equals to DEFAULT_QTY_RECIEVED
        defaultOrderRecievedShouldBeFound("qtyRecieved.equals=" + DEFAULT_QTY_RECIEVED);

        // Get all the orderRecievedList where qtyRecieved equals to UPDATED_QTY_RECIEVED
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.equals=" + UPDATED_QTY_RECIEVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved in DEFAULT_QTY_RECIEVED or UPDATED_QTY_RECIEVED
        defaultOrderRecievedShouldBeFound("qtyRecieved.in=" + DEFAULT_QTY_RECIEVED + "," + UPDATED_QTY_RECIEVED);

        // Get all the orderRecievedList where qtyRecieved equals to UPDATED_QTY_RECIEVED
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.in=" + UPDATED_QTY_RECIEVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved is not null
        defaultOrderRecievedShouldBeFound("qtyRecieved.specified=true");

        // Get all the orderRecievedList where qtyRecieved is null
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved is greater than or equal to DEFAULT_QTY_RECIEVED
        defaultOrderRecievedShouldBeFound("qtyRecieved.greaterThanOrEqual=" + DEFAULT_QTY_RECIEVED);

        // Get all the orderRecievedList where qtyRecieved is greater than or equal to UPDATED_QTY_RECIEVED
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.greaterThanOrEqual=" + UPDATED_QTY_RECIEVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved is less than or equal to DEFAULT_QTY_RECIEVED
        defaultOrderRecievedShouldBeFound("qtyRecieved.lessThanOrEqual=" + DEFAULT_QTY_RECIEVED);

        // Get all the orderRecievedList where qtyRecieved is less than or equal to SMALLER_QTY_RECIEVED
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.lessThanOrEqual=" + SMALLER_QTY_RECIEVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved is less than DEFAULT_QTY_RECIEVED
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.lessThan=" + DEFAULT_QTY_RECIEVED);

        // Get all the orderRecievedList where qtyRecieved is less than UPDATED_QTY_RECIEVED
        defaultOrderRecievedShouldBeFound("qtyRecieved.lessThan=" + UPDATED_QTY_RECIEVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRecievedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRecieved is greater than DEFAULT_QTY_RECIEVED
        defaultOrderRecievedShouldNotBeFound("qtyRecieved.greaterThan=" + DEFAULT_QTY_RECIEVED);

        // Get all the orderRecievedList where qtyRecieved is greater than SMALLER_QTY_RECIEVED
        defaultOrderRecievedShouldBeFound("qtyRecieved.greaterThan=" + SMALLER_QTY_RECIEVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByManufacturingDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where manufacturingDate equals to DEFAULT_MANUFACTURING_DATE
        defaultOrderRecievedShouldBeFound("manufacturingDate.equals=" + DEFAULT_MANUFACTURING_DATE);

        // Get all the orderRecievedList where manufacturingDate equals to UPDATED_MANUFACTURING_DATE
        defaultOrderRecievedShouldNotBeFound("manufacturingDate.equals=" + UPDATED_MANUFACTURING_DATE);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByManufacturingDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where manufacturingDate in DEFAULT_MANUFACTURING_DATE or UPDATED_MANUFACTURING_DATE
        defaultOrderRecievedShouldBeFound("manufacturingDate.in=" + DEFAULT_MANUFACTURING_DATE + "," + UPDATED_MANUFACTURING_DATE);

        // Get all the orderRecievedList where manufacturingDate equals to UPDATED_MANUFACTURING_DATE
        defaultOrderRecievedShouldNotBeFound("manufacturingDate.in=" + UPDATED_MANUFACTURING_DATE);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByManufacturingDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where manufacturingDate is not null
        defaultOrderRecievedShouldBeFound("manufacturingDate.specified=true");

        // Get all the orderRecievedList where manufacturingDate is null
        defaultOrderRecievedShouldNotBeFound("manufacturingDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByExpiryDateIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where expiryDate equals to DEFAULT_EXPIRY_DATE
        defaultOrderRecievedShouldBeFound("expiryDate.equals=" + DEFAULT_EXPIRY_DATE);

        // Get all the orderRecievedList where expiryDate equals to UPDATED_EXPIRY_DATE
        defaultOrderRecievedShouldNotBeFound("expiryDate.equals=" + UPDATED_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByExpiryDateIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where expiryDate in DEFAULT_EXPIRY_DATE or UPDATED_EXPIRY_DATE
        defaultOrderRecievedShouldBeFound("expiryDate.in=" + DEFAULT_EXPIRY_DATE + "," + UPDATED_EXPIRY_DATE);

        // Get all the orderRecievedList where expiryDate equals to UPDATED_EXPIRY_DATE
        defaultOrderRecievedShouldNotBeFound("expiryDate.in=" + UPDATED_EXPIRY_DATE);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByExpiryDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where expiryDate is not null
        defaultOrderRecievedShouldBeFound("expiryDate.specified=true");

        // Get all the orderRecievedList where expiryDate is null
        defaultOrderRecievedShouldNotBeFound("expiryDate.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved equals to DEFAULT_QTY_APPROVED
        defaultOrderRecievedShouldBeFound("qtyApproved.equals=" + DEFAULT_QTY_APPROVED);

        // Get all the orderRecievedList where qtyApproved equals to UPDATED_QTY_APPROVED
        defaultOrderRecievedShouldNotBeFound("qtyApproved.equals=" + UPDATED_QTY_APPROVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved in DEFAULT_QTY_APPROVED or UPDATED_QTY_APPROVED
        defaultOrderRecievedShouldBeFound("qtyApproved.in=" + DEFAULT_QTY_APPROVED + "," + UPDATED_QTY_APPROVED);

        // Get all the orderRecievedList where qtyApproved equals to UPDATED_QTY_APPROVED
        defaultOrderRecievedShouldNotBeFound("qtyApproved.in=" + UPDATED_QTY_APPROVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved is not null
        defaultOrderRecievedShouldBeFound("qtyApproved.specified=true");

        // Get all the orderRecievedList where qtyApproved is null
        defaultOrderRecievedShouldNotBeFound("qtyApproved.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved is greater than or equal to DEFAULT_QTY_APPROVED
        defaultOrderRecievedShouldBeFound("qtyApproved.greaterThanOrEqual=" + DEFAULT_QTY_APPROVED);

        // Get all the orderRecievedList where qtyApproved is greater than or equal to UPDATED_QTY_APPROVED
        defaultOrderRecievedShouldNotBeFound("qtyApproved.greaterThanOrEqual=" + UPDATED_QTY_APPROVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved is less than or equal to DEFAULT_QTY_APPROVED
        defaultOrderRecievedShouldBeFound("qtyApproved.lessThanOrEqual=" + DEFAULT_QTY_APPROVED);

        // Get all the orderRecievedList where qtyApproved is less than or equal to SMALLER_QTY_APPROVED
        defaultOrderRecievedShouldNotBeFound("qtyApproved.lessThanOrEqual=" + SMALLER_QTY_APPROVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved is less than DEFAULT_QTY_APPROVED
        defaultOrderRecievedShouldNotBeFound("qtyApproved.lessThan=" + DEFAULT_QTY_APPROVED);

        // Get all the orderRecievedList where qtyApproved is less than UPDATED_QTY_APPROVED
        defaultOrderRecievedShouldBeFound("qtyApproved.lessThan=" + UPDATED_QTY_APPROVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyApprovedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyApproved is greater than DEFAULT_QTY_APPROVED
        defaultOrderRecievedShouldNotBeFound("qtyApproved.greaterThan=" + DEFAULT_QTY_APPROVED);

        // Get all the orderRecievedList where qtyApproved is greater than SMALLER_QTY_APPROVED
        defaultOrderRecievedShouldBeFound("qtyApproved.greaterThan=" + SMALLER_QTY_APPROVED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected equals to DEFAULT_QTY_REJECTED
        defaultOrderRecievedShouldBeFound("qtyRejected.equals=" + DEFAULT_QTY_REJECTED);

        // Get all the orderRecievedList where qtyRejected equals to UPDATED_QTY_REJECTED
        defaultOrderRecievedShouldNotBeFound("qtyRejected.equals=" + UPDATED_QTY_REJECTED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsInShouldWork() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected in DEFAULT_QTY_REJECTED or UPDATED_QTY_REJECTED
        defaultOrderRecievedShouldBeFound("qtyRejected.in=" + DEFAULT_QTY_REJECTED + "," + UPDATED_QTY_REJECTED);

        // Get all the orderRecievedList where qtyRejected equals to UPDATED_QTY_REJECTED
        defaultOrderRecievedShouldNotBeFound("qtyRejected.in=" + UPDATED_QTY_REJECTED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected is not null
        defaultOrderRecievedShouldBeFound("qtyRejected.specified=true");

        // Get all the orderRecievedList where qtyRejected is null
        defaultOrderRecievedShouldNotBeFound("qtyRejected.specified=false");
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected is greater than or equal to DEFAULT_QTY_REJECTED
        defaultOrderRecievedShouldBeFound("qtyRejected.greaterThanOrEqual=" + DEFAULT_QTY_REJECTED);

        // Get all the orderRecievedList where qtyRejected is greater than or equal to UPDATED_QTY_REJECTED
        defaultOrderRecievedShouldNotBeFound("qtyRejected.greaterThanOrEqual=" + UPDATED_QTY_REJECTED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected is less than or equal to DEFAULT_QTY_REJECTED
        defaultOrderRecievedShouldBeFound("qtyRejected.lessThanOrEqual=" + DEFAULT_QTY_REJECTED);

        // Get all the orderRecievedList where qtyRejected is less than or equal to SMALLER_QTY_REJECTED
        defaultOrderRecievedShouldNotBeFound("qtyRejected.lessThanOrEqual=" + SMALLER_QTY_REJECTED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected is less than DEFAULT_QTY_REJECTED
        defaultOrderRecievedShouldNotBeFound("qtyRejected.lessThan=" + DEFAULT_QTY_REJECTED);

        // Get all the orderRecievedList where qtyRejected is less than UPDATED_QTY_REJECTED
        defaultOrderRecievedShouldBeFound("qtyRejected.lessThan=" + UPDATED_QTY_REJECTED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByQtyRejectedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        // Get all the orderRecievedList where qtyRejected is greater than DEFAULT_QTY_REJECTED
        defaultOrderRecievedShouldNotBeFound("qtyRejected.greaterThan=" + DEFAULT_QTY_REJECTED);

        // Get all the orderRecievedList where qtyRejected is greater than SMALLER_QTY_REJECTED
        defaultOrderRecievedShouldBeFound("qtyRejected.greaterThan=" + SMALLER_QTY_REJECTED);
    }

    @Test
    @Transactional
    void getAllOrderRecievedsByPurchaseQuotationIsEqualToSomething() throws Exception {
        PurchaseQuotation purchaseQuotation;
        if (TestUtil.findAll(em, PurchaseQuotation.class).isEmpty()) {
            orderRecievedRepository.saveAndFlush(orderRecieved);
            purchaseQuotation = PurchaseQuotationResourceIT.createEntity(em);
        } else {
            purchaseQuotation = TestUtil.findAll(em, PurchaseQuotation.class).get(0);
        }
        em.persist(purchaseQuotation);
        em.flush();
        orderRecieved.setPurchaseQuotation(purchaseQuotation);
        orderRecievedRepository.saveAndFlush(orderRecieved);
        Long purchaseQuotationId = purchaseQuotation.getId();

        // Get all the orderRecievedList where purchaseQuotation equals to purchaseQuotationId
        defaultOrderRecievedShouldBeFound("purchaseQuotationId.equals=" + purchaseQuotationId);

        // Get all the orderRecievedList where purchaseQuotation equals to (purchaseQuotationId + 1)
        defaultOrderRecievedShouldNotBeFound("purchaseQuotationId.equals=" + (purchaseQuotationId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderRecievedShouldBeFound(String filter) throws Exception {
        restOrderRecievedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderRecieved.getId().intValue())))
            .andExpect(jsonPath("$.[*].orDate").value(hasItem(DEFAULT_OR_DATE.toString())))
            .andExpect(jsonPath("$.[*].qtyOrdered").value(hasItem(DEFAULT_QTY_ORDERED.intValue())))
            .andExpect(jsonPath("$.[*].qtyRecieved").value(hasItem(DEFAULT_QTY_RECIEVED.intValue())))
            .andExpect(jsonPath("$.[*].manufacturingDate").value(hasItem(DEFAULT_MANUFACTURING_DATE.toString())))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].qtyApproved").value(hasItem(DEFAULT_QTY_APPROVED.intValue())))
            .andExpect(jsonPath("$.[*].qtyRejected").value(hasItem(DEFAULT_QTY_REJECTED.intValue())));

        // Check, that the count call also returns 1
        restOrderRecievedMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderRecievedShouldNotBeFound(String filter) throws Exception {
        restOrderRecievedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderRecievedMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOrderRecieved() throws Exception {
        // Get the orderRecieved
        restOrderRecievedMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderRecieved() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();

        // Update the orderRecieved
        OrderRecieved updatedOrderRecieved = orderRecievedRepository.findById(orderRecieved.getId()).get();
        // Disconnect from session so that the updates on updatedOrderRecieved are not directly saved in db
        em.detach(updatedOrderRecieved);
        updatedOrderRecieved
            .orDate(UPDATED_OR_DATE)
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .qtyRecieved(UPDATED_QTY_RECIEVED)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qtyApproved(UPDATED_QTY_APPROVED)
            .qtyRejected(UPDATED_QTY_REJECTED);
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(updatedOrderRecieved);

        restOrderRecievedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderRecievedDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
        OrderRecieved testOrderRecieved = orderRecievedList.get(orderRecievedList.size() - 1);
        assertThat(testOrderRecieved.getOrDate()).isEqualTo(UPDATED_OR_DATE);
        assertThat(testOrderRecieved.getQtyOrdered()).isEqualTo(UPDATED_QTY_ORDERED);
        assertThat(testOrderRecieved.getQtyRecieved()).isEqualTo(UPDATED_QTY_RECIEVED);
        assertThat(testOrderRecieved.getManufacturingDate()).isEqualTo(UPDATED_MANUFACTURING_DATE);
        assertThat(testOrderRecieved.getExpiryDate()).isEqualTo(UPDATED_EXPIRY_DATE);
        assertThat(testOrderRecieved.getQtyApproved()).isEqualTo(UPDATED_QTY_APPROVED);
        assertThat(testOrderRecieved.getQtyRejected()).isEqualTo(UPDATED_QTY_REJECTED);
    }

    @Test
    @Transactional
    void putNonExistingOrderRecieved() throws Exception {
        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();
        orderRecieved.setId(count.incrementAndGet());

        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderRecievedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderRecievedDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderRecieved() throws Exception {
        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();
        orderRecieved.setId(count.incrementAndGet());

        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderRecievedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderRecieved() throws Exception {
        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();
        orderRecieved.setId(count.incrementAndGet());

        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderRecievedMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOrderRecievedWithPatch() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();

        // Update the orderRecieved using partial update
        OrderRecieved partialUpdatedOrderRecieved = new OrderRecieved();
        partialUpdatedOrderRecieved.setId(orderRecieved.getId());

        partialUpdatedOrderRecieved
            .qtyRecieved(UPDATED_QTY_RECIEVED)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .qtyApproved(UPDATED_QTY_APPROVED);

        restOrderRecievedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderRecieved.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderRecieved))
            )
            .andExpect(status().isOk());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
        OrderRecieved testOrderRecieved = orderRecievedList.get(orderRecievedList.size() - 1);
        assertThat(testOrderRecieved.getOrDate()).isEqualTo(DEFAULT_OR_DATE);
        assertThat(testOrderRecieved.getQtyOrdered()).isEqualTo(DEFAULT_QTY_ORDERED);
        assertThat(testOrderRecieved.getQtyRecieved()).isEqualTo(UPDATED_QTY_RECIEVED);
        assertThat(testOrderRecieved.getManufacturingDate()).isEqualTo(UPDATED_MANUFACTURING_DATE);
        assertThat(testOrderRecieved.getExpiryDate()).isEqualTo(DEFAULT_EXPIRY_DATE);
        assertThat(testOrderRecieved.getQtyApproved()).isEqualTo(UPDATED_QTY_APPROVED);
        assertThat(testOrderRecieved.getQtyRejected()).isEqualTo(DEFAULT_QTY_REJECTED);
    }

    @Test
    @Transactional
    void fullUpdateOrderRecievedWithPatch() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();

        // Update the orderRecieved using partial update
        OrderRecieved partialUpdatedOrderRecieved = new OrderRecieved();
        partialUpdatedOrderRecieved.setId(orderRecieved.getId());

        partialUpdatedOrderRecieved
            .orDate(UPDATED_OR_DATE)
            .qtyOrdered(UPDATED_QTY_ORDERED)
            .qtyRecieved(UPDATED_QTY_RECIEVED)
            .manufacturingDate(UPDATED_MANUFACTURING_DATE)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .qtyApproved(UPDATED_QTY_APPROVED)
            .qtyRejected(UPDATED_QTY_REJECTED);

        restOrderRecievedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderRecieved.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderRecieved))
            )
            .andExpect(status().isOk());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
        OrderRecieved testOrderRecieved = orderRecievedList.get(orderRecievedList.size() - 1);
        assertThat(testOrderRecieved.getOrDate()).isEqualTo(UPDATED_OR_DATE);
        assertThat(testOrderRecieved.getQtyOrdered()).isEqualTo(UPDATED_QTY_ORDERED);
        assertThat(testOrderRecieved.getQtyRecieved()).isEqualTo(UPDATED_QTY_RECIEVED);
        assertThat(testOrderRecieved.getManufacturingDate()).isEqualTo(UPDATED_MANUFACTURING_DATE);
        assertThat(testOrderRecieved.getExpiryDate()).isEqualTo(UPDATED_EXPIRY_DATE);
        assertThat(testOrderRecieved.getQtyApproved()).isEqualTo(UPDATED_QTY_APPROVED);
        assertThat(testOrderRecieved.getQtyRejected()).isEqualTo(UPDATED_QTY_REJECTED);
    }

    @Test
    @Transactional
    void patchNonExistingOrderRecieved() throws Exception {
        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();
        orderRecieved.setId(count.incrementAndGet());

        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderRecievedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderRecievedDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderRecieved() throws Exception {
        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();
        orderRecieved.setId(count.incrementAndGet());

        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderRecievedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderRecieved() throws Exception {
        int databaseSizeBeforeUpdate = orderRecievedRepository.findAll().size();
        orderRecieved.setId(count.incrementAndGet());

        // Create the OrderRecieved
        OrderRecievedDTO orderRecievedDTO = orderRecievedMapper.toDto(orderRecieved);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderRecievedMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderRecievedDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderRecieved in the database
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOrderRecieved() throws Exception {
        // Initialize the database
        orderRecievedRepository.saveAndFlush(orderRecieved);

        int databaseSizeBeforeDelete = orderRecievedRepository.findAll().size();

        // Delete the orderRecieved
        restOrderRecievedMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderRecieved.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderRecieved> orderRecievedList = orderRecievedRepository.findAll();
        assertThat(orderRecievedList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
