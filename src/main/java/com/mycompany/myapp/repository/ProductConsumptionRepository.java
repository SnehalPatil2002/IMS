package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ProductConsumption;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProductConsumption entity.
 */
@Repository
public interface ProductConsumptionRepository
    extends JpaRepository<ProductConsumption, Long>, JpaSpecificationExecutor<ProductConsumption> {
    default Optional<ProductConsumption> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ProductConsumption> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ProductConsumption> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct productConsumption from ProductConsumption productConsumption left join fetch productConsumption.products",
        countQuery = "select count(distinct productConsumption) from ProductConsumption productConsumption"
    )
    Page<ProductConsumption> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct productConsumption from ProductConsumption productConsumption left join fetch productConsumption.products")
    List<ProductConsumption> findAllWithToOneRelationships();

    @Query(
        "select productConsumption from ProductConsumption productConsumption left join fetch productConsumption.products where productConsumption.id =:id"
    )
    Optional<ProductConsumption> findOneWithToOneRelationships(@Param("id") Long id);
}
