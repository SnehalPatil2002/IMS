package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.SalesOrderd;
import com.mycompany.myapp.repository.SalesOrderdRepository;
import com.mycompany.myapp.service.dto.SalesOrderdDTO;
import com.mycompany.myapp.service.mapper.SalesOrderdMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SalesOrderd}.
 */
@Service
@Transactional
public class SalesOrderdService {

    private final Logger log = LoggerFactory.getLogger(SalesOrderdService.class);

    private final SalesOrderdRepository salesOrderdRepository;

    private final SalesOrderdMapper salesOrderdMapper;

    public SalesOrderdService(SalesOrderdRepository salesOrderdRepository, SalesOrderdMapper salesOrderdMapper) {
        this.salesOrderdRepository = salesOrderdRepository;
        this.salesOrderdMapper = salesOrderdMapper;
    }

    /**
     * Save a salesOrderd.
     *
     * @param salesOrderdDTO the entity to save.
     * @return the persisted entity.
     */
    public SalesOrderdDTO save(SalesOrderdDTO salesOrderdDTO) {
        log.debug("Request to save SalesOrderd : {}", salesOrderdDTO);
        SalesOrderd salesOrderd = salesOrderdMapper.toEntity(salesOrderdDTO);
        salesOrderd = salesOrderdRepository.save(salesOrderd);
        return salesOrderdMapper.toDto(salesOrderd);
    }

    /**
     * Update a salesOrderd.
     *
     * @param salesOrderdDTO the entity to save.
     * @return the persisted entity.
     */
    public SalesOrderdDTO update(SalesOrderdDTO salesOrderdDTO) {
        log.debug("Request to update SalesOrderd : {}", salesOrderdDTO);
        SalesOrderd salesOrderd = salesOrderdMapper.toEntity(salesOrderdDTO);
        salesOrderd = salesOrderdRepository.save(salesOrderd);
        return salesOrderdMapper.toDto(salesOrderd);
    }

    /**
     * Partially update a salesOrderd.
     *
     * @param salesOrderdDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SalesOrderdDTO> partialUpdate(SalesOrderdDTO salesOrderdDTO) {
        log.debug("Request to partially update SalesOrderd : {}", salesOrderdDTO);

        return salesOrderdRepository
            .findById(salesOrderdDTO.getId())
            .map(existingSalesOrderd -> {
                salesOrderdMapper.partialUpdate(existingSalesOrderd, salesOrderdDTO);

                return existingSalesOrderd;
            })
            .map(salesOrderdRepository::save)
            .map(salesOrderdMapper::toDto);
    }

    /**
     * Get all the salesOrderds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesOrderdDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SalesOrderds");
        return salesOrderdRepository.findAll(pageable).map(salesOrderdMapper::toDto);
    }

    /**
     * Get one salesOrderd by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SalesOrderdDTO> findOne(Long id) {
        log.debug("Request to get SalesOrderd : {}", id);
        return salesOrderdRepository.findById(id).map(salesOrderdMapper::toDto);
    }

    /**
     * Delete the salesOrderd by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SalesOrderd : {}", id);
        salesOrderdRepository.deleteById(id);
    }
}
