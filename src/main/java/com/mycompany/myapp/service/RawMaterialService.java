package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.repository.RawMaterialRepository;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
import com.mycompany.myapp.service.mapper.RawMaterialMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RawMaterial}.
 */
@Service
@Transactional
public class RawMaterialService {

    private final Logger log = LoggerFactory.getLogger(RawMaterialService.class);

    private final RawMaterialRepository rawMaterialRepository;

    private final RawMaterialMapper rawMaterialMapper;

    public RawMaterialService(RawMaterialRepository rawMaterialRepository, RawMaterialMapper rawMaterialMapper) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    /**
     * Save a rawMaterial.
     *
     * @param rawMaterialDTO the entity to save.
     * @return the persisted entity.
     */
    public RawMaterialDTO save(RawMaterialDTO rawMaterialDTO) {
        log.debug("Request to save RawMaterial : {}", rawMaterialDTO);
        RawMaterial rawMaterial = rawMaterialMapper.toEntity(rawMaterialDTO);
        rawMaterial = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toDto(rawMaterial);
    }

    /**
     * Update a rawMaterial.
     *
     * @param rawMaterialDTO the entity to save.
     * @return the persisted entity.
     */
    public RawMaterialDTO update(RawMaterialDTO rawMaterialDTO) {
        log.debug("Request to update RawMaterial : {}", rawMaterialDTO);
        RawMaterial rawMaterial = rawMaterialMapper.toEntity(rawMaterialDTO);
        rawMaterial = rawMaterialRepository.save(rawMaterial);
        return rawMaterialMapper.toDto(rawMaterial);
    }

    /**
     * Partially update a rawMaterial.
     *
     * @param rawMaterialDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RawMaterialDTO> partialUpdate(RawMaterialDTO rawMaterialDTO) {
        log.debug("Request to partially update RawMaterial : {}", rawMaterialDTO);

        return rawMaterialRepository
            .findById(rawMaterialDTO.getId())
            .map(existingRawMaterial -> {
                rawMaterialMapper.partialUpdate(existingRawMaterial, rawMaterialDTO);

                return existingRawMaterial;
            })
            .map(rawMaterialRepository::save)
            .map(rawMaterialMapper::toDto);
    }

    /**
     * Get all the rawMaterials.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RawMaterialDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RawMaterials");
        return rawMaterialRepository.findAll(pageable).map(rawMaterialMapper::toDto);
    }

    /**
     * Get all the rawMaterials with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RawMaterialDTO> findAllWithEagerRelationships(Pageable pageable) {
        return rawMaterialRepository.findAllWithEagerRelationships(pageable).map(rawMaterialMapper::toDto);
    }

    /**
     * Get one rawMaterial by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RawMaterialDTO> findOne(Long id) {
        log.debug("Request to get RawMaterial : {}", id);
        return rawMaterialRepository.findOneWithEagerRelationships(id).map(rawMaterialMapper::toDto);
    }

    /**
     * Delete the rawMaterial by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RawMaterial : {}", id);
        rawMaterialRepository.deleteById(id);
    }
}
