package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.StockRequestRepository;
import com.mycompany.myapp.service.StockRequestQueryService;
import com.mycompany.myapp.service.StockRequestService;
import com.mycompany.myapp.service.criteria.StockRequestCriteria;
import com.mycompany.myapp.service.dto.StockRequestDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.StockRequest}.
 */
@RestController
@RequestMapping("/api")
public class StockRequestResource {

    private final Logger log = LoggerFactory.getLogger(StockRequestResource.class);

    private static final String ENTITY_NAME = "stockRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockRequestService stockRequestService;

    private final StockRequestRepository stockRequestRepository;

    private final StockRequestQueryService stockRequestQueryService;

    public StockRequestResource(
        StockRequestService stockRequestService,
        StockRequestRepository stockRequestRepository,
        StockRequestQueryService stockRequestQueryService
    ) {
        this.stockRequestService = stockRequestService;
        this.stockRequestRepository = stockRequestRepository;
        this.stockRequestQueryService = stockRequestQueryService;
    }

    /**
     * {@code POST  /stock-requests} : Create a new stockRequest.
     *
     * @param stockRequestDTO the stockRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockRequestDTO, or with status {@code 400 (Bad Request)} if the stockRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-requests")
    public ResponseEntity<StockRequestDTO> createStockRequest(@RequestBody StockRequestDTO stockRequestDTO) throws URISyntaxException {
        log.debug("REST request to save StockRequest : {}", stockRequestDTO);
        if (stockRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockRequestDTO result = stockRequestService.save(stockRequestDTO);
        return ResponseEntity
            .created(new URI("/api/stock-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-requests/:id} : Updates an existing stockRequest.
     *
     * @param id the id of the stockRequestDTO to save.
     * @param stockRequestDTO the stockRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockRequestDTO,
     * or with status {@code 400 (Bad Request)} if the stockRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-requests/{id}")
    public ResponseEntity<StockRequestDTO> updateStockRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockRequestDTO stockRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockRequest : {}, {}", id, stockRequestDTO);
        if (stockRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockRequestDTO result = stockRequestService.update(stockRequestDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stockRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-requests/:id} : Partial updates given fields of an existing stockRequest, field will ignore if it is null
     *
     * @param id the id of the stockRequestDTO to save.
     * @param stockRequestDTO the stockRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockRequestDTO,
     * or with status {@code 400 (Bad Request)} if the stockRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockRequestDTO> partialUpdateStockRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StockRequestDTO stockRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockRequest partially : {}, {}", id, stockRequestDTO);
        if (stockRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockRequestDTO> result = stockRequestService.partialUpdate(stockRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stockRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-requests} : get all the stockRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockRequests in body.
     */
    @GetMapping("/stock-requests")
    public ResponseEntity<List<StockRequestDTO>> getAllStockRequests(
        StockRequestCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get StockRequests by criteria: {}", criteria);
        Page<StockRequestDTO> page = stockRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-requests/count} : count all the stockRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stock-requests/count")
    public ResponseEntity<Long> countStockRequests(StockRequestCriteria criteria) {
        log.debug("REST request to count StockRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-requests/:id} : get the "id" stockRequest.
     *
     * @param id the id of the stockRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-requests/{id}")
    public ResponseEntity<StockRequestDTO> getStockRequest(@PathVariable Long id) {
        log.debug("REST request to get StockRequest : {}", id);
        Optional<StockRequestDTO> stockRequestDTO = stockRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockRequestDTO);
    }

    /**
     * {@code DELETE  /stock-requests/:id} : delete the "id" stockRequest.
     *
     * @param id the id of the stockRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-requests/{id}")
    public ResponseEntity<Void> deleteStockRequest(@PathVariable Long id) {
        log.debug("REST request to delete StockRequest : {}", id);
        stockRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
