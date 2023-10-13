package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.StockRequest;
import com.mycompany.myapp.repository.StockRequestRepository;
import com.mycompany.myapp.service.dto.StockRequestDTO;
import com.mycompany.myapp.service.mapper.StockRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockRequest}.
 */
@Service
@Transactional
public class StockRequestService {

    private final Logger log = LoggerFactory.getLogger(StockRequestService.class);

    private final StockRequestRepository stockRequestRepository;

    private final StockRequestMapper stockRequestMapper;

    public StockRequestService(StockRequestRepository stockRequestRepository, StockRequestMapper stockRequestMapper) {
        this.stockRequestRepository = stockRequestRepository;
        this.stockRequestMapper = stockRequestMapper;
    }

    /**
     * Save a stockRequest.
     *
     * @param stockRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public StockRequestDTO save(StockRequestDTO stockRequestDTO) {
        log.debug("Request to save StockRequest : {}", stockRequestDTO);
        StockRequest stockRequest = stockRequestMapper.toEntity(stockRequestDTO);
        stockRequest = stockRequestRepository.save(stockRequest);
        return stockRequestMapper.toDto(stockRequest);
    }

    /**
     * Update a stockRequest.
     *
     * @param stockRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public StockRequestDTO update(StockRequestDTO stockRequestDTO) {
        log.debug("Request to update StockRequest : {}", stockRequestDTO);
        StockRequest stockRequest = stockRequestMapper.toEntity(stockRequestDTO);
        stockRequest = stockRequestRepository.save(stockRequest);
        return stockRequestMapper.toDto(stockRequest);
    }

    /**
     * Partially update a stockRequest.
     *
     * @param stockRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockRequestDTO> partialUpdate(StockRequestDTO stockRequestDTO) {
        log.debug("Request to partially update StockRequest : {}", stockRequestDTO);

        return stockRequestRepository
            .findById(stockRequestDTO.getId())
            .map(existingStockRequest -> {
                stockRequestMapper.partialUpdate(existingStockRequest, stockRequestDTO);

                return existingStockRequest;
            })
            .map(stockRequestRepository::save)
            .map(stockRequestMapper::toDto);
    }

    /**
     * Get all the stockRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<StockRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all StockRequests");
        return stockRequestRepository.findAll(pageable).map(stockRequestMapper::toDto);
    }

    /**
     * Get one stockRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockRequestDTO> findOne(Long id) {
        log.debug("Request to get StockRequest : {}", id);
        return stockRequestRepository.findById(id).map(stockRequestMapper::toDto);
    }

    /**
     * Delete the stockRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete StockRequest : {}", id);
        stockRequestRepository.deleteById(id);
    }
}
