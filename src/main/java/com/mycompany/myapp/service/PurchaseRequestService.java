package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PurchaseRequest;
import com.mycompany.myapp.repository.PurchaseRequestRepository;
import com.mycompany.myapp.service.dto.PurchaseRequestDTO;
import com.mycompany.myapp.service.mapper.PurchaseRequestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PurchaseRequest}.
 */
@Service
@Transactional
public class PurchaseRequestService {

    private final Logger log = LoggerFactory.getLogger(PurchaseRequestService.class);

    private final PurchaseRequestRepository purchaseRequestRepository;

    private final PurchaseRequestMapper purchaseRequestMapper;

    public PurchaseRequestService(PurchaseRequestRepository purchaseRequestRepository, PurchaseRequestMapper purchaseRequestMapper) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.purchaseRequestMapper = purchaseRequestMapper;
    }

    /**
     * Save a purchaseRequest.
     *
     * @param purchaseRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseRequestDTO save(PurchaseRequestDTO purchaseRequestDTO) {
        log.debug("Request to save PurchaseRequest : {}", purchaseRequestDTO);
        PurchaseRequest purchaseRequest = purchaseRequestMapper.toEntity(purchaseRequestDTO);
        purchaseRequest = purchaseRequestRepository.save(purchaseRequest);
        return purchaseRequestMapper.toDto(purchaseRequest);
    }

    /**
     * Update a purchaseRequest.
     *
     * @param purchaseRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseRequestDTO update(PurchaseRequestDTO purchaseRequestDTO) {
        log.debug("Request to update PurchaseRequest : {}", purchaseRequestDTO);
        PurchaseRequest purchaseRequest = purchaseRequestMapper.toEntity(purchaseRequestDTO);
        purchaseRequest = purchaseRequestRepository.save(purchaseRequest);
        return purchaseRequestMapper.toDto(purchaseRequest);
    }

    /**
     * Partially update a purchaseRequest.
     *
     * @param purchaseRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseRequestDTO> partialUpdate(PurchaseRequestDTO purchaseRequestDTO) {
        log.debug("Request to partially update PurchaseRequest : {}", purchaseRequestDTO);

        return purchaseRequestRepository
            .findById(purchaseRequestDTO.getId())
            .map(existingPurchaseRequest -> {
                purchaseRequestMapper.partialUpdate(existingPurchaseRequest, purchaseRequestDTO);

                return existingPurchaseRequest;
            })
            .map(purchaseRequestRepository::save)
            .map(purchaseRequestMapper::toDto);
    }

    /**
     * Get all the purchaseRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseRequests");
        return purchaseRequestRepository.findAll(pageable).map(purchaseRequestMapper::toDto);
    }

    /**
     * Get one purchaseRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseRequestDTO> findOne(Long id) {
        log.debug("Request to get PurchaseRequest : {}", id);
        return purchaseRequestRepository.findById(id).map(purchaseRequestMapper::toDto);
    }

    /**
     * Delete the purchaseRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PurchaseRequest : {}", id);
        purchaseRequestRepository.deleteById(id);
    }
}
