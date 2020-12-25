package com.etu.audiencesbooking.audiences;

import java.util.Comparator;

public class AudienceComparator implements Comparator<Audience> {
    @Override
    public int compare(Audience o1, Audience o2) {
        return o1.getNumber().compareTo(o2.getNumber());
    }
}
