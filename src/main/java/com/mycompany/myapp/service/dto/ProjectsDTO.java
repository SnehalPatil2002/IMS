package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Projects} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectsDTO implements Serializable {

    private Long id;

    private String projectName;

    private Instant startDate;

    private Instant endDate;

    private Long orderQuantity;

    private Double esimatedBudget;

    private Double finalTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Long getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Long orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Double getEsimatedBudget() {
        return esimatedBudget;
    }

    public void setEsimatedBudget(Double esimatedBudget) {
        this.esimatedBudget = esimatedBudget;
    }

    public Double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(Double finalTotal) {
        this.finalTotal = finalTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectsDTO)) {
            return false;
        }

        ProjectsDTO projectsDTO = (ProjectsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectsDTO{" +
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
