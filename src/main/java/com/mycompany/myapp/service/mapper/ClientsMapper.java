package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Clients;
import com.mycompany.myapp.service.dto.ClientsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Clients} and its DTO {@link ClientsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ClientsMapper extends EntityMapper<ClientsDTO, Clients> {}
