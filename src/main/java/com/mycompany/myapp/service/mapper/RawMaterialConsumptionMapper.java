package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.RawMaterialConsumption;
import com.mycompany.myapp.service.dto.ProductionLineDTO;
import com.mycompany.myapp.service.dto.ProductsDTO;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import com.mycompany.myapp.service.dto.RawMaterialConsumptionDTO;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RawMaterialConsumption} and its DTO {@link RawMaterialConsumptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface RawMaterialConsumptionMapper extends EntityMapper<RawMaterialConsumptionDTO, RawMaterialConsumption> {
    @Mapping(target = "projects", source = "projects", qualifiedByName = "projectsId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productsId")
    @Mapping(target = "productionLine", source = "productionLine", qualifiedByName = "productionLineId")
    @Mapping(target = "rawMaterial", source = "rawMaterial", qualifiedByName = "rawMaterialId")
    RawMaterialConsumptionDTO toDto(RawMaterialConsumption s);

    @Named("projectsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectsDTO toDtoProjectsId(Projects projects);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);

    @Named("productionLineId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductionLineDTO toDtoProductionLineId(ProductionLine productionLine);

    @Named("rawMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RawMaterialDTO toDtoRawMaterialId(RawMaterial rawMaterial);
}
