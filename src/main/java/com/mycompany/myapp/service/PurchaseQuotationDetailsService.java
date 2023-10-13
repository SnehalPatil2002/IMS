package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PurchaseQuotationDetails;
import com.mycompany.myapp.repository.PurchaseQuotationDetailsRepository;
import com.mycompany.myapp.service.dto.PurchaseQuotationDetailsDTO;
import com.mycompany.myapp.service.mapper.PurchaseQuotationDetailsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PurchaseQuotationDetails}.
 */
@Service
@Transactional
public class PurchaseQuotationDetailsService {

    private final Logger log = LoggerFactory.getLogger(PurchaseQuotationDetailsService.class);

    private final PurchaseQuotationDetailsRepository purchaseQuotationDetailsRepository;

    private final PurchaseQuotationDetailsMapper purchaseQuotationDetailsMapper;

    public PurchaseQuotationDetailsService(
        PurchaseQuotationDetailsRepository purchaseQuotationDetailsRepository,
        PurchaseQuotationDetailsMapper purchaseQuotationDetailsMapper
    ) {
        this.purchaseQuotationDetailsRepository = purchaseQuotationDetailsRepository;
        this.purchaseQuotationDetailsMapper = purchaseQuotationDetailsMapper;
    }

    /**
     * Save a purchaseQuotationDetails.
     *
     * @param purchaseQuotationDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseQuotationDetailsDTO save(PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO) {
        log.debug("Request to save PurchaseQuotationDetails : {}", purchaseQuotationDetailsDTO);
        PurchaseQuotationDetails purchaseQuotationDetails = purchaseQuotationDetailsMapper.toEntity(purchaseQuotationDetailsDTO);
        purchaseQuotationDetails = purchaseQuotationDetailsRepository.save(purchaseQuotationDetails);
        return purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);
    }

    /**
     * Update a purchaseQuotationDetails.
     *
     * @param purchaseQuotationDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseQuotationDetailsDTO update(PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO) {
        log.debug("Request to update PurchaseQuotationDetails : {}", purchaseQuotationDetailsDTO);
        PurchaseQuotationDetails purchaseQuotationDetails = purchaseQuotationDetailsMapper.toEntity(purchaseQuotationDetailsDTO);
        purchaseQuotationDetails = purchaseQuotationDetailsRepository.save(purchaseQuotationDetails);
        return purchaseQuotationDetailsMapper.toDto(purchaseQuotationDetails);
    }

    /**
     * Partially update a purchaseQuotationDetails.
     *
     * @param purchaseQuotationDetailsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseQuotationDetailsDTO> partialUpdate(PurchaseQuotationDetailsDTO purchaseQuotationDetailsDTO) {
        log.debug("Request to partially update PurchaseQuotationDetails : {}", purchaseQuotationDetailsDTO);

        return purchaseQuotationDetailsRepository
            .findById(purchaseQuotationDetailsDTO.getId())
            .map(existingPurchaseQuotationDetails -> {
                purchaseQuotationDetailsMapper.partialUpdate(existingPurchaseQuotationDetails, purchaseQuotationDetailsDTO);

                return existingPurchaseQuotationDetails;
            })
            .map(purchaseQuotationDetailsRepository::save)
            .map(purchaseQuotationDetailsMapper::toDto);
    }

    /**
     * Get all the purchaseQuotationDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseQuotationDetailsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseQuotationDetails");
        return purchaseQuotationDetailsRepository.findAll(pageable).map(purchaseQuotationDetailsMapper::toDto);
    }

    /**
     * Get one purchaseQuotationDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseQuotationDetailsDTO> findOne(Long id) {
        log.debug("Request to get PurchaseQuotationDetails : {}", id);
        return purchaseQuotationDetailsRepository.findById(id).map(purchaseQuotationDetailsMapper::toDto);
    }

    /**
     * Delete the purchaseQuotationDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PurchaseQuotationDetails : {}", id);
        purchaseQuotationDetailsRepository.deleteById(id);
    }
}
