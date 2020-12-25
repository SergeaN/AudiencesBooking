package com.etu.audiencesbooking.teachers;

import java.util.Comparator;

public class TeacherComparator implements Comparator<Teacher> {
    @Override
    public int compare(Teacher o1, Teacher o2) {
        return o1.getFirstName().compareTo(o2.getFirstName());
    }
}
