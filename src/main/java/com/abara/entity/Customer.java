package com.abara.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(
            name = "customer_seq",
            sequenceName = "customer_sequence",
            allocationSize = 20
    )
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private CustomerImage image;

    private String createdBy;

    private String modifiedBy;

    public Customer() {
    }

    public Customer(@NotNull String name, @NotNull String surname, CustomerImage image) {
        this.name = name;
        this.surname = surname;
        this.image = image;
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

    public CustomerImage getImage() {
        return image;
    }

    public void setImage(CustomerImage image) {
        this.image = image;
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
