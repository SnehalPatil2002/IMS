package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.OrderRecieved;
import com.mycompany.myapp.repository.OrderRecievedRepository;
import com.mycompany.myapp.service.criteria.OrderRecievedCriteria;
import com.mycompany.myapp.service.dto.OrderRecievedDTO;
import com.mycompany.myapp.service.mapper.OrderRecievedMapper;
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
 * Service for executing complex queries for {@link OrderRecieved} entities in the database.
 * The main input is a {@link OrderRecievedCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OrderRecievedDTO} or a {@link Page} of {@link OrderRecievedDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OrderRecievedQueryService extends QueryService<OrderRecieved> {

    private final Logger log = LoggerFactory.getLogger(OrderRecievedQueryService.class);

    private final OrderRecievedRepository orderRecievedRepository;

    private final OrderRecievedMapper orderRecievedMapper;

    public OrderRecievedQueryService(OrderRecievedRepository orderRecievedRepository, OrderRecievedMapper orderRecievedMapper) {
        this.orderRecievedRepository = orderRecievedRepository;
        this.orderRecievedMapper = orderRecievedMapper;
    }

    /**
     * Return a {@link List} of {@link OrderRecievedDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OrderRecievedDTO> findByCriteria(OrderRecievedCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OrderRecieved> specification = createSpecification(criteria);
        return orderRecievedMapper.toDto(orderRecievedRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OrderRecievedDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderRecievedDTO> findByCriteria(OrderRecievedCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OrderRecieved> specification = createSpecification(criteria);
        return orderRecievedRepository.findAll(specification, page).map(orderRecievedMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OrderRecievedCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OrderRecieved> specification = createSpecification(criteria);
        return orderRecievedRepository.count(specification);
    }

    /**
     * Function to convert {@link OrderRecievedCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OrderRecieved> createSpecification(OrderRecievedCriteria criteria) {
        Specification<OrderRecieved> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OrderRecieved_.id));
            }
            if (criteria.getOrDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrDate(), OrderRecieved_.orDate));
            }
            if (criteria.getQtyOrdered() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyOrdered(), OrderRecieved_.qtyOrdered));
            }
            if (criteria.getQtyRecieved() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyRecieved(), OrderRecieved_.qtyRecieved));
            }
            if (criteria.getManufacturingDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getManufacturingDate(), OrderRecieved_.manufacturingDate));
            }
            if (criteria.getExpiryDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpiryDate(), OrderRecieved_.expiryDate));
            }
            if (criteria.getQtyApproved() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyApproved(), OrderRecieved_.qtyApproved));
            }
            if (criteria.getQtyRejected() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQtyRejected(), OrderRecieved_.qtyRejected));
            }
            if (criteria.getPurchaseQuotationId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getPurchaseQuotationId(),
                            root -> root.join(OrderRecieved_.purchaseQuotation, JoinType.LEFT).get(PurchaseQuotation_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
