package com.abara.model;

import com.abara.entity.Customer;

import java.net.URI;

public class CustomerDetails {

    private Long id;

    private String name;

    private String surname;

    private URI imageURI;

    private String createdBy;

    private String modifiedBy;

    CustomerDetails() {
    }

    public CustomerDetails(Long id, String name, String surname, URI imageURI, String createdBy, String modifiedBy) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.imageURI = imageURI;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    public static CustomerDetails fromUser(Customer customer, URI uri) {
        CustomerDetails customerDetails = new CustomerDetails(customer.getId(), customer.getName(), customer.getSurname(), null, customer.getCreatedBy(), customer.getModifiedBy());
        if (customer.getImage() != null) {
            customerDetails.setImageURI(uri);
        }
        return customerDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
