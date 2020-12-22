package com.etu.audiencesbooking.bookings;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Booking implements Serializable {

    private String mUUID;
    private String mAudienceID;
    private String mTeacherID;
    private Date mStartDate;
    private Date mEndDate;

    public Booking() {
        
    }

    public Booking(String UUID, String audienceID, String teacherID,
                   Date startDate, Date endDate) {
        mUUID = UUID;
        mAudienceID = audienceID;
        mTeacherID = teacherID;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }

    public String getAudienceID() {
        return mAudienceID;
    }

    public void setAudienceID(String audienceID) {
        mAudienceID = audienceID;
    }

    public String getTeacherID() {
        return mTeacherID;
    }

    public void setTeacherID(String teacherID) {
        mTeacherID = teacherID;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

}
