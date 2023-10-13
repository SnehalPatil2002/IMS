package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.ClientType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Clients} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ClientsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /clients?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientsCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ClientType
     */
    public static class ClientTypeFilter extends Filter<ClientType> {

        public ClientTypeFilter() {}

        public ClientTypeFilter(ClientTypeFilter filter) {
            super(filter);
        }

        @Override
        public ClientTypeFilter copy() {
            return new ClientTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sname;

    private StringFilter semail;

    private LongFilter mobileNo;

    private StringFilter companyName;

    private LongFilter companyContactNo;

    private StringFilter address;

    private StringFilter pinCode;

    private StringFilter city;

    private ClientTypeFilter clientType;

    private LongFilter salesOrderdId;

    private Boolean distinct;

    public ClientsCriteria() {}

    public ClientsCriteria(ClientsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.sname = other.sname == null ? null : other.sname.copy();
        this.semail = other.semail == null ? null : other.semail.copy();
        this.mobileNo = other.mobileNo == null ? null : other.mobileNo.copy();
        this.companyName = other.companyName == null ? null : other.companyName.copy();
        this.companyContactNo = other.companyContactNo == null ? null : other.companyContactNo.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.pinCode = other.pinCode == null ? null : other.pinCode.copy();
        this.city = other.city == null ? null : other.city.copy();
        this.clientType = other.clientType == null ? null : other.clientType.copy();
        this.salesOrderdId = other.salesOrderdId == null ? null : other.salesOrderdId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClientsCriteria copy() {
        return new ClientsCriteria(this);
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

    public StringFilter getSname() {
        return sname;
    }

    public StringFilter sname() {
        if (sname == null) {
            sname = new StringFilter();
        }
        return sname;
    }

    public void setSname(StringFilter sname) {
        this.sname = sname;
    }

    public StringFilter getSemail() {
        return semail;
    }

    public StringFilter semail() {
        if (semail == null) {
            semail = new StringFilter();
        }
        return semail;
    }

    public void setSemail(StringFilter semail) {
        this.semail = semail;
    }

    public LongFilter getMobileNo() {
        return mobileNo;
    }

    public LongFilter mobileNo() {
        if (mobileNo == null) {
            mobileNo = new LongFilter();
        }
        return mobileNo;
    }

    public void setMobileNo(LongFilter mobileNo) {
        this.mobileNo = mobileNo;
    }

    public StringFilter getCompanyName() {
        return companyName;
    }

    public StringFilter companyName() {
        if (companyName == null) {
            companyName = new StringFilter();
        }
        return companyName;
    }

    public void setCompanyName(StringFilter companyName) {
        this.companyName = companyName;
    }

    public LongFilter getCompanyContactNo() {
        return companyContactNo;
    }

    public LongFilter companyContactNo() {
        if (companyContactNo == null) {
            companyContactNo = new LongFilter();
        }
        return companyContactNo;
    }

    public void setCompanyContactNo(LongFilter companyContactNo) {
        this.companyContactNo = companyContactNo;
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

    public StringFilter getPinCode() {
        return pinCode;
    }

    public StringFilter pinCode() {
        if (pinCode == null) {
            pinCode = new StringFilter();
        }
        return pinCode;
    }

    public void setPinCode(StringFilter pinCode) {
        this.pinCode = pinCode;
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

    public ClientTypeFilter getClientType() {
        return clientType;
    }

    public ClientTypeFilter clientType() {
        if (clientType == null) {
            clientType = new ClientTypeFilter();
        }
        return clientType;
    }

    public void setClientType(ClientTypeFilter clientType) {
        this.clientType = clientType;
    }

    public LongFilter getSalesOrderdId() {
        return salesOrderdId;
    }

    public LongFilter salesOrderdId() {
        if (salesOrderdId == null) {
            salesOrderdId = new LongFilter();
        }
        return salesOrderdId;
    }

    public void setSalesOrderdId(LongFilter salesOrderdId) {
        this.salesOrderdId = salesOrderdId;
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
        final ClientsCriteria that = (ClientsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sname, that.sname) &&
            Objects.equals(semail, that.semail) &&
            Objects.equals(mobileNo, that.mobileNo) &&
            Objects.equals(companyName, that.companyName) &&
            Objects.equals(companyContactNo, that.companyContactNo) &&
            Objects.equals(address, that.address) &&
            Objects.equals(pinCode, that.pinCode) &&
            Objects.equals(city, that.city) &&
            Objects.equals(clientType, that.clientType) &&
            Objects.equals(salesOrderdId, that.salesOrderdId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            sname,
            semail,
            mobileNo,
            companyName,
            companyContactNo,
            address,
            pinCode,
            city,
            clientType,
            salesOrderdId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (sname != null ? "sname=" + sname + ", " : "") +
            (semail != null ? "semail=" + semail + ", " : "") +
            (mobileNo != null ? "mobileNo=" + mobileNo + ", " : "") +
            (companyName != null ? "companyName=" + companyName + ", " : "") +
            (companyContactNo != null ? "companyContactNo=" + companyContactNo + ", " : "") +
            (address != null ? "address=" + address + ", " : "") +
            (pinCode != null ? "pinCode=" + pinCode + ", " : "") +
            (city != null ? "city=" + city + ", " : "") +
            (clientType != null ? "clientType=" + clientType + ", " : "") +
            (salesOrderdId != null ? "salesOrderdId=" + salesOrderdId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
