package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.TotalConsumption;
import com.mycompany.myapp.repository.TotalConsumptionRepository;
import com.mycompany.myapp.service.criteria.TotalConsumptionCriteria;
import com.mycompany.myapp.service.dto.TotalConsumptionDTO;
import com.mycompany.myapp.service.mapper.TotalConsumptionMapper;
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
 * Service for executing complex queries for {@link TotalConsumption} entities in the database.
 * The main input is a {@link TotalConsumptionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TotalConsumptionDTO} or a {@link Page} of {@link TotalConsumptionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TotalConsumptionQueryService extends QueryService<TotalConsumption> {

    private final Logger log = LoggerFactory.getLogger(TotalConsumptionQueryService.class);

    private final TotalConsumptionRepository totalConsumptionRepository;

    private final TotalConsumptionMapper totalConsumptionMapper;

    public TotalConsumptionQueryService(
        TotalConsumptionRepository totalConsumptionRepository,
        TotalConsumptionMapper totalConsumptionMapper
    ) {
        this.totalConsumptionRepository = totalConsumptionRepository;
        this.totalConsumptionMapper = totalConsumptionMapper;
    }

    /**
     * Return a {@link List} of {@link TotalConsumptionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TotalConsumptionDTO> findByCriteria(TotalConsumptionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TotalConsumption> specification = createSpecification(criteria);
        return totalConsumptionMapper.toDto(totalConsumptionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TotalConsumptionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TotalConsumptionDTO> findByCriteria(TotalConsumptionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TotalConsumption> specification = createSpecification(criteria);
        return totalConsumptionRepository.findAll(specification, page).map(totalConsumptionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TotalConsumptionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TotalConsumption> specification = createSpecification(criteria);
        return totalConsumptionRepository.count(specification);
    }

    /**
     * Function to convert {@link TotalConsumptionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TotalConsumption> createSpecification(TotalConsumptionCriteria criteria) {
        Specification<TotalConsumption> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TotalConsumption_.id));
            }
            if (criteria.getTotalMaterialCost() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getTotalMaterialCost(), TotalConsumption_.totalMaterialCost));
            }
            if (criteria.getTotalProductsCost() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getTotalProductsCost(), TotalConsumption_.totalProductsCost));
            }
            if (criteria.getFinalCost() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFinalCost(), TotalConsumption_.finalCost));
            }
            if (criteria.getProjectsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProjectsId(),
                            root -> root.join(TotalConsumption_.projects, JoinType.LEFT).get(Projects_.id)
                        )
                    );
            }
            if (criteria.getProductsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductsId(),
                            root -> root.join(TotalConsumption_.products, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
