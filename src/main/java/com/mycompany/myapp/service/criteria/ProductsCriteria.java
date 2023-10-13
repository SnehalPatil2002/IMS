package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Products} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ProductsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter productName;

    private DoubleFilter availableQty;

    private DoubleFilter manufacturingCost;

    private DoubleFilter labourCost;

    private LongFilter rawMaterialId;

    private Boolean distinct;

    public ProductsCriteria() {}

    public ProductsCriteria(ProductsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.productName = other.productName == null ? null : other.productName.copy();
        this.availableQty = other.availableQty == null ? null : other.availableQty.copy();
        this.manufacturingCost = other.manufacturingCost == null ? null : other.manufacturingCost.copy();
        this.labourCost = other.labourCost == null ? null : other.labourCost.copy();
        this.rawMaterialId = other.rawMaterialId == null ? null : other.rawMaterialId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductsCriteria copy() {
        return new ProductsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getProductName() {
        return productName;
    }

    public StringFilter productName() {
        if (productName == null) {
            productName = new StringFilter();
        }
        return productName;
    }

    public void setProductName(StringFilter productName) {
        this.productName = productName;
    }

    public DoubleFilter getAvailableQty() {
        return availableQty;
    }

    public DoubleFilter availableQty() {
        if (availableQty == null) {
            availableQty = new DoubleFilter();
        }
        return availableQty;
    }

    public void setAvailableQty(DoubleFilter availableQty) {
        this.availableQty = availableQty;
    }

    public DoubleFilter getManufacturingCost() {
        return manufacturingCost;
    }

    public DoubleFilter manufacturingCost() {
        if (manufacturingCost == null) {
            manufacturingCost = new DoubleFilter();
        }
        return manufacturingCost;
    }

    public void setManufacturingCost(DoubleFilter manufacturingCost) {
        this.manufacturingCost = manufacturingCost;
    }

    public DoubleFilter getLabourCost() {
        return labourCost;
    }

    public DoubleFilter labourCost() {
        if (labourCost == null) {
            labourCost = new DoubleFilter();
        }
        return labourCost;
    }

    public void setLabourCost(DoubleFilter labourCost) {
        this.labourCost = labourCost;
    }

    public LongFilter getRawMaterialId() {
        return rawMaterialId;
    }

    public LongFilter rawMaterialId() {
        if (rawMaterialId == null) {
            rawMaterialId = new LongFilter();
        }
        return rawMaterialId;
    }

    public void setRawMaterialId(LongFilter rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProductsCriteria that = (ProductsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(productName, that.productName) &&
            Objects.equals(availableQty, that.availableQty) &&
            Objects.equals(manufacturingCost, that.manufacturingCost) &&
            Objects.equals(labourCost, that.labourCost) &&
            Objects.equals(rawMaterialId, that.rawMaterialId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productName, availableQty, manufacturingCost, labourCost, rawMaterialId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (productName != null ? "productName=" + productName + ", " : "") +
            (availableQty != null ? "availableQty=" + availableQty + ", " : "") +
            (manufacturingCost != null ? "manufacturingCost=" + manufacturingCost + ", " : "") +
            (labourCost != null ? "labourCost=" + labourCost + ", " : "") +
            (rawMaterialId != null ? "rawMaterialId=" + rawMaterialId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
