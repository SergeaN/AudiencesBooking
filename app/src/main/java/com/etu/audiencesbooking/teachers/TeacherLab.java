package com.etu.audiencesbooking.teachers;

import android.content.Context;
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

public class TeacherLab {

    private static TeacherLab sTeacherLab;

    private Context mContext;
    private Map<String, Teacher> mTeachers;
    private DatabaseReference mDatabase;

    public static TeacherLab get(Context context) {
        if (sTeacherLab == null) {
            sTeacherLab = new TeacherLab(context);
        }
        return sTeacherLab;
    }

    private TeacherLab(final Context context) {
        mTeachers = new TreeMap<>();
        mContext = context.getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference("Teachers");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mTeachers.size() > 0) mTeachers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    assert teacher != null;
                    mTeachers.put(teacher.getUUID(), teacher);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }

    public void addTeacher(Teacher teacher) {
        mDatabase.child(teacher.getUUID()).setValue(teacher);
    }

    public void removeTeacher(String id) {
        mDatabase.child(id).removeValue();
    }

    public List<Teacher> getTeachers() {
        List<Teacher> teachers = new ArrayList<>(mTeachers.values());
        teachers.sort(new TeacherComparator());
        return teachers;
    }

    public Teacher getTeacher(String id) {
        return mTeachers.get(id);
    }

    public List<Teacher> findByPattern(String pattern) {
        List<Teacher> result = new ArrayList<>();
        for (Teacher teacher : mTeachers.values()) {
            if (teacher.getFirstName().toLowerCase().contains(pattern)
                    || teacher.getLastName().toLowerCase().contains(pattern)
                    || teacher.getDepartment().toLowerCase().contains(pattern)
                    || teacher.getEmail().toLowerCase().contains(pattern)) {
                result.add(teacher);
            }
        }
        return result;
    }
}