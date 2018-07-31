package com.abara.model;

import com.abara.entity.Customer;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.net.URI;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class CustomerDetails {

    private Long id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private URI imageURI;

    private String createdBy;
    private String modifiedBy;

    CustomerDetails() {
    }

    private CustomerDetails(Long id, String username, String name, String surname, String email, URI imageURI, String createdBy, String modifiedBy) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.imageURI = imageURI;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    public static CustomerDetails fromCustomer(Customer customer, URI imageURI) {
        CustomerDetails customerDetails = new CustomerDetails(customer.getId(), customer.getUsername(), customer.getName(), customer.getSurname(), customer.getEmail(), null, customer.getCreatedBy(), customer.getModifiedBy());
        if (isNotBlank(customer.getImageUUID())) {
            customerDetails.setImageURI(imageURI);
        }
        return customerDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URI getImageURI() {
        return imageURI;
    }

    public void setImageURI(URI imageURI) {
        this.imageURI = imageURI;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public boolean equals(Object that) {
        if (getClass() != that.getClass()) return false;
        return EqualsBuilder.reflectionEquals(this, that, "createdBy", "modifiedBy");
    }
}
