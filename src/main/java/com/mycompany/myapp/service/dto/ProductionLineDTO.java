package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ProductionLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductionLineDTO implements Serializable {

    private Long id;

    private String description;

    private Boolean isActive;

    private ProductsDTO product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ProductsDTO getProduct() {
        return product;
    }

    public void setProduct(ProductsDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductionLineDTO)) {
            return false;
        }

        ProductionLineDTO productionLineDTO = (ProductionLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productionLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductionLineDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
