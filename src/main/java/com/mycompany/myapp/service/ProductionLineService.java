package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.repository.ProductionLineRepository;
import com.mycompany.myapp.service.dto.ProductionLineDTO;
import com.mycompany.myapp.service.mapper.ProductionLineMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductionLine}.
 */
@Service
@Transactional
public class ProductionLineService {

    private final Logger log = LoggerFactory.getLogger(ProductionLineService.class);

    private final ProductionLineRepository productionLineRepository;

    private final ProductionLineMapper productionLineMapper;

    public ProductionLineService(ProductionLineRepository productionLineRepository, ProductionLineMapper productionLineMapper) {
        this.productionLineRepository = productionLineRepository;
        this.productionLineMapper = productionLineMapper;
    }

    /**
     * Save a productionLine.
     *
     * @param productionLineDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductionLineDTO save(ProductionLineDTO productionLineDTO) {
        log.debug("Request to save ProductionLine : {}", productionLineDTO);
        ProductionLine productionLine = productionLineMapper.toEntity(productionLineDTO);
        productionLine = productionLineRepository.save(productionLine);
        return productionLineMapper.toDto(productionLine);
    }

    /**
     * Update a productionLine.
     *
     * @param productionLineDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductionLineDTO update(ProductionLineDTO productionLineDTO) {
        log.debug("Request to update ProductionLine : {}", productionLineDTO);
        ProductionLine productionLine = productionLineMapper.toEntity(productionLineDTO);
        productionLine = productionLineRepository.save(productionLine);
        return productionLineMapper.toDto(productionLine);
    }

    /**
     * Partially update a productionLine.
     *
     * @param productionLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductionLineDTO> partialUpdate(ProductionLineDTO productionLineDTO) {
        log.debug("Request to partially update ProductionLine : {}", productionLineDTO);

        return productionLineRepository
            .findById(productionLineDTO.getId())
            .map(existingProductionLine -> {
                productionLineMapper.partialUpdate(existingProductionLine, productionLineDTO);

                return existingProductionLine;
            })
            .map(productionLineRepository::save)
            .map(productionLineMapper::toDto);
    }

    /**
     * Get all the productionLines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProductionLineDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductionLines");
        return productionLineRepository.findAll(pageable).map(productionLineMapper::toDto);
    }

    /**
     * Get one productionLine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductionLineDTO> findOne(Long id) {
        log.debug("Request to get ProductionLine : {}", id);
        return productionLineRepository.findById(id).map(productionLineMapper::toDto);
    }

    /**
     * Delete the productionLine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductionLine : {}", id);
        productionLineRepository.deleteById(id);
    }
}
