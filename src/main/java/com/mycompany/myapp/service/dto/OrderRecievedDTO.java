package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.OrderRecieved} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderRecievedDTO implements Serializable {

    private Long id;

    private Instant orDate;

    private Long qtyOrdered;

    private Long qtyRecieved;

    private Instant manufacturingDate;

    private Instant expiryDate;

    private Long qtyApproved;

    private Long qtyRejected;

    private PurchaseQuotationDTO purchaseQuotation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getOrDate() {
        return orDate;
    }

    public void setOrDate(Instant orDate) {
        this.orDate = orDate;
    }

    public Long getQtyOrdered() {
        return qtyOrdered;
    }

    public void setQtyOrdered(Long qtyOrdered) {
        this.qtyOrdered = qtyOrdered;
    }

    public Long getQtyRecieved() {
        return qtyRecieved;
    }

    public void setQtyRecieved(Long qtyRecieved) {
        this.qtyRecieved = qtyRecieved;
    }

    public Instant getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(Instant manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getQtyApproved() {
        return qtyApproved;
    }

    public void setQtyApproved(Long qtyApproved) {
        this.qtyApproved = qtyApproved;
    }

    public Long getQtyRejected() {
        return qtyRejected;
    }

    public void setQtyRejected(Long qtyRejected) {
        this.qtyRejected = qtyRejected;
    }

    public PurchaseQuotationDTO getPurchaseQuotation() {
        return purchaseQuotation;
    }

    public void setPurchaseQuotation(PurchaseQuotationDTO purchaseQuotation) {
        this.purchaseQuotation = purchaseQuotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderRecievedDTO)) {
            return false;
        }

        OrderRecievedDTO orderRecievedDTO = (OrderRecievedDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderRecievedDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderRecievedDTO{" +
            "id=" + getId() +
            ", orDate='" + getOrDate() + "'" +
            ", qtyOrdered=" + getQtyOrdered() +
            ", qtyRecieved=" + getQtyRecieved() +
            ", manufacturingDate='" + getManufacturingDate() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", qtyApproved=" + getQtyApproved() +
            ", qtyRejected=" + getQtyRejected() +
            ", purchaseQuotation=" + getPurchaseQuotation() +
            "}";
    }
}
