package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ProductConsumption;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.Projects;
import com.mycompany.myapp.service.dto.ProductConsumptionDTO;
import com.mycompany.myapp.service.dto.ProductsDTO;
import com.mycompany.myapp.service.dto.ProjectsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductConsumption} and its DTO {@link ProductConsumptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductConsumptionMapper extends EntityMapper<ProductConsumptionDTO, ProductConsumption> {
    @Mapping(target = "projects", source = "projects", qualifiedByName = "projectsId")
    @Mapping(target = "product", source = "product", qualifiedByName = "productsId")
    @Mapping(target = "products", source = "products", qualifiedByName = "productsProductName")
    ProductConsumptionDTO toDto(ProductConsumption s);

    @Named("projectsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectsDTO toDtoProjectsId(Projects projects);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);

    @Named("productsProductName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "productName", source = "productName")
    ProductsDTO toDtoProductsProductName(Products products);
}
