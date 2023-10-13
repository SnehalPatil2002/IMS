package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Warehouse} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.WarehouseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /warehouses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WarehouseCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter whName;

    private StringFilter address;

    private LongFilter pincode;

    private StringFilter city;

    private StringFilter managerName;

    private StringFilter managerEmail;

    private LongFilter rawMaterialId;

    private Boolean distinct;

    public WarehouseCriteria() {}

    public WarehouseCriteria(WarehouseCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.whName = other.whName == null ? null : other.whName.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.pincode = other.pincode == null ? null : other.pincode.copy();
        this.city = other.city == null ? null : other.city.copy();
        this.managerName = other.managerName == null ? null : other.managerName.copy();
        this.managerEmail = other.managerEmail == null ? null : other.managerEmail.copy();
        this.rawMaterialId = other.rawMaterialId == null ? null : other.rawMaterialId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WarehouseCriteria copy() {
        return new WarehouseCriteria(this);
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

    public StringFilter getWhName() {
        return whName;
    }

    public StringFilter whName() {
        if (whName == null) {
            whName = new StringFilter();
        }
        return whName;
    }

    public void setWhName(StringFilter whName) {
        this.whName = whName;
    }

    public StringFilter getAddress() {
        return address;
    }

    public StringFilter address() {
        if (address == null) {
            address = new StringFilter();
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public LongFilter getPincode() {
        return pincode;
    }

    public LongFilter pincode() {
        if (pincode == null) {
            pincode = new LongFilter();
        }
        return pincode;
    }

    public void setPincode(LongFilter pincode) {
        this.pincode = pincode;
    }

    public StringFilter getCity() {
        return city;
    }

    public StringFilter city() {
        if (city == null) {
            city = new StringFilter();
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getManagerName() {
        return managerName;
    }

    public StringFilter managerName() {
        if (managerName == null) {
            managerName = new StringFilter();
        }
        return managerName;
    }

    public void setManagerName(StringFilter managerName) {
        this.managerName = managerName;
    }

    public StringFilter getManagerEmail() {
        return managerEmail;
    }

    public StringFilter managerEmail() {
        if (managerEmail == null) {
            managerEmail = new StringFilter();
        }
        return managerEmail;
    }

    public void setManagerEmail(StringFilter managerEmail) {
        this.managerEmail = managerEmail;
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
        final WarehouseCriteria that = (WarehouseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(whName, that.whName) &&
            Objects.equals(address, that.address) &&
            Objects.equals(pincode, that.pincode) &&
            Objects.equals(city, that.city) &&
            Objects.equals(managerName, that.managerName) &&
            Objects.equals(managerEmail, that.managerEmail) &&
            Objects.equals(rawMaterialId, that.rawMaterialId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, whName, address, pincode, city, managerName, managerEmail, rawMaterialId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WarehouseCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (whName != null ? "whName=" + whName + ", " : "") +
            (address != null ? "address=" + address + ", " : "") +
            (pincode != null ? "pincode=" + pincode + ", " : "") +
            (city != null ? "city=" + city + ", " : "") +
            (managerName != null ? "managerName=" + managerName + ", " : "") +
            (managerEmail != null ? "managerEmail=" + managerEmail + ", " : "") +
            (rawMaterialId != null ? "rawMaterialId=" + rawMaterialId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
