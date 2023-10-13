package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.repository.ProductionLineRepository;
import com.mycompany.myapp.service.criteria.ProductionLineCriteria;
import com.mycompany.myapp.service.dto.ProductionLineDTO;
import com.mycompany.myapp.service.mapper.ProductionLineMapper;
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
 * Service for executing complex queries for {@link ProductionLine} entities in the database.
 * The main input is a {@link ProductionLineCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProductionLineDTO} or a {@link Page} of {@link ProductionLineDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProductionLineQueryService extends QueryService<ProductionLine> {

    private final Logger log = LoggerFactory.getLogger(ProductionLineQueryService.class);

    private final ProductionLineRepository productionLineRepository;

    private final ProductionLineMapper productionLineMapper;

    public ProductionLineQueryService(ProductionLineRepository productionLineRepository, ProductionLineMapper productionLineMapper) {
        this.productionLineRepository = productionLineRepository;
        this.productionLineMapper = productionLineMapper;
    }

    /**
     * Return a {@link List} of {@link ProductionLineDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProductionLineDTO> findByCriteria(ProductionLineCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ProductionLine> specification = createSpecification(criteria);
        return productionLineMapper.toDto(productionLineRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ProductionLineDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductionLineDTO> findByCriteria(ProductionLineCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ProductionLine> specification = createSpecification(criteria);
        return productionLineRepository.findAll(specification, page).map(productionLineMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProductionLineCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ProductionLine> specification = createSpecification(criteria);
        return productionLineRepository.count(specification);
    }

    /**
     * Function to convert {@link ProductionLineCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProductionLine> createSpecification(ProductionLineCriteria criteria) {
        Specification<ProductionLine> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ProductionLine_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), ProductionLine_.description));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), ProductionLine_.isActive));
            }
            if (criteria.getProductId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductId(),
                            root -> root.join(ProductionLine_.product, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
