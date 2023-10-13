package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.domain.RawMaterial;
import com.mycompany.myapp.domain.Warehouse;
import com.mycompany.myapp.service.dto.ProductsDTO;
import com.mycompany.myapp.service.dto.RawMaterialDTO;
import com.mycompany.myapp.service.dto.WarehouseDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RawMaterial} and its DTO {@link RawMaterialDTO}.
 */
@Mapper(componentModel = "spring")
public interface RawMaterialMapper extends EntityMapper<RawMaterialDTO, RawMaterial> {
    @Mapping(target = "products", source = "products", qualifiedByName = "productsIdSet")
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseId")
    RawMaterialDTO toDto(RawMaterial s);

    @Mapping(target = "removeProducts", ignore = true)
    RawMaterial toEntity(RawMaterialDTO rawMaterialDTO);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);

    @Named("productsIdSet")
    default Set<ProductsDTO> toDtoProductsIdSet(Set<Products> products) {
        return products.stream().map(this::toDtoProductsId).collect(Collectors.toSet());
    }

    @Named("warehouseId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    WarehouseDTO toDtoWarehouseId(Warehouse warehouse);
}
