package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PurchaseQuotation;
import com.mycompany.myapp.repository.PurchaseQuotationRepository;
import com.mycompany.myapp.service.dto.PurchaseQuotationDTO;
import com.mycompany.myapp.service.mapper.PurchaseQuotationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link PurchaseQuotation}.
 */
@Service
@Transactional
public class PurchaseQuotationService {

    private final Logger log = LoggerFactory.getLogger(PurchaseQuotationService.class);

    private final PurchaseQuotationRepository purchaseQuotationRepository;

    private final PurchaseQuotationMapper purchaseQuotationMapper;

    public PurchaseQuotationService(
        PurchaseQuotationRepository purchaseQuotationRepository,
        PurchaseQuotationMapper purchaseQuotationMapper
    ) {
        this.purchaseQuotationRepository = purchaseQuotationRepository;
        this.purchaseQuotationMapper = purchaseQuotationMapper;
    }

    /**
     * Save a purchaseQuotation.
     *
     * @param purchaseQuotationDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseQuotationDTO save(PurchaseQuotationDTO purchaseQuotationDTO) {
        log.debug("Request to save PurchaseQuotation : {}", purchaseQuotationDTO);
        PurchaseQuotation purchaseQuotation = purchaseQuotationMapper.toEntity(purchaseQuotationDTO);
        purchaseQuotation = purchaseQuotationRepository.save(purchaseQuotation);
        return purchaseQuotationMapper.toDto(purchaseQuotation);
    }

    /**
     * Update a purchaseQuotation.
     *
     * @param purchaseQuotationDTO the entity to save.
     * @return the persisted entity.
     */
    public PurchaseQuotationDTO update(PurchaseQuotationDTO purchaseQuotationDTO) {
        log.debug("Request to update PurchaseQuotation : {}", purchaseQuotationDTO);
        PurchaseQuotation purchaseQuotation = purchaseQuotationMapper.toEntity(purchaseQuotationDTO);
        purchaseQuotation = purchaseQuotationRepository.save(purchaseQuotation);
        return purchaseQuotationMapper.toDto(purchaseQuotation);
    }

    /**
     * Partially update a purchaseQuotation.
     *
     * @param purchaseQuotationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PurchaseQuotationDTO> partialUpdate(PurchaseQuotationDTO purchaseQuotationDTO) {
        log.debug("Request to partially update PurchaseQuotation : {}", purchaseQuotationDTO);

        return purchaseQuotationRepository
            .findById(purchaseQuotationDTO.getId())
            .map(existingPurchaseQuotation -> {
                purchaseQuotationMapper.partialUpdate(existingPurchaseQuotation, purchaseQuotationDTO);

                return existingPurchaseQuotation;
            })
            .map(purchaseQuotationRepository::save)
            .map(purchaseQuotationMapper::toDto);
    }

    /**
     * Get all the purchaseQuotations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PurchaseQuotationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PurchaseQuotations");
        return purchaseQuotationRepository.findAll(pageable).map(purchaseQuotationMapper::toDto);
    }

    /**
     * Get one purchaseQuotation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PurchaseQuotationDTO> findOne(Long id) {
        log.debug("Request to get PurchaseQuotation : {}", id);
        return purchaseQuotationRepository.findById(id).map(purchaseQuotationMapper::toDto);
    }

    /**
     * Delete the purchaseQuotation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PurchaseQuotation : {}", id);
        purchaseQuotationRepository.deleteById(id);
    }
}
