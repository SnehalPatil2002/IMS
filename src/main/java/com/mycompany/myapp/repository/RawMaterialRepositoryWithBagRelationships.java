package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RawMaterial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface RawMaterialRepositoryWithBagRelationships {
    Optional<RawMaterial> fetchBagRelationships(Optional<RawMaterial> rawMaterial);

    List<RawMaterial> fetchBagRelationships(List<RawMaterial> rawMaterials);

    Page<RawMaterial> fetchBagRelationships(Page<RawMaterial> rawMaterials);
}
