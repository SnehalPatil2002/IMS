package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.Unit;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.RawMaterial} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.RawMaterialResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /raw-materials?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RawMaterialCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Unit
     */
    public static class UnitFilter extends Filter<Unit> {

        public UnitFilter() {}

        public UnitFilter(UnitFilter filter) {
            super(filter);
        }

        @Override
        public UnitFilter copy() {
            return new UnitFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter barcode;

    private LongFilter quantity;

    private DoubleFilter unitPrice;

    private UnitFilter unitMeasure;

    private DoubleFilter gstPercentage;

    private LongFilter reorderPoint;

    private LongFilter productsId;

    private LongFilter warehouseId;

    private Boolean distinct;

    public RawMaterialCriteria() {}

    public RawMaterialCriteria(RawMaterialCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.barcode = other.barcode == null ? null : other.barcode.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.unitPrice = other.unitPrice == null ? null : other.unitPrice.copy();
        this.unitMeasure = other.unitMeasure == null ? null : other.unitMeasure.copy();
        this.gstPercentage = other.gstPercentage == null ? null : other.gstPercentage.copy();
        this.reorderPoint = other.reorderPoint == null ? null : other.reorderPoint.copy();
        this.productsId = other.productsId == null ? null : other.productsId.copy();
        this.warehouseId = other.warehouseId == null ? null : other.warehouseId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RawMaterialCriteria copy() {
        return new RawMaterialCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getBarcode() {
        return barcode;
    }

    public StringFilter barcode() {
        if (barcode == null) {
            barcode = new StringFilter();
        }
        return barcode;
    }

    public void setBarcode(StringFilter barcode) {
        this.barcode = barcode;
    }

    public LongFilter getQuantity() {
        return quantity;
    }

    public LongFilter quantity() {
        if (quantity == null) {
            quantity = new LongFilter();
        }
        return quantity;
    }

    public void setQuantity(LongFilter quantity) {
        this.quantity = quantity;
    }

    public DoubleFilter getUnitPrice() {
        return unitPrice;
    }

    public DoubleFilter unitPrice() {
        if (unitPrice == null) {
            unitPrice = new DoubleFilter();
        }
        return unitPrice;
    }

    public void setUnitPrice(DoubleFilter unitPrice) {
        this.unitPrice = unitPrice;
    }

    public UnitFilter getUnitMeasure() {
        return unitMeasure;
    }

    public UnitFilter unitMeasure() {
        if (unitMeasure == null) {
            unitMeasure = new UnitFilter();
        }
        return unitMeasure;
    }

    public void setUnitMeasure(UnitFilter unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public DoubleFilter getGstPercentage() {
        return gstPercentage;
    }

    public DoubleFilter gstPercentage() {
        if (gstPercentage == null) {
            gstPercentage = new DoubleFilter();
        }
        return gstPercentage;
    }

    public void setGstPercentage(DoubleFilter gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public LongFilter getReorderPoint() {
        return reorderPoint;
    }

    public LongFilter reorderPoint() {
        if (reorderPoint == null) {
            reorderPoint = new LongFilter();
        }
        return reorderPoint;
    }

    public void setReorderPoint(LongFilter reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    public LongFilter getProductsId() {
        return productsId;
    }

    public LongFilter productsId() {
        if (productsId == null) {
            productsId = new LongFilter();
        }
        return productsId;
    }

    public void setProductsId(LongFilter productsId) {
        this.productsId = productsId;
    }

    public LongFilter getWarehouseId() {
        return warehouseId;
    }

    public LongFilter warehouseId() {
        if (warehouseId == null) {
            warehouseId = new LongFilter();
        }
        return warehouseId;
    }

    public void setWarehouseId(LongFilter warehouseId) {
        this.warehouseId = warehouseId;
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
        final RawMaterialCriteria that = (RawMaterialCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(barcode, that.barcode) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(unitPrice, that.unitPrice) &&
            Objects.equals(unitMeasure, that.unitMeasure) &&
            Objects.equals(gstPercentage, that.gstPercentage) &&
            Objects.equals(reorderPoint, that.reorderPoint) &&
            Objects.equals(productsId, that.productsId) &&
            Objects.equals(warehouseId, that.warehouseId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            barcode,
            quantity,
            unitPrice,
            unitMeasure,
            gstPercentage,
            reorderPoint,
            productsId,
            warehouseId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RawMaterialCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (barcode != null ? "barcode=" + barcode + ", " : "") +
            (quantity != null ? "quantity=" + quantity + ", " : "") +
            (unitPrice != null ? "unitPrice=" + unitPrice + ", " : "") +
            (unitMeasure != null ? "unitMeasure=" + unitMeasure + ", " : "") +
            (gstPercentage != null ? "gstPercentage=" + gstPercentage + ", " : "") +
            (reorderPoint != null ? "reorderPoint=" + reorderPoint + ", " : "") +
            (productsId != null ? "productsId=" + productsId + ", " : "") +
            (warehouseId != null ? "warehouseId=" + warehouseId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
