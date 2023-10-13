package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.repository.RawMaterialRepository;
import com.mycompany.myapp.service.criteria.RawMaterialCriteria;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
import com.mycompany.myapp.service.mapper.RawMaterialMapper;
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
 * Service for executing complex queries for {@link RawMaterial} entities in the database.
 * The main input is a {@link RawMaterialCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RawMaterialDTO} or a {@link Page} of {@link RawMaterialDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RawMaterialQueryService extends QueryService<RawMaterial> {

    private final Logger log = LoggerFactory.getLogger(RawMaterialQueryService.class);

    private final RawMaterialRepository rawMaterialRepository;

    private final RawMaterialMapper rawMaterialMapper;

    public RawMaterialQueryService(RawMaterialRepository rawMaterialRepository, RawMaterialMapper rawMaterialMapper) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    /**
     * Return a {@link List} of {@link RawMaterialDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RawMaterialDTO> findByCriteria(RawMaterialCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RawMaterial> specification = createSpecification(criteria);
        return rawMaterialMapper.toDto(rawMaterialRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RawMaterialDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RawMaterialDTO> findByCriteria(RawMaterialCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RawMaterial> specification = createSpecification(criteria);
        return rawMaterialRepository.findAll(specification, page).map(rawMaterialMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RawMaterialCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RawMaterial> specification = createSpecification(criteria);
        return rawMaterialRepository.count(specification);
    }

    /**
     * Function to convert {@link RawMaterialCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RawMaterial> createSpecification(RawMaterialCriteria criteria) {
        Specification<RawMaterial> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RawMaterial_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), RawMaterial_.name));
            }
            if (criteria.getBarcode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBarcode(), RawMaterial_.barcode));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), RawMaterial_.quantity));
            }
            if (criteria.getUnitPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUnitPrice(), RawMaterial_.unitPrice));
            }
            if (criteria.getUnitMeasure() != null) {
                specification = specification.and(buildSpecification(criteria.getUnitMeasure(), RawMaterial_.unitMeasure));
            }
            if (criteria.getGstPercentage() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getGstPercentage(), RawMaterial_.gstPercentage));
            }
            if (criteria.getReorderPoint() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReorderPoint(), RawMaterial_.reorderPoint));
            }
            if (criteria.getProductsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getProductsId(),
                            root -> root.join(RawMaterial_.products, JoinType.LEFT).get(Products_.id)
                        )
                    );
            }
            if (criteria.getWarehouseId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWarehouseId(),
                            root -> root.join(RawMaterial_.warehouse, JoinType.LEFT).get(Warehouse_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
