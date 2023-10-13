package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RawMaterialConsumption;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RawMaterialConsumption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RawMaterialConsumptionRepository
    extends JpaRepository<RawMaterialConsumption, Long>, JpaSpecificationExecutor<RawMaterialConsumption> {}
