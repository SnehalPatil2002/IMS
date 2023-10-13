package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ProductConsumption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductConsumptionDTO implements Serializable {

    private Long id;

    private Double quantityConsumed;

    private Double totalProductCost;

    private ProjectsDTO projects;

    private ProductsDTO product;

    private ProductsDTO products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantityConsumed() {
        return quantityConsumed;
    }

    public void setQuantityConsumed(Double quantityConsumed) {
        this.quantityConsumed = quantityConsumed;
    }

    public Double getTotalProductCost() {
        return totalProductCost;
    }

    public void setTotalProductCost(Double totalProductCost) {
        this.totalProductCost = totalProductCost;
    }

    public ProjectsDTO getProjects() {
        return projects;
    }

    public void setProjects(ProjectsDTO projects) {
        this.projects = projects;
    }

    public ProductsDTO getProduct() {
        return product;
    }

    public void setProduct(ProductsDTO product) {
        this.product = product;
    }

    public ProductsDTO getProducts() {
        return products;
    }

    public void setProducts(ProductsDTO products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductConsumptionDTO)) {
            return false;
        }

        ProductConsumptionDTO productConsumptionDTO = (ProductConsumptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productConsumptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductConsumptionDTO{" +
            "id=" + getId() +
            ", quantityConsumed=" + getQuantityConsumed() +
            ", totalProductCost=" + getTotalProductCost() +
            ", projects=" + getProjects() +
            ", product=" + getProduct() +
            ", products=" + getProducts() +
            "}";
    }
}
