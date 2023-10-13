package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.StockRequest;
import com.mycompany.myapp.service.dto.ProductionLineDTO;
import com.mycompany.myapp.service.dto.ProductsDTO;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
import com.mycompany.myapp.service.dto.StockRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockRequest} and its DTO {@link StockRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockRequestMapper extends EntityMapper<StockRequestDTO, StockRequest> {
    @Mapping(target = "rawMaterial", source = "rawMaterial", qualifiedByName = "rawMaterialId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productsId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productsId")
    @Mapping(target = "productionLine", source = "productionLine", qualifiedByName = "productionLineId")
    @Mapping(target = "projects", source = "projects", qualifiedByName = "projectsId")
    StockRequestDTO toDto(StockRequest s);

    @Named("rawMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RawMaterialDTO toDtoRawMaterialId(RawMaterial rawMaterial);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);

    @Named("productionLineId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductionLineDTO toDtoProductionLineId(ProductionLine productionLine);

    @Named("projectsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectsDTO toDtoProjectsId(Projects projects);
}
