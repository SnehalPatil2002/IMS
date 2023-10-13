package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TotalConsumptionRepository;
import com.mycompany.myapp.service.TotalConsumptionQueryService;
import com.mycompany.myapp.service.TotalConsumptionService;
import com.mycompany.myapp.service.criteria.TotalConsumptionCriteria;
import com.mycompany.myapp.service.dto.TotalConsumptionDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TotalConsumption}.
 */
@RestController
@RequestMapping("/api")
public class TotalConsumptionResource {

    private final Logger log = LoggerFactory.getLogger(TotalConsumptionResource.class);

    private static final String ENTITY_NAME = "totalConsumption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TotalConsumptionService totalConsumptionService;

    private final TotalConsumptionRepository totalConsumptionRepository;

    private final TotalConsumptionQueryService totalConsumptionQueryService;

    public TotalConsumptionResource(
        TotalConsumptionService totalConsumptionService,
        TotalConsumptionRepository totalConsumptionRepository,
        TotalConsumptionQueryService totalConsumptionQueryService
    ) {
        this.totalConsumptionService = totalConsumptionService;
        this.totalConsumptionRepository = totalConsumptionRepository;
        this.totalConsumptionQueryService = totalConsumptionQueryService;
    }

    /**
     * {@code POST  /total-consumptions} : Create a new totalConsumption.
     *
     * @param totalConsumptionDTO the totalConsumptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new totalConsumptionDTO, or with status {@code 400 (Bad Request)} if the totalConsumption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/total-consumptions")
    public ResponseEntity<TotalConsumptionDTO> createTotalConsumption(@RequestBody TotalConsumptionDTO totalConsumptionDTO)
        throws URISyntaxException {
        log.debug("REST request to save TotalConsumption : {}", totalConsumptionDTO);
        if (totalConsumptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new totalConsumption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TotalConsumptionDTO result = totalConsumptionService.save(totalConsumptionDTO);
        return ResponseEntity
            .created(new URI("/api/total-consumptions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /total-consumptions/:id} : Updates an existing totalConsumption.
     *
     * @param id the id of the totalConsumptionDTO to save.
     * @param totalConsumptionDTO the totalConsumptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated totalConsumptionDTO,
     * or with status {@code 400 (Bad Request)} if the totalConsumptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the totalConsumptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/total-consumptions/{id}")
    public ResponseEntity<TotalConsumptionDTO> updateTotalConsumption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TotalConsumptionDTO totalConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TotalConsumption : {}, {}", id, totalConsumptionDTO);
        if (totalConsumptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, totalConsumptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!totalConsumptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TotalConsumptionDTO result = totalConsumptionService.update(totalConsumptionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, totalConsumptionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /total-consumptions/:id} : Partial updates given fields of an existing totalConsumption, field will ignore if it is null
     *
     * @param id the id of the totalConsumptionDTO to save.
     * @param totalConsumptionDTO the totalConsumptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated totalConsumptionDTO,
     * or with status {@code 400 (Bad Request)} if the totalConsumptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the totalConsumptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the totalConsumptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/total-consumptions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TotalConsumptionDTO> partialUpdateTotalConsumption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TotalConsumptionDTO totalConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TotalConsumption partially : {}, {}", id, totalConsumptionDTO);
        if (totalConsumptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, totalConsumptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!totalConsumptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TotalConsumptionDTO> result = totalConsumptionService.partialUpdate(totalConsumptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, totalConsumptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /total-consumptions} : get all the totalConsumptions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of totalConsumptions in body.
     */
    @GetMapping("/total-consumptions")
    public ResponseEntity<List<TotalConsumptionDTO>> getAllTotalConsumptions(
        TotalConsumptionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get TotalConsumptions by criteria: {}", criteria);
        Page<TotalConsumptionDTO> page = totalConsumptionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /total-consumptions/count} : count all the totalConsumptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/total-consumptions/count")
    public ResponseEntity<Long> countTotalConsumptions(TotalConsumptionCriteria criteria) {
        log.debug("REST request to count TotalConsumptions by criteria: {}", criteria);
        return ResponseEntity.ok().body(totalConsumptionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /total-consumptions/:id} : get the "id" totalConsumption.
     *
     * @param id the id of the totalConsumptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the totalConsumptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/total-consumptions/{id}")
    public ResponseEntity<TotalConsumptionDTO> getTotalConsumption(@PathVariable Long id) {
        log.debug("REST request to get TotalConsumption : {}", id);
        Optional<TotalConsumptionDTO> totalConsumptionDTO = totalConsumptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(totalConsumptionDTO);
    }

    /**
     * {@code DELETE  /total-consumptions/:id} : delete the "id" totalConsumption.
     *
     * @param id the id of the totalConsumptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/total-consumptions/{id}")
    public ResponseEntity<Void> deleteTotalConsumption(@PathVariable Long id) {
        log.debug("REST request to delete TotalConsumption : {}", id);
        totalConsumptionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
