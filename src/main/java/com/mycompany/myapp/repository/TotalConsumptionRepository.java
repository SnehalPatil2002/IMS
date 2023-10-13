package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TotalConsumption;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TotalConsumption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TotalConsumptionRepository extends JpaRepository<TotalConsumption, Long>, JpaSpecificationExecutor<TotalConsumption> {}
