package com.etu.audiencesbooking.schedule;

import java.util.Comparator;

public class ScheduleComparator implements Comparator<Schedule> {
    @Override
    public int compare(Schedule o1, Schedule o2) {
        if (o1.getStartTime().equals(o2.getStartTime()))
            return o1.getEndTime().compareTo(o2.getEndTime());
        else
            return o1.getStartTime().compareTo(o2.getEndTime());
    }
}
