package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PurchaseQuotationRepository;
import com.mycompany.myapp.service.PurchaseQuotationQueryService;
import com.mycompany.myapp.service.PurchaseQuotationService;
import com.mycompany.myapp.service.criteria.PurchaseQuotationCriteria;
import com.mycompany.myapp.service.dto.PurchaseQuotationDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PurchaseQuotation}.
 */
@RestController
@RequestMapping("/api")
public class PurchaseQuotationResource {

    private final Logger log = LoggerFactory.getLogger(PurchaseQuotationResource.class);

    private static final String ENTITY_NAME = "purchaseQuotation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseQuotationService purchaseQuotationService;

    private final PurchaseQuotationRepository purchaseQuotationRepository;

    private final PurchaseQuotationQueryService purchaseQuotationQueryService;

    public PurchaseQuotationResource(
        PurchaseQuotationService purchaseQuotationService,
        PurchaseQuotationRepository purchaseQuotationRepository,
        PurchaseQuotationQueryService purchaseQuotationQueryService
    ) {
        this.purchaseQuotationService = purchaseQuotationService;
        this.purchaseQuotationRepository = purchaseQuotationRepository;
        this.purchaseQuotationQueryService = purchaseQuotationQueryService;
    }

    /**
     * {@code POST  /purchase-quotations} : Create a new purchaseQuotation.
     *
     * @param purchaseQuotationDTO the purchaseQuotationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseQuotationDTO, or with status {@code 400 (Bad Request)} if the purchaseQuotation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/purchase-quotations")
    public ResponseEntity<PurchaseQuotationDTO> createPurchaseQuotation(@RequestBody PurchaseQuotationDTO purchaseQuotationDTO)
        throws URISyntaxException {
        log.debug("REST request to save PurchaseQuotation : {}", purchaseQuotationDTO);
        if (purchaseQuotationDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseQuotation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PurchaseQuotationDTO result = purchaseQuotationService.save(purchaseQuotationDTO);
        return ResponseEntity
            .created(new URI("/api/purchase-quotations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /purchase-quotations/:id} : Updates an existing purchaseQuotation.
     *
     * @param id the id of the purchaseQuotationDTO to save.
     * @param purchaseQuotationDTO the purchaseQuotationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseQuotationDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseQuotationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseQuotationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/purchase-quotations/{id}")
    public ResponseEntity<PurchaseQuotationDTO> updatePurchaseQuotation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseQuotationDTO purchaseQuotationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PurchaseQuotation : {}, {}", id, purchaseQuotationDTO);
        if (purchaseQuotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseQuotationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseQuotationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PurchaseQuotationDTO result = purchaseQuotationService.update(purchaseQuotationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseQuotationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /purchase-quotations/:id} : Partial updates given fields of an existing purchaseQuotation, field will ignore if it is null
     *
     * @param id the id of the purchaseQuotationDTO to save.
     * @param purchaseQuotationDTO the purchaseQuotationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseQuotationDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseQuotationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseQuotationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseQuotationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/purchase-quotations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseQuotationDTO> partialUpdatePurchaseQuotation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseQuotationDTO purchaseQuotationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PurchaseQuotation partially : {}, {}", id, purchaseQuotationDTO);
        if (purchaseQuotationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseQuotationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseQuotationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseQuotationDTO> result = purchaseQuotationService.partialUpdate(purchaseQuotationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseQuotationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-quotations} : get all the purchaseQuotations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseQuotations in body.
     */
    @GetMapping("/purchase-quotations")
    public ResponseEntity<List<PurchaseQuotationDTO>> getAllPurchaseQuotations(
        PurchaseQuotationCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PurchaseQuotations by criteria: {}", criteria);
        Page<PurchaseQuotationDTO> page = purchaseQuotationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-quotations/count} : count all the purchaseQuotations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/purchase-quotations/count")
    public ResponseEntity<Long> countPurchaseQuotations(PurchaseQuotationCriteria criteria) {
        log.debug("REST request to count PurchaseQuotations by criteria: {}", criteria);
        return ResponseEntity.ok().body(purchaseQuotationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /purchase-quotations/:id} : get the "id" purchaseQuotation.
     *
     * @param id the id of the purchaseQuotationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseQuotationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/purchase-quotations/{id}")
    public ResponseEntity<PurchaseQuotationDTO> getPurchaseQuotation(@PathVariable Long id) {
        log.debug("REST request to get PurchaseQuotation : {}", id);
        Optional<PurchaseQuotationDTO> purchaseQuotationDTO = purchaseQuotationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseQuotationDTO);
    }

    /**
     * {@code DELETE  /purchase-quotations/:id} : delete the "id" purchaseQuotation.
     *
     * @param id the id of the purchaseQuotationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/purchase-quotations/{id}")
    public ResponseEntity<Void> deletePurchaseQuotation(@PathVariable Long id) {
        log.debug("REST request to delete PurchaseQuotation : {}", id);
        purchaseQuotationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
