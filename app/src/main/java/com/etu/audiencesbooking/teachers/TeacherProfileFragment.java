package com.etu.audiencesbooking.teachers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.SessionManager;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;

public class TeacherProfileFragment extends Fragment {
    private Teacher mTeacher;

    private MaterialTextView mFirstNameView;
    private MaterialTextView mLastNameView;
    private MaterialTextView mEmailView;
    private MaterialTextView mDepartmentView;

    public static TeacherProfileFragment newInstance() {
        return new TeacherProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.myprofile);

        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        SessionManager sessionManager = new SessionManager(getContext(),
                SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        String id = userDetails.get(SessionManager.KEY_ID);
        mTeacher = TeacherLab.get(getContext()).getTeacher(id);

        mFirstNameView = view.findViewById(R.id.profile_first_name);
        mLastNameView = view.findViewById(R.id.profile_last_name);
        mEmailView = view.findViewById(R.id.profile_email);
        mDepartmentView = view.findViewById(R.id.profile_department);

        String firstName;
        String lastName;
        String department;
        String email;
        if (mTeacher != null) {
            firstName = mTeacher.getFirstName();
            lastName = mTeacher.getLastName();
            department = "Department of " + mTeacher.getDepartment();
            email = mTeacher.getEmail();
        } else {
            firstName = "Not found";
            lastName = "Not found";
            department = "Not found";
            email = "Not found";
        }
        mFirstNameView.setText(firstName);
        mLastNameView.setText(lastName);
        mDepartmentView.setText(department);
        mEmailView.setText(email);

        return view;
    }

}
