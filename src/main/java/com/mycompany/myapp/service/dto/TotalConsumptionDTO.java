package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TotalConsumption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TotalConsumptionDTO implements Serializable {

    private Long id;

    private Double totalMaterialCost;

    private Double totalProductsCost;

    private Double finalCost;

    private ProjectsDTO projects;

    private ProductsDTO products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalMaterialCost() {
        return totalMaterialCost;
    }

    public void setTotalMaterialCost(Double totalMaterialCost) {
        this.totalMaterialCost = totalMaterialCost;
    }

    public Double getTotalProductsCost() {
        return totalProductsCost;
    }

    public void setTotalProductsCost(Double totalProductsCost) {
        this.totalProductsCost = totalProductsCost;
    }

    public Double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Double finalCost) {
        this.finalCost = finalCost;
    }

    public ProjectsDTO getProjects() {
        return projects;
    }

    public void setProjects(ProjectsDTO projects) {
        this.projects = projects;
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
        if (!(o instanceof TotalConsumptionDTO)) {
            return false;
        }

        TotalConsumptionDTO totalConsumptionDTO = (TotalConsumptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, totalConsumptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TotalConsumptionDTO{" +
            "id=" + getId() +
            ", totalMaterialCost=" + getTotalMaterialCost() +
            ", totalProductsCost=" + getTotalProductsCost() +
            ", finalCost=" + getFinalCost() +
            ", projects=" + getProjects() +
            ", products=" + getProducts() +
            "}";
    }
}
