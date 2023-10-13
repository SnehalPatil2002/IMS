package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Products;
import com.mycompany.myapp.service.dto.ProductsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Products} and its DTO {@link ProductsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductsMapper extends EntityMapper<ProductsDTO, Products> {}
