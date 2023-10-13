package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.domain.TotalConsumption;
import com.mycompany.myapp.service.dto.ProductsDTO;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import com.mycompany.myapp.service.dto.TotalConsumptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TotalConsumption} and its DTO {@link TotalConsumptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TotalConsumptionMapper extends EntityMapper<TotalConsumptionDTO, TotalConsumption> {
    @Mapping(target = "projects", source = "projects", qualifiedByName = "projectsId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productsId")
    TotalConsumptionDTO toDto(TotalConsumption s);

    @Named("projectsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectsDTO toDtoProjectsId(Projects projects);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);
}
