package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.OrderRecieved;
import com.mycompany.myapp.repository.OrderRecievedRepository;
import com.mycompany.myapp.service.dto.OrderRecievedDTO;
import com.mycompany.myapp.service.mapper.OrderRecievedMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderRecieved}.
 */
@Service
@Transactional
public class OrderRecievedService {

    private final Logger log = LoggerFactory.getLogger(OrderRecievedService.class);

    private final OrderRecievedRepository orderRecievedRepository;

    private final OrderRecievedMapper orderRecievedMapper;

    public OrderRecievedService(OrderRecievedRepository orderRecievedRepository, OrderRecievedMapper orderRecievedMapper) {
        this.orderRecievedRepository = orderRecievedRepository;
        this.orderRecievedMapper = orderRecievedMapper;
    }

    /**
     * Save a orderRecieved.
     *
     * @param orderRecievedDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderRecievedDTO save(OrderRecievedDTO orderRecievedDTO) {
        log.debug("Request to save OrderRecieved : {}", orderRecievedDTO);
        OrderRecieved orderRecieved = orderRecievedMapper.toEntity(orderRecievedDTO);
        orderRecieved = orderRecievedRepository.save(orderRecieved);
        return orderRecievedMapper.toDto(orderRecieved);
    }

    /**
     * Update a orderRecieved.
     *
     * @param orderRecievedDTO the entity to save.
     * @return the persisted entity.
     */
    public OrderRecievedDTO update(OrderRecievedDTO orderRecievedDTO) {
        log.debug("Request to update OrderRecieved : {}", orderRecievedDTO);
        OrderRecieved orderRecieved = orderRecievedMapper.toEntity(orderRecievedDTO);
        orderRecieved = orderRecievedRepository.save(orderRecieved);
        return orderRecievedMapper.toDto(orderRecieved);
    }

    /**
     * Partially update a orderRecieved.
     *
     * @param orderRecievedDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrderRecievedDTO> partialUpdate(OrderRecievedDTO orderRecievedDTO) {
        log.debug("Request to partially update OrderRecieved : {}", orderRecievedDTO);

        return orderRecievedRepository
            .findById(orderRecievedDTO.getId())
            .map(existingOrderRecieved -> {
                orderRecievedMapper.partialUpdate(existingOrderRecieved, orderRecievedDTO);

                return existingOrderRecieved;
            })
            .map(orderRecievedRepository::save)
            .map(orderRecievedMapper::toDto);
    }

    /**
     * Get all the orderRecieveds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OrderRecievedDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderRecieveds");
        return orderRecievedRepository.findAll(pageable).map(orderRecievedMapper::toDto);
    }

    /**
     * Get one orderRecieved by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrderRecievedDTO> findOne(Long id) {
        log.debug("Request to get OrderRecieved : {}", id);
        return orderRecievedRepository.findById(id).map(orderRecievedMapper::toDto);
    }

    /**
     * Delete the orderRecieved by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete OrderRecieved : {}", id);
        orderRecievedRepository.deleteById(id);
    }
}
