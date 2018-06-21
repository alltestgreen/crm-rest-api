package com.abara.model;

public class CustomerDetails {

    private Long id;

    private String name;

    private String surname;

    private String imageURL;

    private String createdBy;

    private String modifiedBy;

    CustomerDetails() {
    }

    public CustomerDetails(Long id, String name, String surname, String imageURL, String createdBy, String modifiedBy) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.imageURL = imageURL;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
