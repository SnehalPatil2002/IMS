package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.PurchaseRequest;
import com.mycompany.myapp.repository.PurchaseRequestRepository;
import com.mycompany.myapp.service.criteria.PurchaseRequestCriteria;
import com.mycompany.myapp.service.dto.PurchaseRequestDTO;
import com.mycompany.myapp.service.mapper.PurchaseRequestMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PurchaseRequest} entities in the database.
 * The main input is a {@link PurchaseRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PurchaseRequestDTO} or a {@link Page} of {@link PurchaseRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PurchaseRequestQueryService extends QueryService<PurchaseRequest> {

    private final Logger log = LoggerFactory.getLogger(PurchaseRequestQueryService.class);

    private final PurchaseRequestRepository purchaseRequestRepository;

    private final PurchaseRequestMapper purchaseRequestMapper;

    public PurchaseRequestQueryService(PurchaseRequestRepository purchaseRequestRepository, PurchaseRequestMapper purchaseRequestMapper) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.purchaseRequestMapper = purchaseRequestMapper;
    }

    /**
     * Return a {@link List} of {@link PurchaseRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PurchaseRequestDTO> findByCriteria(PurchaseRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PurchaseRequest> specification = createSpecification(criteria);
        return purchaseRequestMapper.toDto(purchaseRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PurchaseRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseRequestDTO> findByCriteria(PurchaseRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PurchaseRequest> specification = createSpecification(criteria);
        return purchaseRequestRepository.findAll(specification, page).map(purchaseRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PurchaseRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PurchaseRequest> specification = createSpecification(criteria);
        return purchaseRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link PurchaseRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PurchaseRequest> createSpecification(PurchaseRequestCriteria criteria) {
        Specification<PurchaseRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PurchaseRequest_.id));
            }
            if (criteria.getQtyRequired() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyRequired(), PurchaseRequest_.qtyRequired));
            }
            if (criteria.getRequestDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRequestDate(), PurchaseRequest_.requestDate));
            }
            if (criteria.getExpectedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpectedDate(), PurchaseRequest_.expectedDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), PurchaseRequest_.status));
            }
            if (criteria.getRawMaterialId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRawMaterialId(),
                            root -> root.join(PurchaseRequest_.rawMaterial, JoinType.LEFT).get(RawMaterial_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
