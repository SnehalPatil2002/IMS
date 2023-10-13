package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.PurchaseRequest;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.enumeration.Status;
import com.mycompany.myapp.repository.PurchaseRequestRepository;
import com.mycompany.myapp.service.criteria.PurchaseRequestCriteria;
import com.mycompany.myapp.service.dto.PurchaseRequestDTO;
import com.mycompany.myapp.service.mapper.PurchaseRequestMapper;
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
 * Integration tests for the {@link PurchaseRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseRequestResourceIT {

    private static final Long DEFAULT_QTY_REQUIRED = 1L;
    private static final Long UPDATED_QTY_REQUIRED = 2L;
    private static final Long SMALLER_QTY_REQUIRED = 1L - 1L;

    private static final Instant DEFAULT_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPECTED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPECTED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_STATUS = Status.REQUESTED;
    private static final Status UPDATED_STATUS = Status.APPROVED;

    private static final String ENTITY_API_URL = "/api/purchase-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PurchaseRequestRepository purchaseRequestRepository;

    @Autowired
    private PurchaseRequestMapper purchaseRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseRequestMockMvc;

    private PurchaseRequest purchaseRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseRequest createEntity(EntityManager em) {
        PurchaseRequest purchaseRequest = new PurchaseRequest()
            .qtyRequired(DEFAULT_QTY_REQUIRED)
            .requestDate(DEFAULT_REQUEST_DATE)
            .expectedDate(DEFAULT_EXPECTED_DATE)
            .status(DEFAULT_STATUS);
        return purchaseRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PurchaseRequest createUpdatedEntity(EntityManager em) {
        PurchaseRequest purchaseRequest = new PurchaseRequest()
            .qtyRequired(UPDATED_QTY_REQUIRED)
            .requestDate(UPDATED_REQUEST_DATE)
            .expectedDate(UPDATED_EXPECTED_DATE)
            .status(UPDATED_STATUS);
        return purchaseRequest;
    }

    @BeforeEach
    public void initTest() {
        purchaseRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createPurchaseRequest() throws Exception {
        int databaseSizeBeforeCreate = purchaseRequestRepository.findAll().size();
        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);
        restPurchaseRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isCreated());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeCreate + 1);
        PurchaseRequest testPurchaseRequest = purchaseRequestList.get(purchaseRequestList.size() - 1);
        assertThat(testPurchaseRequest.getQtyRequired()).isEqualTo(DEFAULT_QTY_REQUIRED);
        assertThat(testPurchaseRequest.getRequestDate()).isEqualTo(DEFAULT_REQUEST_DATE);
        assertThat(testPurchaseRequest.getExpectedDate()).isEqualTo(DEFAULT_EXPECTED_DATE);
        assertThat(testPurchaseRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createPurchaseRequestWithExistingId() throws Exception {
        // Create the PurchaseRequest with an existing ID
        purchaseRequest.setId(1L);
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        int databaseSizeBeforeCreate = purchaseRequestRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchaseRequests() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList
        restPurchaseRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].qtyRequired").value(hasItem(DEFAULT_QTY_REQUIRED.intValue())))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedDate").value(hasItem(DEFAULT_EXPECTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    void getPurchaseRequest() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get the purchaseRequest
        restPurchaseRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, purchaseRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchaseRequest.getId().intValue()))
            .andExpect(jsonPath("$.qtyRequired").value(DEFAULT_QTY_REQUIRED.intValue()))
            .andExpect(jsonPath("$.requestDate").value(DEFAULT_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.expectedDate").value(DEFAULT_EXPECTED_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getPurchaseRequestsByIdFiltering() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        Long id = purchaseRequest.getId();

        defaultPurchaseRequestShouldBeFound("id.equals=" + id);
        defaultPurchaseRequestShouldNotBeFound("id.notEquals=" + id);

        defaultPurchaseRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPurchaseRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultPurchaseRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPurchaseRequestShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired equals to DEFAULT_QTY_REQUIRED
        defaultPurchaseRequestShouldBeFound("qtyRequired.equals=" + DEFAULT_QTY_REQUIRED);

        // Get all the purchaseRequestList where qtyRequired equals to UPDATED_QTY_REQUIRED
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.equals=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired in DEFAULT_QTY_REQUIRED or UPDATED_QTY_REQUIRED
        defaultPurchaseRequestShouldBeFound("qtyRequired.in=" + DEFAULT_QTY_REQUIRED + "," + UPDATED_QTY_REQUIRED);

        // Get all the purchaseRequestList where qtyRequired equals to UPDATED_QTY_REQUIRED
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.in=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired is not null
        defaultPurchaseRequestShouldBeFound("qtyRequired.specified=true");

        // Get all the purchaseRequestList where qtyRequired is null
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired is greater than or equal to DEFAULT_QTY_REQUIRED
        defaultPurchaseRequestShouldBeFound("qtyRequired.greaterThanOrEqual=" + DEFAULT_QTY_REQUIRED);

        // Get all the purchaseRequestList where qtyRequired is greater than or equal to UPDATED_QTY_REQUIRED
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.greaterThanOrEqual=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired is less than or equal to DEFAULT_QTY_REQUIRED
        defaultPurchaseRequestShouldBeFound("qtyRequired.lessThanOrEqual=" + DEFAULT_QTY_REQUIRED);

        // Get all the purchaseRequestList where qtyRequired is less than or equal to SMALLER_QTY_REQUIRED
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.lessThanOrEqual=" + SMALLER_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsLessThanSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired is less than DEFAULT_QTY_REQUIRED
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.lessThan=" + DEFAULT_QTY_REQUIRED);

        // Get all the purchaseRequestList where qtyRequired is less than UPDATED_QTY_REQUIRED
        defaultPurchaseRequestShouldBeFound("qtyRequired.lessThan=" + UPDATED_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByQtyRequiredIsGreaterThanSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where qtyRequired is greater than DEFAULT_QTY_REQUIRED
        defaultPurchaseRequestShouldNotBeFound("qtyRequired.greaterThan=" + DEFAULT_QTY_REQUIRED);

        // Get all the purchaseRequestList where qtyRequired is greater than SMALLER_QTY_REQUIRED
        defaultPurchaseRequestShouldBeFound("qtyRequired.greaterThan=" + SMALLER_QTY_REQUIRED);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByRequestDateIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where requestDate equals to DEFAULT_REQUEST_DATE
        defaultPurchaseRequestShouldBeFound("requestDate.equals=" + DEFAULT_REQUEST_DATE);

        // Get all the purchaseRequestList where requestDate equals to UPDATED_REQUEST_DATE
        defaultPurchaseRequestShouldNotBeFound("requestDate.equals=" + UPDATED_REQUEST_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByRequestDateIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where requestDate in DEFAULT_REQUEST_DATE or UPDATED_REQUEST_DATE
        defaultPurchaseRequestShouldBeFound("requestDate.in=" + DEFAULT_REQUEST_DATE + "," + UPDATED_REQUEST_DATE);

        // Get all the purchaseRequestList where requestDate equals to UPDATED_REQUEST_DATE
        defaultPurchaseRequestShouldNotBeFound("requestDate.in=" + UPDATED_REQUEST_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByRequestDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where requestDate is not null
        defaultPurchaseRequestShouldBeFound("requestDate.specified=true");

        // Get all the purchaseRequestList where requestDate is null
        defaultPurchaseRequestShouldNotBeFound("requestDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByExpectedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where expectedDate equals to DEFAULT_EXPECTED_DATE
        defaultPurchaseRequestShouldBeFound("expectedDate.equals=" + DEFAULT_EXPECTED_DATE);

        // Get all the purchaseRequestList where expectedDate equals to UPDATED_EXPECTED_DATE
        defaultPurchaseRequestShouldNotBeFound("expectedDate.equals=" + UPDATED_EXPECTED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByExpectedDateIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where expectedDate in DEFAULT_EXPECTED_DATE or UPDATED_EXPECTED_DATE
        defaultPurchaseRequestShouldBeFound("expectedDate.in=" + DEFAULT_EXPECTED_DATE + "," + UPDATED_EXPECTED_DATE);

        // Get all the purchaseRequestList where expectedDate equals to UPDATED_EXPECTED_DATE
        defaultPurchaseRequestShouldNotBeFound("expectedDate.in=" + UPDATED_EXPECTED_DATE);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByExpectedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where expectedDate is not null
        defaultPurchaseRequestShouldBeFound("expectedDate.specified=true");

        // Get all the purchaseRequestList where expectedDate is null
        defaultPurchaseRequestShouldNotBeFound("expectedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where status equals to DEFAULT_STATUS
        defaultPurchaseRequestShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the purchaseRequestList where status equals to UPDATED_STATUS
        defaultPurchaseRequestShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultPurchaseRequestShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the purchaseRequestList where status equals to UPDATED_STATUS
        defaultPurchaseRequestShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        // Get all the purchaseRequestList where status is not null
        defaultPurchaseRequestShouldBeFound("status.specified=true");

        // Get all the purchaseRequestList where status is null
        defaultPurchaseRequestShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllPurchaseRequestsByRawMaterialIsEqualToSomething() throws Exception {
        RawMaterial rawMaterial;
        if (TestUtil.findAll(em, RawMaterial.class).isEmpty()) {
            purchaseRequestRepository.saveAndFlush(purchaseRequest);
            rawMaterial = RawMaterialResourceIT.createEntity(em);
        } else {
            rawMaterial = TestUtil.findAll(em, RawMaterial.class).get(0);
        }
        em.persist(rawMaterial);
        em.flush();
        purchaseRequest.setRawMaterial(rawMaterial);
        purchaseRequestRepository.saveAndFlush(purchaseRequest);
        Long rawMaterialId = rawMaterial.getId();

        // Get all the purchaseRequestList where rawMaterial equals to rawMaterialId
        defaultPurchaseRequestShouldBeFound("rawMaterialId.equals=" + rawMaterialId);

        // Get all the purchaseRequestList where rawMaterial equals to (rawMaterialId + 1)
        defaultPurchaseRequestShouldNotBeFound("rawMaterialId.equals=" + (rawMaterialId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPurchaseRequestShouldBeFound(String filter) throws Exception {
        restPurchaseRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchaseRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].qtyRequired").value(hasItem(DEFAULT_QTY_REQUIRED.intValue())))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedDate").value(hasItem(DEFAULT_EXPECTED_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restPurchaseRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPurchaseRequestShouldNotBeFound(String filter) throws Exception {
        restPurchaseRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPurchaseRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPurchaseRequest() throws Exception {
        // Get the purchaseRequest
        restPurchaseRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPurchaseRequest() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();

        // Update the purchaseRequest
        PurchaseRequest updatedPurchaseRequest = purchaseRequestRepository.findById(purchaseRequest.getId()).get();
        // Disconnect from session so that the updates on updatedPurchaseRequest are not directly saved in db
        em.detach(updatedPurchaseRequest);
        updatedPurchaseRequest
            .qtyRequired(UPDATED_QTY_REQUIRED)
            .requestDate(UPDATED_REQUEST_DATE)
            .expectedDate(UPDATED_EXPECTED_DATE)
            .status(UPDATED_STATUS);
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(updatedPurchaseRequest);

        restPurchaseRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
        PurchaseRequest testPurchaseRequest = purchaseRequestList.get(purchaseRequestList.size() - 1);
        assertThat(testPurchaseRequest.getQtyRequired()).isEqualTo(UPDATED_QTY_REQUIRED);
        assertThat(testPurchaseRequest.getRequestDate()).isEqualTo(UPDATED_REQUEST_DATE);
        assertThat(testPurchaseRequest.getExpectedDate()).isEqualTo(UPDATED_EXPECTED_DATE);
        assertThat(testPurchaseRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingPurchaseRequest() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();
        purchaseRequest.setId(count.incrementAndGet());

        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchaseRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchaseRequest() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();
        purchaseRequest.setId(count.incrementAndGet());

        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchaseRequest() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();
        purchaseRequest.setId(count.incrementAndGet());

        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseRequestMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseRequestWithPatch() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();

        // Update the purchaseRequest using partial update
        PurchaseRequest partialUpdatedPurchaseRequest = new PurchaseRequest();
        partialUpdatedPurchaseRequest.setId(purchaseRequest.getId());

        partialUpdatedPurchaseRequest.requestDate(UPDATED_REQUEST_DATE).expectedDate(UPDATED_EXPECTED_DATE).status(UPDATED_STATUS);

        restPurchaseRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseRequest))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
        PurchaseRequest testPurchaseRequest = purchaseRequestList.get(purchaseRequestList.size() - 1);
        assertThat(testPurchaseRequest.getQtyRequired()).isEqualTo(DEFAULT_QTY_REQUIRED);
        assertThat(testPurchaseRequest.getRequestDate()).isEqualTo(UPDATED_REQUEST_DATE);
        assertThat(testPurchaseRequest.getExpectedDate()).isEqualTo(UPDATED_EXPECTED_DATE);
        assertThat(testPurchaseRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void fullUpdatePurchaseRequestWithPatch() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();

        // Update the purchaseRequest using partial update
        PurchaseRequest partialUpdatedPurchaseRequest = new PurchaseRequest();
        partialUpdatedPurchaseRequest.setId(purchaseRequest.getId());

        partialUpdatedPurchaseRequest
            .qtyRequired(UPDATED_QTY_REQUIRED)
            .requestDate(UPDATED_REQUEST_DATE)
            .expectedDate(UPDATED_EXPECTED_DATE)
            .status(UPDATED_STATUS);

        restPurchaseRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchaseRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchaseRequest))
            )
            .andExpect(status().isOk());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
        PurchaseRequest testPurchaseRequest = purchaseRequestList.get(purchaseRequestList.size() - 1);
        assertThat(testPurchaseRequest.getQtyRequired()).isEqualTo(UPDATED_QTY_REQUIRED);
        assertThat(testPurchaseRequest.getRequestDate()).isEqualTo(UPDATED_REQUEST_DATE);
        assertThat(testPurchaseRequest.getExpectedDate()).isEqualTo(UPDATED_EXPECTED_DATE);
        assertThat(testPurchaseRequest.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingPurchaseRequest() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();
        purchaseRequest.setId(count.incrementAndGet());

        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchaseRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchaseRequest() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();
        purchaseRequest.setId(count.incrementAndGet());

        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchaseRequest() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRequestRepository.findAll().size();
        purchaseRequest.setId(count.incrementAndGet());

        // Create the PurchaseRequest
        PurchaseRequestDTO purchaseRequestDTO = purchaseRequestMapper.toDto(purchaseRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseRequestMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchaseRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PurchaseRequest in the database
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchaseRequest() throws Exception {
        // Initialize the database
        purchaseRequestRepository.saveAndFlush(purchaseRequest);

        int databaseSizeBeforeDelete = purchaseRequestRepository.findAll().size();

        // Delete the purchaseRequest
        restPurchaseRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchaseRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PurchaseRequest> purchaseRequestList = purchaseRequestRepository.findAll();
        assertThat(purchaseRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
