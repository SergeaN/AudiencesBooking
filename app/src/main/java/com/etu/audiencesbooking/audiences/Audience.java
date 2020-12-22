package com.etu.audiencesbooking.audiences;

import java.io.Serializable;

public class Audience implements Serializable{

    private String mUUID;
    private String mNumber;
    private int mCapacity;

    public Audience() {
    }

    public Audience(String UUID, String number, int capacity) {
        mUUID = UUID;
        mNumber = number;
        mCapacity = capacity;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    public int getCapacity() {
        return mCapacity;
    }

    public void setCapacity(int capacity) {
        mCapacity = capacity;
    }

}
