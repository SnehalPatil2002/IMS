package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A Projects.
 */
@Entity
@Table(name = "projects")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Projects implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "order_quantity")
    private Long orderQuantity;

    @Column(name = "esimated_budget")
    private Double esimatedBudget;

    @Column(name = "final_total")
    private Double finalTotal;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Projects id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public Projects projectName(String projectName) {
        this.setProjectName(projectName);
        return this;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Projects startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Projects endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Long getOrderQuantity() {
        return this.orderQuantity;
    }

    public Projects orderQuantity(Long orderQuantity) {
        this.setOrderQuantity(orderQuantity);
        return this;
    }

    public void setOrderQuantity(Long orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Double getEsimatedBudget() {
        return this.esimatedBudget;
    }

    public Projects esimatedBudget(Double esimatedBudget) {
        this.setEsimatedBudget(esimatedBudget);
        return this;
    }

    public void setEsimatedBudget(Double esimatedBudget) {
        this.esimatedBudget = esimatedBudget;
    }

    public Double getFinalTotal() {
        return this.finalTotal;
    }

    public Projects finalTotal(Double finalTotal) {
        this.setFinalTotal(finalTotal);
        return this;
    }

    public void setFinalTotal(Double finalTotal) {
        this.finalTotal = finalTotal;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Projects)) {
            return false;
        }
        return id != null && id.equals(((Projects) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Projects{" +
            "id=" + getId() +
            ", projectName='" + getProjectName() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", orderQuantity=" + getOrderQuantity() +
            ", esimatedBudget=" + getEsimatedBudget() +
            ", finalTotal=" + getFinalTotal() +
            "}";
    }
}
