package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.OrderRecieved;
import com.mycompany.myapp.domain.PurchaseQuotation;
import com.mycompany.myapp.service.dto.OrderRecievedDTO;
import com.mycompany.myapp.service.dto.PurchaseQuotationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderRecieved} and its DTO {@link OrderRecievedDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderRecievedMapper extends EntityMapper<OrderRecievedDTO, OrderRecieved> {
    @Mapping(target = "purchaseQuotation", source = "purchaseQuotation", qualifiedByName = "purchaseQuotationId")
    OrderRecievedDTO toDto(OrderRecieved s);

    @Named("purchaseQuotationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PurchaseQuotationDTO toDtoPurchaseQuotationId(PurchaseQuotation purchaseQuotation);
}
