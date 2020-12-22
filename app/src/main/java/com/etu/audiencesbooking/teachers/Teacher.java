package com.etu.audiencesbooking.teachers;

import java.io.Serializable;

public class Teacher implements Serializable {

    private String mUUID;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mDepartment;
    private String mPassword;
    private int mType;

    public Teacher() {

    }

    public Teacher(String uuid, String firstName, String lastName, String email,
                   String department, String password, int type) {
        mUUID = uuid;
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mDepartment = department;
        mPassword = password;
        mType = 0;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(String department) {
        this.mDepartment = department;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
