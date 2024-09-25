package com.logic.jobportal.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "registration_date")
    private Date registrationDate;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "user_type_id", referencedColumnName = "user_type_id")
    private UsersType userType;


    public Users() {
    }

    public Users(String email, String password, boolean isActive, Date registrationDate) {
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.registrationDate = registrationDate;
    }


    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UsersType getUserType() {
        return userType;
    }

    public void setUserType(UsersType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "Users{" +
                "registrationDate=" + registrationDate +
                ", isActive=" + isActive +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                '}';
    }
}
