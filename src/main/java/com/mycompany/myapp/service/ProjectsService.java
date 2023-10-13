package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.repository.ProjectsRepository;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import com.mycompany.myapp.service.mapper.ProjectsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Projects}.
 */
@Service
@Transactional
public class ProjectsService {

    private final Logger log = LoggerFactory.getLogger(ProjectsService.class);

    private final ProjectsRepository projectsRepository;

    private final ProjectsMapper projectsMapper;

    public ProjectsService(ProjectsRepository projectsRepository, ProjectsMapper projectsMapper) {
        this.projectsRepository = projectsRepository;
        this.projectsMapper = projectsMapper;
    }

    /**
     * Save a projects.
     *
     * @param projectsDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectsDTO save(ProjectsDTO projectsDTO) {
        log.debug("Request to save Projects : {}", projectsDTO);
        Projects projects = projectsMapper.toEntity(projectsDTO);
        projects = projectsRepository.save(projects);
        return projectsMapper.toDto(projects);
    }

    /**
     * Update a projects.
     *
     * @param projectsDTO the entity to save.
     * @return the persisted entity.
     */
    public ProjectsDTO update(ProjectsDTO projectsDTO) {
        log.debug("Request to update Projects : {}", projectsDTO);
        Projects projects = projectsMapper.toEntity(projectsDTO);
        projects = projectsRepository.save(projects);
        return projectsMapper.toDto(projects);
    }

    /**
     * Partially update a projects.
     *
     * @param projectsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProjectsDTO> partialUpdate(ProjectsDTO projectsDTO) {
        log.debug("Request to partially update Projects : {}", projectsDTO);

        return projectsRepository
            .findById(projectsDTO.getId())
            .map(existingProjects -> {
                projectsMapper.partialUpdate(existingProjects, projectsDTO);

                return existingProjects;
            })
            .map(projectsRepository::save)
            .map(projectsMapper::toDto);
    }

    /**
     * Get all the projects.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Projects");
        return projectsRepository.findAll(pageable).map(projectsMapper::toDto);
    }

    /**
     * Get one projects by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProjectsDTO> findOne(Long id) {
        log.debug("Request to get Projects : {}", id);
        return projectsRepository.findById(id).map(projectsMapper::toDto);
    }

    /**
     * Delete the projects by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Projects : {}", id);
        projectsRepository.deleteById(id);
    }
}
