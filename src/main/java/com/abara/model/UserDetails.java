package com.abara.model;

import com.abara.entity.Role;
import com.abara.entity.User;

import java.util.Set;

public class UserDetails {

    private Long id;
    private String username;
    private Set<Role> roles;

    UserDetails() {
    }

    private UserDetails(Long id, String username, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public static UserDetails fromUser(User user) {
        return new UserDetails(user.getId(), user.getUsername(), user.getRoles());
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
