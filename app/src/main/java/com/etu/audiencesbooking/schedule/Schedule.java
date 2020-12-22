package com.etu.audiencesbooking.schedule;

import java.io.Serializable;

public class Schedule implements Serializable {

    private String mUUID;
    private String mAudienceID;
    private String mTeacherID;
    private String mDayOfWeek;
    private String mStartTime;
    private String mEndTime;

    public Schedule() {
    }

    public Schedule(String UUID, String audienceID, String teacherID,
                    String dayOfWeek, String startTime, String endTime) {
        mUUID = UUID;
        mAudienceID = audienceID;
        mTeacherID = teacherID;
        mDayOfWeek = dayOfWeek;
        mStartTime = startTime;
        mEndTime = endTime;
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

    public String getDayOfWeek() {
        return mDayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }
}
