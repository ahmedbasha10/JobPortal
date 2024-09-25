package com.logic.jobportal.entity;

import jakarta.persistence.*;

import java.util.List;

public class UsersType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_type_id")
    private int id;

    @Column(name = "user_type_name")
    private String userTypeName;

    @OneToMany(targetEntity = Users.class, mappedBy = "userType", cascade = CascadeType.ALL)
    private List<Users> users;

    public UsersType() {
    }

    public UsersType(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "UsersType{" +
                "id=" + id +
                ", userTypeName='" + userTypeName + '\'' +
                '}';
    }
}
