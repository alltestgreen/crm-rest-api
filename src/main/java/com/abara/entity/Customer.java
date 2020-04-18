package com.abara.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank
    @Size(max = 256)
    private String username;

    @NotBlank
    @Size(max = 256)
    private String name;

    @NotBlank
    @Size(max = 256)
    private String surname;

    @Column(unique = true)
    @NotBlank
    @Email
    private String email;

    private String imageUUID;

    @Size(max = 256)
    private String createdBy;

    @Size(max = 256)
    private String modifiedBy;

    public Customer() {
    }

    public Customer(String username, String name, String surname, String email, String imageUUID) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.imageUUID = imageUUID;
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

    public String getImageUUID() {
        return imageUUID;
    }

    public void setImageUUID(String imageUUID) {
        this.imageUUID = imageUUID;
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
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object that) {
        if (getClass() != that.getClass()) return false;
        return EqualsBuilder.reflectionEquals(this, that, "id", "createdBy", "modifiedBy");
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
