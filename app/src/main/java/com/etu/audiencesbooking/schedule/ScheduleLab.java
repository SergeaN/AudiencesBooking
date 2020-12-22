package com.etu.audiencesbooking.schedule;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScheduleLab {
    private static ScheduleLab sScheduleLab;

    private Context mContext;
    private Map<String, Schedule> mSchedule;
    private DatabaseReference mDatabase;

    public static ScheduleLab get(Context context) {
        if (sScheduleLab == null) {
            sScheduleLab = new ScheduleLab(context);
        }
        return sScheduleLab;
    }

    private ScheduleLab(final Context context) {
        mSchedule = new TreeMap<>();
        mContext = context.getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference("Schedule");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mSchedule.size() > 0) mSchedule.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Schedule schedule = dataSnapshot.getValue(Schedule.class);
                    assert schedule != null;
                    mSchedule.put(schedule.getUUID(), schedule);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }

    public void addSchedule(Schedule schedule) {
        mDatabase.child(schedule.getUUID()).setValue(schedule);
    }

    public void removeSchedule(String id) {
        mDatabase.child(id).removeValue();
    }

    public List<Schedule> getFullSchedule() {
        List<Schedule> schedules = new ArrayList<>(mSchedule.values());
        schedules.sort(new ScheduleComparator());
        return schedules;
    }

    public Schedule getSchedule(String id) {
        return mSchedule.get(id);
    }

    public List<Schedule> findByAudience(String id) {
        List<Schedule> result = new ArrayList<>();
        for (Schedule schedule : mSchedule.values()) {
            if (schedule.getAudienceID().equals(id)) result.add(schedule);
        }
        return result;
    }

    public List<Schedule> findByTeacher(String id) {
        List<Schedule> result = new ArrayList<>();
        for (Schedule schedule : mSchedule.values()) {
            if (schedule.getTeacherID().equals(id)) result.add(schedule);
        }
        return result;
    }
}
