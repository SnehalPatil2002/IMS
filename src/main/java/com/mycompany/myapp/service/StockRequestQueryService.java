package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.StockRequest;
import com.mycompany.myapp.repository.StockRequestRepository;
import com.mycompany.myapp.service.criteria.StockRequestCriteria;
import com.mycompany.myapp.service.dto.StockRequestDTO;
import com.mycompany.myapp.service.mapper.StockRequestMapper;
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
 * Service for executing complex queries for {@link StockRequest} entities in the database.
 * The main input is a {@link StockRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockRequestDTO} or a {@link Page} of {@link StockRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockRequestQueryService extends QueryService<StockRequest> {

    private final Logger log = LoggerFactory.getLogger(StockRequestQueryService.class);

    private final StockRequestRepository stockRequestRepository;

    private final StockRequestMapper stockRequestMapper;

    public StockRequestQueryService(StockRequestRepository stockRequestRepository, StockRequestMapper stockRequestMapper) {
        this.stockRequestRepository = stockRequestRepository;
        this.stockRequestMapper = stockRequestMapper;
    }

    /**
     * Return a {@link List} of {@link StockRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockRequestDTO> findByCriteria(StockRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StockRequest> specification = createSpecification(criteria);
        return stockRequestMapper.toDto(stockRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockRequestDTO> findByCriteria(StockRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockRequest> specification = createSpecification(criteria);
        return stockRequestRepository.findAll(specification, page).map(stockRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StockRequest> specification = createSpecification(criteria);
        return stockRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link StockRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockRequest> createSpecification(StockRequestCriteria criteria) {
        Specification<StockRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockRequest_.id));
            }
            if (criteria.getQtyRequired() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyRequired(), StockRequest_.qtyRequired));
            }
            if (criteria.getReqDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReqDate(), StockRequest_.reqDate));
            }
            if (criteria.getIsProd() != null) {
                specification = specification.and(buildSpecification(criteria.getIsProd(), StockRequest_.isProd));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), StockRequest_.status));
            }
            if (criteria.getRawMaterialId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getRawMaterialId(),
                            root -> root.join(StockRequest_.rawMaterial, JoinType.LEFT).get(RawMaterial_.id)
                        )
                    );
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductId(),
                            root -> root.join(StockRequest_.product, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
            if (criteria.getProductsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductsId(),
                            root -> root.join(StockRequest_.products, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
            if (criteria.getProductionLineId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductionLineId(),
                            root -> root.join(StockRequest_.productionLine, JoinType.LEFT).get(ProductionLine_.id)
                        )
                    );
            }
            if (criteria.getProjectsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectsId(),
                            root -> root.join(StockRequest_.projects, JoinType.LEFT).get(Projects_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
