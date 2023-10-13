package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.RawMaterialRepository;
import com.mycompany.myapp.service.RawMaterialQueryService;
import com.mycompany.myapp.service.RawMaterialService;
import com.mycompany.myapp.service.criteria.RawMaterialCriteria;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.RawMaterial}.
 */
@RestController
@RequestMapping("/api")
public class RawMaterialResource {

    private final Logger log = LoggerFactory.getLogger(RawMaterialResource.class);

    private static final String ENTITY_NAME = "rawMaterial";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RawMaterialService rawMaterialService;

    private final RawMaterialRepository rawMaterialRepository;

    private final RawMaterialQueryService rawMaterialQueryService;

    public RawMaterialResource(
        RawMaterialService rawMaterialService,
        RawMaterialRepository rawMaterialRepository,
        RawMaterialQueryService rawMaterialQueryService
    ) {
        this.rawMaterialService = rawMaterialService;
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialQueryService = rawMaterialQueryService;
    }

    /**
     * {@code POST  /raw-materials} : Create a new rawMaterial.
     *
     * @param rawMaterialDTO the rawMaterialDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rawMaterialDTO, or with status {@code 400 (Bad Request)} if the rawMaterial has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/raw-materials")
    public ResponseEntity<RawMaterialDTO> createRawMaterial(@RequestBody RawMaterialDTO rawMaterialDTO) throws URISyntaxException {
        log.debug("REST request to save RawMaterial : {}", rawMaterialDTO);
        if (rawMaterialDTO.getId() != null) {
            throw new BadRequestAlertException("A new rawMaterial cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RawMaterialDTO result = rawMaterialService.save(rawMaterialDTO);
        return ResponseEntity
            .created(new URI("/api/raw-materials/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /raw-materials/:id} : Updates an existing rawMaterial.
     *
     * @param id the id of the rawMaterialDTO to save.
     * @param rawMaterialDTO the rawMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the rawMaterialDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rawMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/raw-materials/{id}")
    public ResponseEntity<RawMaterialDTO> updateRawMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RawMaterialDTO rawMaterialDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RawMaterial : {}, {}", id, rawMaterialDTO);
        if (rawMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rawMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RawMaterialDTO result = rawMaterialService.update(rawMaterialDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rawMaterialDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /raw-materials/:id} : Partial updates given fields of an existing rawMaterial, field will ignore if it is null
     *
     * @param id the id of the rawMaterialDTO to save.
     * @param rawMaterialDTO the rawMaterialDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rawMaterialDTO,
     * or with status {@code 400 (Bad Request)} if the rawMaterialDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rawMaterialDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rawMaterialDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/raw-materials/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RawMaterialDTO> partialUpdateRawMaterial(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RawMaterialDTO rawMaterialDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RawMaterial partially : {}, {}", id, rawMaterialDTO);
        if (rawMaterialDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rawMaterialDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rawMaterialRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RawMaterialDTO> result = rawMaterialService.partialUpdate(rawMaterialDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rawMaterialDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /raw-materials} : get all the rawMaterials.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rawMaterials in body.
     */
    @GetMapping("/raw-materials")
    public ResponseEntity<List<RawMaterialDTO>> getAllRawMaterials(
        RawMaterialCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RawMaterials by criteria: {}", criteria);
        Page<RawMaterialDTO> page = rawMaterialQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /raw-materials/count} : count all the rawMaterials.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/raw-materials/count")
    public ResponseEntity<Long> countRawMaterials(RawMaterialCriteria criteria) {
        log.debug("REST request to count RawMaterials by criteria: {}", criteria);
        return ResponseEntity.ok().body(rawMaterialQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /raw-materials/:id} : get the "id" rawMaterial.
     *
     * @param id the id of the rawMaterialDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rawMaterialDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/raw-materials/{id}")
    public ResponseEntity<RawMaterialDTO> getRawMaterial(@PathVariable Long id) {
        log.debug("REST request to get RawMaterial : {}", id);
        Optional<RawMaterialDTO> rawMaterialDTO = rawMaterialService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rawMaterialDTO);
    }

    /**
     * {@code DELETE  /raw-materials/:id} : delete the "id" rawMaterial.
     *
     * @param id the id of the rawMaterialDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/raw-materials/{id}")
    public ResponseEntity<Void> deleteRawMaterial(@PathVariable Long id) {
        log.debug("REST request to delete RawMaterial : {}", id);
        rawMaterialService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
