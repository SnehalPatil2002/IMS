package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PurchaseRequestRepository;
import com.mycompany.myapp.service.PurchaseRequestQueryService;
import com.mycompany.myapp.service.PurchaseRequestService;
import com.mycompany.myapp.service.criteria.PurchaseRequestCriteria;
import com.mycompany.myapp.service.dto.PurchaseRequestDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PurchaseRequest}.
 */
@RestController
@RequestMapping("/api")
public class PurchaseRequestResource {

    private final Logger log = LoggerFactory.getLogger(PurchaseRequestResource.class);

    private static final String ENTITY_NAME = "purchaseRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PurchaseRequestService purchaseRequestService;

    private final PurchaseRequestRepository purchaseRequestRepository;

    private final PurchaseRequestQueryService purchaseRequestQueryService;

    public PurchaseRequestResource(
        PurchaseRequestService purchaseRequestService,
        PurchaseRequestRepository purchaseRequestRepository,
        PurchaseRequestQueryService purchaseRequestQueryService
    ) {
        this.purchaseRequestService = purchaseRequestService;
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.purchaseRequestQueryService = purchaseRequestQueryService;
    }

    /**
     * {@code POST  /purchase-requests} : Create a new purchaseRequest.
     *
     * @param purchaseRequestDTO the purchaseRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new purchaseRequestDTO, or with status {@code 400 (Bad Request)} if the purchaseRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/purchase-requests")
    public ResponseEntity<PurchaseRequestDTO> createPurchaseRequest(@RequestBody PurchaseRequestDTO purchaseRequestDTO)
        throws URISyntaxException {
        log.debug("REST request to save PurchaseRequest : {}", purchaseRequestDTO);
        if (purchaseRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new purchaseRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PurchaseRequestDTO result = purchaseRequestService.save(purchaseRequestDTO);
        return ResponseEntity
            .created(new URI("/api/purchase-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /purchase-requests/:id} : Updates an existing purchaseRequest.
     *
     * @param id the id of the purchaseRequestDTO to save.
     * @param purchaseRequestDTO the purchaseRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseRequestDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the purchaseRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/purchase-requests/{id}")
    public ResponseEntity<PurchaseRequestDTO> updatePurchaseRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseRequestDTO purchaseRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PurchaseRequest : {}, {}", id, purchaseRequestDTO);
        if (purchaseRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PurchaseRequestDTO result = purchaseRequestService.update(purchaseRequestDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /purchase-requests/:id} : Partial updates given fields of an existing purchaseRequest, field will ignore if it is null
     *
     * @param id the id of the purchaseRequestDTO to save.
     * @param purchaseRequestDTO the purchaseRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated purchaseRequestDTO,
     * or with status {@code 400 (Bad Request)} if the purchaseRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the purchaseRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the purchaseRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/purchase-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PurchaseRequestDTO> partialUpdatePurchaseRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PurchaseRequestDTO purchaseRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PurchaseRequest partially : {}, {}", id, purchaseRequestDTO);
        if (purchaseRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, purchaseRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!purchaseRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PurchaseRequestDTO> result = purchaseRequestService.partialUpdate(purchaseRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, purchaseRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /purchase-requests} : get all the purchaseRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of purchaseRequests in body.
     */
    @GetMapping("/purchase-requests")
    public ResponseEntity<List<PurchaseRequestDTO>> getAllPurchaseRequests(
        PurchaseRequestCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get PurchaseRequests by criteria: {}", criteria);
        Page<PurchaseRequestDTO> page = purchaseRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /purchase-requests/count} : count all the purchaseRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/purchase-requests/count")
    public ResponseEntity<Long> countPurchaseRequests(PurchaseRequestCriteria criteria) {
        log.debug("REST request to count PurchaseRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(purchaseRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /purchase-requests/:id} : get the "id" purchaseRequest.
     *
     * @param id the id of the purchaseRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the purchaseRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/purchase-requests/{id}")
    public ResponseEntity<PurchaseRequestDTO> getPurchaseRequest(@PathVariable Long id) {
        log.debug("REST request to get PurchaseRequest : {}", id);
        Optional<PurchaseRequestDTO> purchaseRequestDTO = purchaseRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(purchaseRequestDTO);
    }

    /**
     * {@code DELETE  /purchase-requests/:id} : delete the "id" purchaseRequest.
     *
     * @param id the id of the purchaseRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/purchase-requests/{id}")
    public ResponseEntity<Void> deletePurchaseRequest(@PathVariable Long id) {
        log.debug("REST request to delete PurchaseRequest : {}", id);
        purchaseRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
