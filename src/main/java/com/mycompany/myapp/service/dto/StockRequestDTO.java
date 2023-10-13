package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.Status;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.StockRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockRequestDTO implements Serializable {

    private Long id;

    private Double qtyRequired;

    private Instant reqDate;

    private Boolean isProd;

    private Status status;

    private RawMaterialDTO rawMaterial;

    private ProductsDTO product;

    private ProductsDTO products;

    private ProductionLineDTO productionLine;

    private ProjectsDTO projects;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQtyRequired() {
        return qtyRequired;
    }

    public void setQtyRequired(Double qtyRequired) {
        this.qtyRequired = qtyRequired;
    }

    public Instant getReqDate() {
        return reqDate;
    }

    public void setReqDate(Instant reqDate) {
        this.reqDate = reqDate;
    }

    public Boolean getIsProd() {
        return isProd;
    }

    public void setIsProd(Boolean isProd) {
        this.isProd = isProd;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public RawMaterialDTO getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterialDTO rawMaterial) {
        this.rawMaterial = rawMaterial;
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

    public ProductionLineDTO getProductionLine() {
        return productionLine;
    }

    public void setProductionLine(ProductionLineDTO productionLine) {
        this.productionLine = productionLine;
    }

    public ProjectsDTO getProjects() {
        return projects;
    }

    public void setProjects(ProjectsDTO projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockRequestDTO)) {
            return false;
        }

        StockRequestDTO stockRequestDTO = (StockRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockRequestDTO{" +
            "id=" + getId() +
            ", qtyRequired=" + getQtyRequired() +
            ", reqDate='" + getReqDate() + "'" +
            ", isProd='" + getIsProd() + "'" +
            ", status='" + getStatus() + "'" +
            ", rawMaterial=" + getRawMaterial() +
            ", product=" + getProduct() +
            ", products=" + getProducts() +
            ", productionLine=" + getProductionLine() +
            ", projects=" + getProjects() +
            "}";
    }
}
