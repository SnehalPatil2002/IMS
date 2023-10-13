package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.OrderRecievedRepository;
import com.mycompany.myapp.service.OrderRecievedQueryService;
import com.mycompany.myapp.service.OrderRecievedService;
import com.mycompany.myapp.service.criteria.OrderRecievedCriteria;
import com.mycompany.myapp.service.dto.OrderRecievedDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.OrderRecieved}.
 */
@RestController
@RequestMapping("/api")
public class OrderRecievedResource {

    private final Logger log = LoggerFactory.getLogger(OrderRecievedResource.class);

    private static final String ENTITY_NAME = "orderRecieved";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderRecievedService orderRecievedService;

    private final OrderRecievedRepository orderRecievedRepository;

    private final OrderRecievedQueryService orderRecievedQueryService;

    public OrderRecievedResource(
        OrderRecievedService orderRecievedService,
        OrderRecievedRepository orderRecievedRepository,
        OrderRecievedQueryService orderRecievedQueryService
    ) {
        this.orderRecievedService = orderRecievedService;
        this.orderRecievedRepository = orderRecievedRepository;
        this.orderRecievedQueryService = orderRecievedQueryService;
    }

    /**
     * {@code POST  /order-recieveds} : Create a new orderRecieved.
     *
     * @param orderRecievedDTO the orderRecievedDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderRecievedDTO, or with status {@code 400 (Bad Request)} if the orderRecieved has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-recieveds")
    public ResponseEntity<OrderRecievedDTO> createOrderRecieved(@RequestBody OrderRecievedDTO orderRecievedDTO) throws URISyntaxException {
        log.debug("REST request to save OrderRecieved : {}", orderRecievedDTO);
        if (orderRecievedDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderRecieved cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderRecievedDTO result = orderRecievedService.save(orderRecievedDTO);
        return ResponseEntity
            .created(new URI("/api/order-recieveds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-recieveds/:id} : Updates an existing orderRecieved.
     *
     * @param id the id of the orderRecievedDTO to save.
     * @param orderRecievedDTO the orderRecievedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderRecievedDTO,
     * or with status {@code 400 (Bad Request)} if the orderRecievedDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderRecievedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-recieveds/{id}")
    public ResponseEntity<OrderRecievedDTO> updateOrderRecieved(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderRecievedDTO orderRecievedDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderRecieved : {}, {}", id, orderRecievedDTO);
        if (orderRecievedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderRecievedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRecievedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderRecievedDTO result = orderRecievedService.update(orderRecievedDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderRecievedDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-recieveds/:id} : Partial updates given fields of an existing orderRecieved, field will ignore if it is null
     *
     * @param id the id of the orderRecievedDTO to save.
     * @param orderRecievedDTO the orderRecievedDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderRecievedDTO,
     * or with status {@code 400 (Bad Request)} if the orderRecievedDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderRecievedDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderRecievedDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-recieveds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderRecievedDTO> partialUpdateOrderRecieved(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody OrderRecievedDTO orderRecievedDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderRecieved partially : {}, {}", id, orderRecievedDTO);
        if (orderRecievedDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderRecievedDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderRecievedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderRecievedDTO> result = orderRecievedService.partialUpdate(orderRecievedDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, orderRecievedDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-recieveds} : get all the orderRecieveds.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderRecieveds in body.
     */
    @GetMapping("/order-recieveds")
    public ResponseEntity<List<OrderRecievedDTO>> getAllOrderRecieveds(
        OrderRecievedCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get OrderRecieveds by criteria: {}", criteria);
        Page<OrderRecievedDTO> page = orderRecievedQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-recieveds/count} : count all the orderRecieveds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/order-recieveds/count")
    public ResponseEntity<Long> countOrderRecieveds(OrderRecievedCriteria criteria) {
        log.debug("REST request to count OrderRecieveds by criteria: {}", criteria);
        return ResponseEntity.ok().body(orderRecievedQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /order-recieveds/:id} : get the "id" orderRecieved.
     *
     * @param id the id of the orderRecievedDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderRecievedDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-recieveds/{id}")
    public ResponseEntity<OrderRecievedDTO> getOrderRecieved(@PathVariable Long id) {
        log.debug("REST request to get OrderRecieved : {}", id);
        Optional<OrderRecievedDTO> orderRecievedDTO = orderRecievedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderRecievedDTO);
    }

    /**
     * {@code DELETE  /order-recieveds/:id} : delete the "id" orderRecieved.
     *
     * @param id the id of the orderRecievedDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-recieveds/{id}")
    public ResponseEntity<Void> deleteOrderRecieved(@PathVariable Long id) {
        log.debug("REST request to delete OrderRecieved : {}", id);
        orderRecievedService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
