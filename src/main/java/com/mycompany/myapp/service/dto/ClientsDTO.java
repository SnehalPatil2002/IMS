package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.ClientType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Clients} entity.
 */
@Schema(description = "not an ignored comment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClientsDTO implements Serializable {

    private Long id;

    private String sname;

    private String semail;

    private Long mobileNo;

    private String companyName;

    private Long companyContactNo;

    private String address;

    private String pinCode;

    private String city;

    private ClientType clientType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSemail() {
        return semail;
    }

    public void setSemail(String semail) {
        this.semail = semail;
    }

    public Long getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(Long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getCompanyContactNo() {
        return companyContactNo;
    }

    public void setCompanyContactNo(Long companyContactNo) {
        this.companyContactNo = companyContactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientsDTO)) {
            return false;
        }

        ClientsDTO clientsDTO = (ClientsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, clientsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientsDTO{" +
            "id=" + getId() +
            ", sname='" + getSname() + "'" +
            ", semail='" + getSemail() + "'" +
            ", mobileNo=" + getMobileNo() +
            ", companyName='" + getCompanyName() + "'" +
            ", companyContactNo=" + getCompanyContactNo() +
            ", address='" + getAddress() + "'" +
            ", pinCode='" + getPinCode() + "'" +
            ", city='" + getCity() + "'" +
            ", clientType='" + getClientType() + "'" +
            "}";
    }
}
