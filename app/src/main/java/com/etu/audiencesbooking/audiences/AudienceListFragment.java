package com.etu.audiencesbooking.audiences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.SessionManager;
import com.etu.audiencesbooking.bookings.BookingLab;
import com.etu.audiencesbooking.schedule.ScheduleLab;
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

public class AudienceListFragment extends Fragment {

    private static final String DIALOG_ADD_AUDIENCE = "DialogAudience";
    private static final int REQUEST_ADD_AUDIENCE = 0;

    public static final int RESULT_ADD = 2;
    public static final int RESULT_DELETE = 3;

    private List<Audience> mAudiences;
    private DatabaseReference mDatabase;
    private int mTypeOfUser;

    private RecyclerView mRecyclerView;
    private AudienceAdapter mAdapter;
    private FloatingActionButton mAddButton;

    public static AudienceListFragment newInstance() {
        return new AudienceListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudiences = new ArrayList<>();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.audiences);

        View view = inflater.inflate(R.layout.audience_list_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.audience_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mAddButton = view.findViewById(R.id.add_audience_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudienceFragment dialog = AudienceFragment.newInstance(null);
                FragmentManager manager = getFragmentManager();
                dialog.setTargetFragment(AudienceListFragment.this, REQUEST_ADD_AUDIENCE);
                dialog.setCancelable(false);
                dialog.show(manager, DIALOG_ADD_AUDIENCE);
                mAddButton.setVisibility(View.GONE);
            }
        });
        updateList();

        if (mTypeOfUser == 1) { //set admin function
            mAddButton.setVisibility(View.VISIBLE);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Audiences");
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
        mAudiences = AudienceLab.get(getContext()).getAudiences();
        mAdapter = new AudienceAdapter(mAudiences, this, mTypeOfUser);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            mAddButton.setVisibility(View.VISIBLE);
            return;
        }
        if (requestCode == REQUEST_ADD_AUDIENCE && resultCode == RESULT_ADD) {
            Audience audience = (Audience) data
                    .getSerializableExtra(AudienceFragment.EXTRA_AUDIENCE);
            AudienceLab.get(getContext()).addAudience(audience);
            mAddButton.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_ADD_AUDIENCE && resultCode == RESULT_DELETE) {
            Audience audience = (Audience) data
                    .getSerializableExtra(AudienceFragment.EXTRA_AUDIENCE);
            if (!isInvolvedAudience(audience.getUUID())) {
                AudienceLab.get(getContext()).removeAudience(audience.getUUID());
            } else {
                Toast.makeText(getContext(),
                        getResources().getString(R.string.involved_teacher),
                        Toast.LENGTH_SHORT).show();
            }
            mAddButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean isInvolvedAudience(String id) {
        return ScheduleLab.get(getContext()).findByAudience(id).size() != 0
                || BookingLab.get(getContext()).findByAudience(id).size() != 0;
    }
}
