package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RawMaterial;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class RawMaterialRepositoryWithBagRelationshipsImpl implements RawMaterialRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RawMaterial> fetchBagRelationships(Optional<RawMaterial> rawMaterial) {
        return rawMaterial.map(this::fetchProducts);
    }

    @Override
    public Page<RawMaterial> fetchBagRelationships(Page<RawMaterial> rawMaterials) {
        return new PageImpl<>(
            fetchBagRelationships(rawMaterials.getContent()),
            rawMaterials.getPageable(),
            rawMaterials.getTotalElements()
        );
    }

    @Override
    public List<RawMaterial> fetchBagRelationships(List<RawMaterial> rawMaterials) {
        return Optional.of(rawMaterials).map(this::fetchProducts).orElse(Collections.emptyList());
    }

    RawMaterial fetchProducts(RawMaterial result) {
        return entityManager
            .createQuery(
                "select rawMaterial from RawMaterial rawMaterial left join fetch rawMaterial.products where rawMaterial is :rawMaterial",
                RawMaterial.class
            )
            .setParameter("rawMaterial", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<RawMaterial> fetchProducts(List<RawMaterial> rawMaterials) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, rawMaterials.size()).forEach(index -> order.put(rawMaterials.get(index).getId(), index));
        List<RawMaterial> result = entityManager
            .createQuery(
                "select distinct rawMaterial from RawMaterial rawMaterial left join fetch rawMaterial.products where rawMaterial in :rawMaterials",
                RawMaterial.class
            )
            .setParameter("rawMaterials", rawMaterials)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
