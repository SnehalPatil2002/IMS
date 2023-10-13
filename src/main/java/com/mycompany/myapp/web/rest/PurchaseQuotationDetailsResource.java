package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PurchaseQuotationDetailsRepository;
import com.mycompany.myapp.service.PurchaseQuotationDetailsQueryService;
import com.mycompany.myapp.service.PurchaseQuotationDetailsService;
import com.mycompany.myapp.service.criteria.PurchaseQuotationDetailsCriteria;
import com.mycompany.myapp.service.dto.PurchaseQuotationDetailsDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PurchaseQuotationDetails}.
 */
@RestController
@RequestMapping("/api")
public class PurchaseQuotationDetailsResource {

    private final Logger log = LoggerFactory.getLogger(PurchaseQuotationDetailsResource.class);

    private static final String ENTITY_NAME = "purchaseQuotationDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseQuotationDetailsService purchaseQuotationDetailsService;

    private final PurchaseQuotationDetailsRepository purchaseQuotationDetailsRepository;

    private final PurchaseQuotationDetailsQueryService purchaseQuotationDetailsQueryService;

    public PurchaseQuotationDetailsResource(
        PurchaseQuotationDetailsService purchaseQuotationDetailsService,
        PurchaseQuotationDetailsRepository purchaseQuotationDetailsRepository,
        PurchaseQuotationDetailsQueryService purchaseQuotationDetailsQueryService
    ) {
        this.purchaseQuotationDetailsService = purchaseQuotationDetailsService;
        this.purchaseQuotationDetailsRepository = purchaseQuotationDetailsRepository;
        this.purchaseQuotationDetailsQueryService = purchaseQuotationDetailsQueryService;
    }

    /**
     * {@code POST  /purchase-quotation-details} : Create a new purchaseQuotationDetails.
     *
     * @param purchaseQuotationDetailsDTO the purchaseQuotationDetailsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseQuotationDetailsDTO, or with status {@code 400 (Bad Request)} if the purchaseQuotationDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/purchase-quotation-details")
    public ResponseEntity<PurchaseQuotationDetailsDTO> createPurchaseQuotationDetails(
        @RequestBody PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to save PurchaseQuotationDetails : {}", purchaseQuotationDetailsDTO);
        if (purchaseQuotationDetailsDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseQuotationDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PurchaseQuotationDetailsDTO result = purchaseQuotationDetailsService.save(purchaseQuotationDetailsDTO);
        return ResponseEntity
            .created(new URI("/api/purchase-quotation-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /purchase-quotation-details/:id} : Updates an existing purchaseQuotationDetails.
     *
     * @param id the id of the purchaseQuotationDetailsDTO to save.
     * @param purchaseQuotationDetailsDTO the purchaseQuotationDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseQuotationDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseQuotationDetailsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseQuotationDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/purchase-quotation-details/{id}")
    public ResponseEntity<PurchaseQuotationDetailsDTO> updatePurchaseQuotationDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PurchaseQuotationDetails : {}, {}", id, purchaseQuotationDetailsDTO);
        if (purchaseQuotationDetailsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseQuotationDetailsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseQuotationDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PurchaseQuotationDetailsDTO result = purchaseQuotationDetailsService.update(purchaseQuotationDetailsDTO);
        return ResponseEntity
            .ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseQuotationDetailsDTO.getId().toString())
            )
            .body(result);
    }

    /**
     * {@code PATCH  /purchase-quotation-details/:id} : Partial updates given fields of an existing purchaseQuotationDetails, field will ignore if it is null
     *
     * @param id the id of the purchaseQuotationDetailsDTO to save.
     * @param purchaseQuotationDetailsDTO the purchaseQuotationDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseQuotationDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseQuotationDetailsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseQuotationDetailsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseQuotationDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/purchase-quotation-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseQuotationDetailsDTO> partialUpdatePurchaseQuotationDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PurchaseQuotationDetails partially : {}, {}", id, purchaseQuotationDetailsDTO);
        if (purchaseQuotationDetailsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseQuotationDetailsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseQuotationDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseQuotationDetailsDTO> result = purchaseQuotationDetailsService.partialUpdate(purchaseQuotationDetailsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseQuotationDetailsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-quotation-details} : get all the purchaseQuotationDetails.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseQuotationDetails in body.
     */
    @GetMapping("/purchase-quotation-details")
    public ResponseEntity<List<PurchaseQuotationDetailsDTO>> getAllPurchaseQuotationDetails(
        PurchaseQuotationDetailsCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PurchaseQuotationDetails by criteria: {}", criteria);
        Page<PurchaseQuotationDetailsDTO> page = purchaseQuotationDetailsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-quotation-details/count} : count all the purchaseQuotationDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/purchase-quotation-details/count")
    public ResponseEntity<Long> countPurchaseQuotationDetails(PurchaseQuotationDetailsCriteria criteria) {
        log.debug("REST request to count PurchaseQuotationDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(purchaseQuotationDetailsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /purchase-quotation-details/:id} : get the "id" purchaseQuotationDetails.
     *
     * @param id the id of the purchaseQuotationDetailsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseQuotationDetailsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/purchase-quotation-details/{id}")
    public ResponseEntity<PurchaseQuotationDetailsDTO> getPurchaseQuotationDetails(@PathVariable Long id) {
        log.debug("REST request to get PurchaseQuotationDetails : {}", id);
        Optional<PurchaseQuotationDetailsDTO> purchaseQuotationDetailsDTO = purchaseQuotationDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseQuotationDetailsDTO);
    }

    /**
     * {@code DELETE  /purchase-quotation-details/:id} : delete the "id" purchaseQuotationDetails.
     *
     * @param id the id of the purchaseQuotationDetailsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/purchase-quotation-details/{id}")
    public ResponseEntity<Void> deletePurchaseQuotationDetails(@PathVariable Long id) {
        log.debug("REST request to delete PurchaseQuotationDetails : {}", id);
        purchaseQuotationDetailsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
