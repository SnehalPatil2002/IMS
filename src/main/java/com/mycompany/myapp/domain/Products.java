package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Products.
 */
@Entity
@Table(name = "products")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "available_qty")
    private Double availableQty;

    @Column(name = "manufacturing_cost")
    private Double manufacturingCost;

    @Column(name = "labour_cost")
    private Double labourCost;

    @ManyToMany(mappedBy = "products")
    @JsonIgnoreProperties(value = { "products", "warehouse" }, allowSetters = true)
    private Set<RawMaterial> rawMaterials = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Products id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return this.productName;
    }

    public Products productName(String productName) {
        this.setProductName(productName);
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getAvailableQty() {
        return this.availableQty;
    }

    public Products availableQty(Double availableQty) {
        this.setAvailableQty(availableQty);
        return this;
    }

    public void setAvailableQty(Double availableQty) {
        this.availableQty = availableQty;
    }

    public Double getManufacturingCost() {
        return this.manufacturingCost;
    }

    public Products manufacturingCost(Double manufacturingCost) {
        this.setManufacturingCost(manufacturingCost);
        return this;
    }

    public void setManufacturingCost(Double manufacturingCost) {
        this.manufacturingCost = manufacturingCost;
    }

    public Double getLabourCost() {
        return this.labourCost;
    }

    public Products labourCost(Double labourCost) {
        this.setLabourCost(labourCost);
        return this;
    }

    public void setLabourCost(Double labourCost) {
        this.labourCost = labourCost;
    }

    public Set<RawMaterial> getRawMaterials() {
        return this.rawMaterials;
    }

    public void setRawMaterials(Set<RawMaterial> rawMaterials) {
        if (this.rawMaterials != null) {
            this.rawMaterials.forEach(i -> i.removeProducts(this));
        }
        if (rawMaterials != null) {
            rawMaterials.forEach(i -> i.addProducts(this));
        }
        this.rawMaterials = rawMaterials;
    }

    public Products rawMaterials(Set<RawMaterial> rawMaterials) {
        this.setRawMaterials(rawMaterials);
        return this;
    }

    public Products addRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterials.add(rawMaterial);
        rawMaterial.getProducts().add(this);
        return this;
    }

    public Products removeRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterials.remove(rawMaterial);
        rawMaterial.getProducts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", productName='" + getProductName() + "'" +
            ", availableQty=" + getAvailableQty() +
            ", manufacturingCost=" + getManufacturingCost() +
            ", labourCost=" + getLabourCost() +
            "}";
    }
}
