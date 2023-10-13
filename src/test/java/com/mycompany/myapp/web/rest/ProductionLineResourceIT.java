package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.repository.ProductionLineRepository;
import com.mycompany.myapp.service.criteria.ProductionLineCriteria;
import com.mycompany.myapp.service.dto.ProductionLineDTO;
import com.mycompany.myapp.service.mapper.ProductionLineMapper;
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
 * Integration tests for the {@link ProductionLineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductionLineResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/production-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductionLineRepository productionLineRepository;

    @Autowired
    private ProductionLineMapper productionLineMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProductionLineMockMvc;

    private ProductionLine productionLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductionLine createEntity(EntityManager em) {
        ProductionLine productionLine = new ProductionLine().description(DEFAULT_DESCRIPTION).isActive(DEFAULT_IS_ACTIVE);
        return productionLine;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductionLine createUpdatedEntity(EntityManager em) {
        ProductionLine productionLine = new ProductionLine().description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE);
        return productionLine;
    }

    @BeforeEach
    public void initTest() {
        productionLine = createEntity(em);
    }

    @Test
    @Transactional
    void createProductionLine() throws Exception {
        int databaseSizeBeforeCreate = productionLineRepository.findAll().size();
        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);
        restProductionLineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeCreate + 1);
        ProductionLine testProductionLine = productionLineList.get(productionLineList.size() - 1);
        assertThat(testProductionLine.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductionLine.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void createProductionLineWithExistingId() throws Exception {
        // Create the ProductionLine with an existing ID
        productionLine.setId(1L);
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        int databaseSizeBeforeCreate = productionLineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductionLineMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProductionLines() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList
        restProductionLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productionLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getProductionLine() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get the productionLine
        restProductionLineMockMvc
            .perform(get(ENTITY_API_URL_ID, productionLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productionLine.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getProductionLinesByIdFiltering() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        Long id = productionLine.getId();

        defaultProductionLineShouldBeFound("id.equals=" + id);
        defaultProductionLineShouldNotBeFound("id.notEquals=" + id);

        defaultProductionLineShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultProductionLineShouldNotBeFound("id.greaterThan=" + id);

        defaultProductionLineShouldBeFound("id.lessThanOrEqual=" + id);
        defaultProductionLineShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProductionLinesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where description equals to DEFAULT_DESCRIPTION
        defaultProductionLineShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the productionLineList where description equals to UPDATED_DESCRIPTION
        defaultProductionLineShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductionLinesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultProductionLineShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the productionLineList where description equals to UPDATED_DESCRIPTION
        defaultProductionLineShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductionLinesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where description is not null
        defaultProductionLineShouldBeFound("description.specified=true");

        // Get all the productionLineList where description is null
        defaultProductionLineShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllProductionLinesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where description contains DEFAULT_DESCRIPTION
        defaultProductionLineShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the productionLineList where description contains UPDATED_DESCRIPTION
        defaultProductionLineShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductionLinesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where description does not contain DEFAULT_DESCRIPTION
        defaultProductionLineShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the productionLineList where description does not contain UPDATED_DESCRIPTION
        defaultProductionLineShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllProductionLinesByIsActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where isActive equals to DEFAULT_IS_ACTIVE
        defaultProductionLineShouldBeFound("isActive.equals=" + DEFAULT_IS_ACTIVE);

        // Get all the productionLineList where isActive equals to UPDATED_IS_ACTIVE
        defaultProductionLineShouldNotBeFound("isActive.equals=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllProductionLinesByIsActiveIsInShouldWork() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where isActive in DEFAULT_IS_ACTIVE or UPDATED_IS_ACTIVE
        defaultProductionLineShouldBeFound("isActive.in=" + DEFAULT_IS_ACTIVE + "," + UPDATED_IS_ACTIVE);

        // Get all the productionLineList where isActive equals to UPDATED_IS_ACTIVE
        defaultProductionLineShouldNotBeFound("isActive.in=" + UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void getAllProductionLinesByIsActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        // Get all the productionLineList where isActive is not null
        defaultProductionLineShouldBeFound("isActive.specified=true");

        // Get all the productionLineList where isActive is null
        defaultProductionLineShouldNotBeFound("isActive.specified=false");
    }

    @Test
    @Transactional
    void getAllProductionLinesByProductIsEqualToSomething() throws Exception {
        Products product;
        if (TestUtil.findAll(em, Products.class).isEmpty()) {
            productionLineRepository.saveAndFlush(productionLine);
            product = ProductsResourceIT.createEntity(em);
        } else {
            product = TestUtil.findAll(em, Products.class).get(0);
        }
        em.persist(product);
        em.flush();
        productionLine.setProduct(product);
        productionLineRepository.saveAndFlush(productionLine);
        Long productId = product.getId();

        // Get all the productionLineList where product equals to productId
        defaultProductionLineShouldBeFound("productId.equals=" + productId);

        // Get all the productionLineList where product equals to (productId + 1)
        defaultProductionLineShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProductionLineShouldBeFound(String filter) throws Exception {
        restProductionLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productionLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isActive").value(hasItem(DEFAULT_IS_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restProductionLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProductionLineShouldNotBeFound(String filter) throws Exception {
        restProductionLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProductionLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProductionLine() throws Exception {
        // Get the productionLine
        restProductionLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProductionLine() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();

        // Update the productionLine
        ProductionLine updatedProductionLine = productionLineRepository.findById(productionLine.getId()).get();
        // Disconnect from session so that the updates on updatedProductionLine are not directly saved in db
        em.detach(updatedProductionLine);
        updatedProductionLine.description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE);
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(updatedProductionLine);

        restProductionLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productionLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
        ProductionLine testProductionLine = productionLineList.get(productionLineList.size() - 1);
        assertThat(testProductionLine.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductionLine.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingProductionLine() throws Exception {
        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();
        productionLine.setId(count.incrementAndGet());

        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductionLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, productionLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProductionLine() throws Exception {
        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();
        productionLine.setId(count.incrementAndGet());

        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProductionLine() throws Exception {
        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();
        productionLine.setId(count.incrementAndGet());

        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLineMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProductionLineWithPatch() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();

        // Update the productionLine using partial update
        ProductionLine partialUpdatedProductionLine = new ProductionLine();
        partialUpdatedProductionLine.setId(productionLine.getId());

        restProductionLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductionLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductionLine))
            )
            .andExpect(status().isOk());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
        ProductionLine testProductionLine = productionLineList.get(productionLineList.size() - 1);
        assertThat(testProductionLine.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testProductionLine.getIsActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateProductionLineWithPatch() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();

        // Update the productionLine using partial update
        ProductionLine partialUpdatedProductionLine = new ProductionLine();
        partialUpdatedProductionLine.setId(productionLine.getId());

        partialUpdatedProductionLine.description(UPDATED_DESCRIPTION).isActive(UPDATED_IS_ACTIVE);

        restProductionLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProductionLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProductionLine))
            )
            .andExpect(status().isOk());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
        ProductionLine testProductionLine = productionLineList.get(productionLineList.size() - 1);
        assertThat(testProductionLine.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testProductionLine.getIsActive()).isEqualTo(UPDATED_IS_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingProductionLine() throws Exception {
        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();
        productionLine.setId(count.incrementAndGet());

        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductionLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, productionLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProductionLine() throws Exception {
        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();
        productionLine.setId(count.incrementAndGet());

        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProductionLine() throws Exception {
        int databaseSizeBeforeUpdate = productionLineRepository.findAll().size();
        productionLine.setId(count.incrementAndGet());

        // Create the ProductionLine
        ProductionLineDTO productionLineDTO = productionLineMapper.toDto(productionLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductionLineMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(productionLineDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProductionLine in the database
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProductionLine() throws Exception {
        // Initialize the database
        productionLineRepository.saveAndFlush(productionLine);

        int databaseSizeBeforeDelete = productionLineRepository.findAll().size();

        // Delete the productionLine
        restProductionLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, productionLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductionLine> productionLineList = productionLineRepository.findAll();
        assertThat(productionLineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
