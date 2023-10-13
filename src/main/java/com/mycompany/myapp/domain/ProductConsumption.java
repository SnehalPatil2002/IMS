package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A ProductConsumption.
 */
@Entity
@Table(name = "product_consumption")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductConsumption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity_consumed")
    private Double quantityConsumed;

    @Column(name = "total_product_cost")
    private Double totalProductCost;

    @OneToOne
    @JoinColumn(unique = true)
    private Projects projects;

    @JsonIgnoreProperties(value = { "rawMaterials" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Products product;

    @JsonIgnoreProperties(value = { "rawMaterials" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Products products;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductConsumption id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantityConsumed() {
        return this.quantityConsumed;
    }

    public ProductConsumption quantityConsumed(Double quantityConsumed) {
        this.setQuantityConsumed(quantityConsumed);
        return this;
    }

    public void setQuantityConsumed(Double quantityConsumed) {
        this.quantityConsumed = quantityConsumed;
    }

    public Double getTotalProductCost() {
        return this.totalProductCost;
    }

    public ProductConsumption totalProductCost(Double totalProductCost) {
        this.setTotalProductCost(totalProductCost);
        return this;
    }

    public void setTotalProductCost(Double totalProductCost) {
        this.totalProductCost = totalProductCost;
    }

    public Projects getProjects() {
        return this.projects;
    }

    public void setProjects(Projects projects) {
        this.projects = projects;
    }

    public ProductConsumption projects(Projects projects) {
        this.setProjects(projects);
        return this;
    }

    public Products getProduct() {
        return this.product;
    }

    public void setProduct(Products products) {
        this.product = products;
    }

    public ProductConsumption product(Products products) {
        this.setProduct(products);
        return this;
    }

    public Products getProducts() {
        return this.products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public ProductConsumption products(Products products) {
        this.setProducts(products);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductConsumption)) {
            return false;
        }
        return id != null && id.equals(((ProductConsumption) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductConsumption{" +
            "id=" + getId() +
            ", quantityConsumed=" + getQuantityConsumed() +
            ", totalProductCost=" + getTotalProductCost() +
            "}";
    }
}
