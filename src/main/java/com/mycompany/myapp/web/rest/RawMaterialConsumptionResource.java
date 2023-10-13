package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.RawMaterialConsumptionRepository;
import com.mycompany.myapp.service.RawMaterialConsumptionQueryService;
import com.mycompany.myapp.service.RawMaterialConsumptionService;
import com.mycompany.myapp.service.criteria.RawMaterialConsumptionCriteria;
import com.mycompany.myapp.service.dto.RawMaterialConsumptionDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.RawMaterialConsumption}.
 */
@RestController
@RequestMapping("/api")
public class RawMaterialConsumptionResource {

    private final Logger log = LoggerFactory.getLogger(RawMaterialConsumptionResource.class);

    private static final String ENTITY_NAME = "rawMaterialConsumption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RawMaterialConsumptionService rawMaterialConsumptionService;

    private final RawMaterialConsumptionRepository rawMaterialConsumptionRepository;

    private final RawMaterialConsumptionQueryService rawMaterialConsumptionQueryService;

    public RawMaterialConsumptionResource(
        RawMaterialConsumptionService rawMaterialConsumptionService,
        RawMaterialConsumptionRepository rawMaterialConsumptionRepository,
        RawMaterialConsumptionQueryService rawMaterialConsumptionQueryService
    ) {
        this.rawMaterialConsumptionService = rawMaterialConsumptionService;
        this.rawMaterialConsumptionRepository = rawMaterialConsumptionRepository;
        this.rawMaterialConsumptionQueryService = rawMaterialConsumptionQueryService;
    }

    /**
     * {@code POST  /raw-material-consumptions} : Create a new rawMaterialConsumption.
     *
     * @param rawMaterialConsumptionDTO the rawMaterialConsumptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rawMaterialConsumptionDTO, or with status {@code 400 (Bad Request)} if the rawMaterialConsumption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/raw-material-consumptions")
    public ResponseEntity<RawMaterialConsumptionDTO> createRawMaterialConsumption(
        @RequestBody RawMaterialConsumptionDTO rawMaterialConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to save RawMaterialConsumption : {}", rawMaterialConsumptionDTO);
        if (rawMaterialConsumptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new rawMaterialConsumption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RawMaterialConsumptionDTO result = rawMaterialConsumptionService.save(rawMaterialConsumptionDTO);
        return ResponseEntity
            .created(new URI("/api/raw-material-consumptions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /raw-material-consumptions/:id} : Updates an existing rawMaterialConsumption.
     *
     * @param id the id of the rawMaterialConsumptionDTO to save.
     * @param rawMaterialConsumptionDTO the rawMaterialConsumptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMaterialConsumptionDTO,
     * or with status {@code 400 (Bad Request)} if the rawMaterialConsumptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rawMaterialConsumptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/raw-material-consumptions/{id}")
    public ResponseEntity<RawMaterialConsumptionDTO> updateRawMaterialConsumption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RawMaterialConsumptionDTO rawMaterialConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RawMaterialConsumption : {}, {}", id, rawMaterialConsumptionDTO);
        if (rawMaterialConsumptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMaterialConsumptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rawMaterialConsumptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RawMaterialConsumptionDTO result = rawMaterialConsumptionService.update(rawMaterialConsumptionDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rawMaterialConsumptionDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /raw-material-consumptions/:id} : Partial updates given fields of an existing rawMaterialConsumption, field will ignore if it is null
     *
     * @param id the id of the rawMaterialConsumptionDTO to save.
     * @param rawMaterialConsumptionDTO the rawMaterialConsumptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMaterialConsumptionDTO,
     * or with status {@code 400 (Bad Request)} if the rawMaterialConsumptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rawMaterialConsumptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rawMaterialConsumptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/raw-material-consumptions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RawMaterialConsumptionDTO> partialUpdateRawMaterialConsumption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RawMaterialConsumptionDTO rawMaterialConsumptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RawMaterialConsumption partially : {}, {}", id, rawMaterialConsumptionDTO);
        if (rawMaterialConsumptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMaterialConsumptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rawMaterialConsumptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RawMaterialConsumptionDTO> result = rawMaterialConsumptionService.partialUpdate(rawMaterialConsumptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rawMaterialConsumptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /raw-material-consumptions} : get all the rawMaterialConsumptions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rawMaterialConsumptions in body.
     */
    @GetMapping("/raw-material-consumptions")
    public ResponseEntity<List<RawMaterialConsumptionDTO>> getAllRawMaterialConsumptions(
        RawMaterialConsumptionCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RawMaterialConsumptions by criteria: {}", criteria);
        Page<RawMaterialConsumptionDTO> page = rawMaterialConsumptionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /raw-material-consumptions/count} : count all the rawMaterialConsumptions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/raw-material-consumptions/count")
    public ResponseEntity<Long> countRawMaterialConsumptions(RawMaterialConsumptionCriteria criteria) {
        log.debug("REST request to count RawMaterialConsumptions by criteria: {}", criteria);
        return ResponseEntity.ok().body(rawMaterialConsumptionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /raw-material-consumptions/:id} : get the "id" rawMaterialConsumption.
     *
     * @param id the id of the rawMaterialConsumptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rawMaterialConsumptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/raw-material-consumptions/{id}")
    public ResponseEntity<RawMaterialConsumptionDTO> getRawMaterialConsumption(@PathVariable Long id) {
        log.debug("REST request to get RawMaterialConsumption : {}", id);
        Optional<RawMaterialConsumptionDTO> rawMaterialConsumptionDTO = rawMaterialConsumptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rawMaterialConsumptionDTO);
    }

    /**
     * {@code DELETE  /raw-material-consumptions/:id} : delete the "id" rawMaterialConsumption.
     *
     * @param id the id of the rawMaterialConsumptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/raw-material-consumptions/{id}")
    public ResponseEntity<Void> deleteRawMaterialConsumption(@PathVariable Long id) {
        log.debug("REST request to delete RawMaterialConsumption : {}", id);
        rawMaterialConsumptionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
