package com.etu.audiencesbooking.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.SessionManager;
import com.etu.audiencesbooking.teachers.Teacher;
import com.etu.audiencesbooking.teachers.TeacherLab;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScheduleListFragment extends Fragment {

    private static final int REQUEST_CHANGE = 0;

    public static final int RESULT_ADD = 2;
    public static final int RESULT_DELETE = 3;

    private List<Schedule> mSchedules;
    private DatabaseReference mDatabase;
    private int mTypeOfUser;

    private RecyclerView mRecyclerView;
    private ScheduleAdapter mAdapter;
    private FloatingActionButton mAddButton;

    public static ScheduleListFragment newInstance() {
        return new ScheduleListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSchedules = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(getContext(),
                SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String id = userDetails.get(SessionManager.KEY_ID);
        Teacher user = TeacherLab.get(getContext()).getTeacher(id);
        if (user != null) mTypeOfUser = user.getType();
        else mTypeOfUser = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.schedule);

        View view = inflater.inflate(R.layout.schedule_list_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.schedule_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddButton = view.findViewById(R.id.add_schedule_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleFragment fragment = ScheduleFragment.newInstance(null);
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setTargetFragment(ScheduleListFragment.this, REQUEST_CHANGE);
                fragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        updateList();

        if (mTypeOfUser == 1) { //set admin function
            mAddButton.setVisibility(View.VISIBLE);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Schedule");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });

        return view;
    }

    private void updateList() {
        mSchedules = ScheduleLab.get(getContext()).getFullSchedule();
        mAdapter = new ScheduleAdapter(mSchedules, getContext(), this, mTypeOfUser);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CHANGE && resultCode == RESULT_ADD) {
            Schedule schedule = (Schedule) data
                    .getSerializableExtra(ScheduleFragment.EXTRA_SCHEDULE);
            ScheduleLab.get(getContext()).addSchedule(schedule);
        } else if (requestCode == REQUEST_CHANGE && resultCode == RESULT_DELETE) {
            Schedule schedule = (Schedule) data
                    .getSerializableExtra(ScheduleFragment.EXTRA_SCHEDULE);
            ScheduleLab.get(getContext()).removeSchedule(schedule.getUUID());
        }
    }
}
