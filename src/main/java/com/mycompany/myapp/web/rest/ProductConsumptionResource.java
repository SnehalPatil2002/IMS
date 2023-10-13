package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ProductConsumptionRepository;
import com.mycompany.myapp.service.ProductConsumptionQueryService;
import com.mycompany.myapp.service.ProductConsumptionService;
import com.mycompany.myapp.service.criteria.ProductConsumptionCriteria;
import com.mycompany.myapp.service.dto.ProductConsumptionDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.ProductConsumption}.
 */
@RestController
@RequestMapping("/api")
public class ProductConsumptionResource {

    private final Logger log = LoggerFactory.getLogger(ProductConsumptionResource.class);

    private static final String ENTITY_NAME = "productConsumption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductConsumptionService productConsumptionService;

    private final ProductConsumptionRepository productConsumptionRepository;

    private final ProductConsumptionQueryService productConsumptionQueryService;

    public ProductConsumptionResource(
        ProductConsumptionService productConsumptionService,
        ProductConsumptionRepository productConsumptionRepository,
        ProductConsumptionQueryService productConsumptionQueryService
    ) {
        this.productConsumptionService = productConsumptionService;
        this.productConsumptionRepository = productConsumptionRepository;
        this.productConsumptionQueryService = productConsumptionQueryService;
    }

    /**
     * {@code POST  /product-consumptions} : Create a new productConsumption.
     *
     * @param productConsumptionDTO the productConsumptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productConsumptionDTO, or with status {@code 400 (Bad Request)} if the productConsumption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-consumptions")
    public ResponseEntity<ProductConsumptionDTO> createProductConsumption(@RequestBody ProductConsumptionDTO productConsumptionDTO)
        throws URISyntaxException {
        log.debug("REST request to save ProductConsumption : {}", productConsumptionDTO);
        if (productConsumptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new productConsumption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductConsumptionDTO result = productConsumptionService.save(productConsumptionDTO);
        return ResponseEntity
            .created(new URI("/api/product-consumptions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-consumptions/:id} : Updates an existing productConsumption.
     *
     * @param id the id of the productConsumptionDTO to save.
     * @param productConsumptionDTO the productConsumptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productConsumptionDTO,
     * or with status {@code 400 (Bad Request)} if the productConsumptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productConsumptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-consumptions/{id}")
    public ResponseEntity<ProductConsumptionDTO> updateProductConsumption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductConsumptionDTO productConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ProductConsumption : {}, {}", id, productConsumptionDTO);
        if (productConsumptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productConsumptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productConsumptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProductConsumptionDTO result = productConsumptionService.update(productConsumptionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productConsumptionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /product-consumptions/:id} : Partial updates given fields of an existing productConsumption, field will ignore if it is null
     *
     * @param id the id of the productConsumptionDTO to save.
     * @param productConsumptionDTO the productConsumptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productConsumptionDTO,
     * or with status {@code 400 (Bad Request)} if the productConsumptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productConsumptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productConsumptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-consumptions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProductConsumptionDTO> partialUpdateProductConsumption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProductConsumptionDTO productConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductConsumption partially : {}, {}", id, productConsumptionDTO);
        if (productConsumptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productConsumptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productConsumptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProductConsumptionDTO> result = productConsumptionService.partialUpdate(productConsumptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productConsumptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /product-consumptions} : get all the productConsumptions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productConsumptions in body.
     */
    @GetMapping("/product-consumptions")
    public ResponseEntity<List<ProductConsumptionDTO>> getAllProductConsumptions(
        ProductConsumptionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get ProductConsumptions by criteria: {}", criteria);
        Page<ProductConsumptionDTO> page = productConsumptionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /product-consumptions/count} : count all the productConsumptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/product-consumptions/count")
    public ResponseEntity<Long> countProductConsumptions(ProductConsumptionCriteria criteria) {
        log.debug("REST request to count ProductConsumptions by criteria: {}", criteria);
        return ResponseEntity.ok().body(productConsumptionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /product-consumptions/:id} : get the "id" productConsumption.
     *
     * @param id the id of the productConsumptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productConsumptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-consumptions/{id}")
    public ResponseEntity<ProductConsumptionDTO> getProductConsumption(@PathVariable Long id) {
        log.debug("REST request to get ProductConsumption : {}", id);
        Optional<ProductConsumptionDTO> productConsumptionDTO = productConsumptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productConsumptionDTO);
    }

    /**
     * {@code DELETE  /product-consumptions/:id} : delete the "id" productConsumption.
     *
     * @param id the id of the productConsumptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-consumptions/{id}")
    public ResponseEntity<Void> deleteProductConsumption(@PathVariable Long id) {
        log.debug("REST request to delete ProductConsumption : {}", id);
        productConsumptionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
