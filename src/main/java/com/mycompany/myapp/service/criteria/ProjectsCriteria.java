package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Projects} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ProjectsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /projects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter projectName;

    private InstantFilter startDate;

    private InstantFilter endDate;

    private LongFilter orderQuantity;

    private DoubleFilter esimatedBudget;

    private DoubleFilter finalTotal;

    private Boolean distinct;

    public ProjectsCriteria() {}

    public ProjectsCriteria(ProjectsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.projectName = other.projectName == null ? null : other.projectName.copy();
        this.startDate = other.startDate == null ? null : other.startDate.copy();
        this.endDate = other.endDate == null ? null : other.endDate.copy();
        this.orderQuantity = other.orderQuantity == null ? null : other.orderQuantity.copy();
        this.esimatedBudget = other.esimatedBudget == null ? null : other.esimatedBudget.copy();
        this.finalTotal = other.finalTotal == null ? null : other.finalTotal.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectsCriteria copy() {
        return new ProjectsCriteria(this);
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

    public StringFilter getProjectName() {
        return projectName;
    }

    public StringFilter projectName() {
        if (projectName == null) {
            projectName = new StringFilter();
        }
        return projectName;
    }

    public void setProjectName(StringFilter projectName) {
        this.projectName = projectName;
    }

    public InstantFilter getStartDate() {
        return startDate;
    }

    public InstantFilter startDate() {
        if (startDate == null) {
            startDate = new InstantFilter();
        }
        return startDate;
    }

    public void setStartDate(InstantFilter startDate) {
        this.startDate = startDate;
    }

    public InstantFilter getEndDate() {
        return endDate;
    }

    public InstantFilter endDate() {
        if (endDate == null) {
            endDate = new InstantFilter();
        }
        return endDate;
    }

    public void setEndDate(InstantFilter endDate) {
        this.endDate = endDate;
    }

    public LongFilter getOrderQuantity() {
        return orderQuantity;
    }

    public LongFilter orderQuantity() {
        if (orderQuantity == null) {
            orderQuantity = new LongFilter();
        }
        return orderQuantity;
    }

    public void setOrderQuantity(LongFilter orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public DoubleFilter getEsimatedBudget() {
        return esimatedBudget;
    }

    public DoubleFilter esimatedBudget() {
        if (esimatedBudget == null) {
            esimatedBudget = new DoubleFilter();
        }
        return esimatedBudget;
    }

    public void setEsimatedBudget(DoubleFilter esimatedBudget) {
        this.esimatedBudget = esimatedBudget;
    }

    public DoubleFilter getFinalTotal() {
        return finalTotal;
    }

    public DoubleFilter finalTotal() {
        if (finalTotal == null) {
            finalTotal = new DoubleFilter();
        }
        return finalTotal;
    }

    public void setFinalTotal(DoubleFilter finalTotal) {
        this.finalTotal = finalTotal;
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
        final ProjectsCriteria that = (ProjectsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(projectName, that.projectName) &&
            Objects.equals(startDate, that.startDate) &&
            Objects.equals(endDate, that.endDate) &&
            Objects.equals(orderQuantity, that.orderQuantity) &&
            Objects.equals(esimatedBudget, that.esimatedBudget) &&
            Objects.equals(finalTotal, that.finalTotal) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectName, startDate, endDate, orderQuantity, esimatedBudget, finalTotal, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (projectName != null ? "projectName=" + projectName + ", " : "") +
            (startDate != null ? "startDate=" + startDate + ", " : "") +
            (endDate != null ? "endDate=" + endDate + ", " : "") +
            (orderQuantity != null ? "orderQuantity=" + orderQuantity + ", " : "") +
            (esimatedBudget != null ? "esimatedBudget=" + esimatedBudget + ", " : "") +
            (finalTotal != null ? "finalTotal=" + finalTotal + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
