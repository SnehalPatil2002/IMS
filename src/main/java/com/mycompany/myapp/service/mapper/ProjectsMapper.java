package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Projects} and its DTO {@link ProjectsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectsMapper extends EntityMapper<ProjectsDTO, Projects> {}
