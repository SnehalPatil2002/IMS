package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.RawMaterialConsumption;
import com.mycompany.myapp.repository.RawMaterialConsumptionRepository;
import com.mycompany.myapp.service.dto.RawMaterialConsumptionDTO;
import com.mycompany.myapp.service.mapper.RawMaterialConsumptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RawMaterialConsumption}.
 */
@Service
@Transactional
public class RawMaterialConsumptionService {

    private final Logger log = LoggerFactory.getLogger(RawMaterialConsumptionService.class);

    private final RawMaterialConsumptionRepository rawMaterialConsumptionRepository;

    private final RawMaterialConsumptionMapper rawMaterialConsumptionMapper;

    public RawMaterialConsumptionService(
        RawMaterialConsumptionRepository rawMaterialConsumptionRepository,
        RawMaterialConsumptionMapper rawMaterialConsumptionMapper
    ) {
        this.rawMaterialConsumptionRepository = rawMaterialConsumptionRepository;
        this.rawMaterialConsumptionMapper = rawMaterialConsumptionMapper;
    }

    /**
     * Save a rawMaterialConsumption.
     *
     * @param rawMaterialConsumptionDTO the entity to save.
     * @return the persisted entity.
     */
    public RawMaterialConsumptionDTO save(RawMaterialConsumptionDTO rawMaterialConsumptionDTO) {
        log.debug("Request to save RawMaterialConsumption : {}", rawMaterialConsumptionDTO);
        RawMaterialConsumption rawMaterialConsumption = rawMaterialConsumptionMapper.toEntity(rawMaterialConsumptionDTO);
        rawMaterialConsumption = rawMaterialConsumptionRepository.save(rawMaterialConsumption);
        return rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);
    }

    /**
     * Update a rawMaterialConsumption.
     *
     * @param rawMaterialConsumptionDTO the entity to save.
     * @return the persisted entity.
     */
    public RawMaterialConsumptionDTO update(RawMaterialConsumptionDTO rawMaterialConsumptionDTO) {
        log.debug("Request to update RawMaterialConsumption : {}", rawMaterialConsumptionDTO);
        RawMaterialConsumption rawMaterialConsumption = rawMaterialConsumptionMapper.toEntity(rawMaterialConsumptionDTO);
        rawMaterialConsumption = rawMaterialConsumptionRepository.save(rawMaterialConsumption);
        return rawMaterialConsumptionMapper.toDto(rawMaterialConsumption);
    }

    /**
     * Partially update a rawMaterialConsumption.
     *
     * @param rawMaterialConsumptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RawMaterialConsumptionDTO> partialUpdate(RawMaterialConsumptionDTO rawMaterialConsumptionDTO) {
        log.debug("Request to partially update RawMaterialConsumption : {}", rawMaterialConsumptionDTO);

        return rawMaterialConsumptionRepository
            .findById(rawMaterialConsumptionDTO.getId())
            .map(existingRawMaterialConsumption -> {
                rawMaterialConsumptionMapper.partialUpdate(existingRawMaterialConsumption, rawMaterialConsumptionDTO);

                return existingRawMaterialConsumption;
            })
            .map(rawMaterialConsumptionRepository::save)
            .map(rawMaterialConsumptionMapper::toDto);
    }

    /**
     * Get all the rawMaterialConsumptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RawMaterialConsumptionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RawMaterialConsumptions");
        return rawMaterialConsumptionRepository.findAll(pageable).map(rawMaterialConsumptionMapper::toDto);
    }

    /**
     * Get one rawMaterialConsumption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RawMaterialConsumptionDTO> findOne(Long id) {
        log.debug("Request to get RawMaterialConsumption : {}", id);
        return rawMaterialConsumptionRepository.findById(id).map(rawMaterialConsumptionMapper::toDto);
    }

    /**
     * Delete the rawMaterialConsumption by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RawMaterialConsumption : {}", id);
        rawMaterialConsumptionRepository.deleteById(id);
    }
}
