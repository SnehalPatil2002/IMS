package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ProductionLine;
import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.service.dto.ProductionLineDTO;
import com.mycompany.myapp.service.dto.ProductsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductionLine} and its DTO {@link ProductionLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductionLineMapper extends EntityMapper<ProductionLineDTO, ProductionLine> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productsId")
    ProductionLineDTO toDto(ProductionLine s);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);
}
