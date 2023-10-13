package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ProductConsumption;
import com.mycompany.myapp.repository.ProductConsumptionRepository;
import com.mycompany.myapp.service.dto.ProductConsumptionDTO;
import com.mycompany.myapp.service.mapper.ProductConsumptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductConsumption}.
 */
@Service
@Transactional
public class ProductConsumptionService {

    private final Logger log = LoggerFactory.getLogger(ProductConsumptionService.class);

    private final ProductConsumptionRepository productConsumptionRepository;

    private final ProductConsumptionMapper productConsumptionMapper;

    public ProductConsumptionService(
        ProductConsumptionRepository productConsumptionRepository,
        ProductConsumptionMapper productConsumptionMapper
    ) {
        this.productConsumptionRepository = productConsumptionRepository;
        this.productConsumptionMapper = productConsumptionMapper;
    }

    /**
     * Save a productConsumption.
     *
     * @param productConsumptionDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductConsumptionDTO save(ProductConsumptionDTO productConsumptionDTO) {
        log.debug("Request to save ProductConsumption : {}", productConsumptionDTO);
        ProductConsumption productConsumption = productConsumptionMapper.toEntity(productConsumptionDTO);
        productConsumption = productConsumptionRepository.save(productConsumption);
        return productConsumptionMapper.toDto(productConsumption);
    }

    /**
     * Update a productConsumption.
     *
     * @param productConsumptionDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductConsumptionDTO update(ProductConsumptionDTO productConsumptionDTO) {
        log.debug("Request to update ProductConsumption : {}", productConsumptionDTO);
        ProductConsumption productConsumption = productConsumptionMapper.toEntity(productConsumptionDTO);
        productConsumption = productConsumptionRepository.save(productConsumption);
        return productConsumptionMapper.toDto(productConsumption);
    }

    /**
     * Partially update a productConsumption.
     *
     * @param productConsumptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductConsumptionDTO> partialUpdate(ProductConsumptionDTO productConsumptionDTO) {
        log.debug("Request to partially update ProductConsumption : {}", productConsumptionDTO);

        return productConsumptionRepository
            .findById(productConsumptionDTO.getId())
            .map(existingProductConsumption -> {
                productConsumptionMapper.partialUpdate(existingProductConsumption, productConsumptionDTO);

                return existingProductConsumption;
            })
            .map(productConsumptionRepository::save)
            .map(productConsumptionMapper::toDto);
    }

    /**
     * Get all the productConsumptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductConsumptionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductConsumptions");
        return productConsumptionRepository.findAll(pageable).map(productConsumptionMapper::toDto);
    }

    /**
     * Get all the productConsumptions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProductConsumptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productConsumptionRepository.findAllWithEagerRelationships(pageable).map(productConsumptionMapper::toDto);
    }

    /**
     * Get one productConsumption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductConsumptionDTO> findOne(Long id) {
        log.debug("Request to get ProductConsumption : {}", id);
        return productConsumptionRepository.findOneWithEagerRelationships(id).map(productConsumptionMapper::toDto);
    }

    /**
     * Delete the productConsumption by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductConsumption : {}", id);
        productConsumptionRepository.deleteById(id);
    }
}
