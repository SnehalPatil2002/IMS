package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.TotalConsumption;
import com.mycompany.myapp.repository.TotalConsumptionRepository;
import com.mycompany.myapp.service.dto.TotalConsumptionDTO;
import com.mycompany.myapp.service.mapper.TotalConsumptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TotalConsumption}.
 */
@Service
@Transactional
public class TotalConsumptionService {

    private final Logger log = LoggerFactory.getLogger(TotalConsumptionService.class);

    private final TotalConsumptionRepository totalConsumptionRepository;

    private final TotalConsumptionMapper totalConsumptionMapper;

    public TotalConsumptionService(TotalConsumptionRepository totalConsumptionRepository, TotalConsumptionMapper totalConsumptionMapper) {
        this.totalConsumptionRepository = totalConsumptionRepository;
        this.totalConsumptionMapper = totalConsumptionMapper;
    }

    /**
     * Save a totalConsumption.
     *
     * @param totalConsumptionDTO the entity to save.
     * @return the persisted entity.
     */
    public TotalConsumptionDTO save(TotalConsumptionDTO totalConsumptionDTO) {
        log.debug("Request to save TotalConsumption : {}", totalConsumptionDTO);
        TotalConsumption totalConsumption = totalConsumptionMapper.toEntity(totalConsumptionDTO);
        totalConsumption = totalConsumptionRepository.save(totalConsumption);
        return totalConsumptionMapper.toDto(totalConsumption);
    }

    /**
     * Update a totalConsumption.
     *
     * @param totalConsumptionDTO the entity to save.
     * @return the persisted entity.
     */
    public TotalConsumptionDTO update(TotalConsumptionDTO totalConsumptionDTO) {
        log.debug("Request to update TotalConsumption : {}", totalConsumptionDTO);
        TotalConsumption totalConsumption = totalConsumptionMapper.toEntity(totalConsumptionDTO);
        totalConsumption = totalConsumptionRepository.save(totalConsumption);
        return totalConsumptionMapper.toDto(totalConsumption);
    }

    /**
     * Partially update a totalConsumption.
     *
     * @param totalConsumptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TotalConsumptionDTO> partialUpdate(TotalConsumptionDTO totalConsumptionDTO) {
        log.debug("Request to partially update TotalConsumption : {}", totalConsumptionDTO);

        return totalConsumptionRepository
            .findById(totalConsumptionDTO.getId())
            .map(existingTotalConsumption -> {
                totalConsumptionMapper.partialUpdate(existingTotalConsumption, totalConsumptionDTO);

                return existingTotalConsumption;
            })
            .map(totalConsumptionRepository::save)
            .map(totalConsumptionMapper::toDto);
    }

    /**
     * Get all the totalConsumptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TotalConsumptionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TotalConsumptions");
        return totalConsumptionRepository.findAll(pageable).map(totalConsumptionMapper::toDto);
    }

    /**
     * Get one totalConsumption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TotalConsumptionDTO> findOne(Long id) {
        log.debug("Request to get TotalConsumption : {}", id);
        return totalConsumptionRepository.findById(id).map(totalConsumptionMapper::toDto);
    }

    /**
     * Delete the totalConsumption by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TotalConsumption : {}", id);
        totalConsumptionRepository.deleteById(id);
    }
}
