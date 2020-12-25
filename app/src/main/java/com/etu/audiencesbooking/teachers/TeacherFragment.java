package com.etu.audiencesbooking.teachers;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.etu.audiencesbooking.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class TeacherFragment extends DialogFragment {

    public static final String EXTRA_TEACHER = "com.etu.audiencesbooking.teacher_dialog";

    private static final String ARG_TEACHER_ID = "teacher_id";

    private String mUuidField;
    private TextInputLayout mFirstNameField;
    private TextInputLayout mLastNameField;
    private TextInputLayout mEmailField;
    private TextInputLayout mDepartmentField;
    private TextInputLayout mPasswordField;

    private Teacher mTeacher;
    private boolean mIsRegisteredEmail;

    public static TeacherFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ARG_TEACHER_ID, id);
        TeacherFragment fragment = new TeacherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getArguments().getString(ARG_TEACHER_ID);
        if (id != null) mTeacher = TeacherLab.get(getActivity()).getTeacher(id);
    }

    private void sendResult(int resultCode, Teacher teacher) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TEACHER, teacher);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.teacher_dialog_fragment, null);

        mFirstNameField = view.findViewById(R.id.dialog_first_name);
        mLastNameField = view.findViewById(R.id.dialog_last_name);
        mEmailField = view.findViewById(R.id.dialog_email);
        mDepartmentField = view.findViewById(R.id.dialog_department);
        mPasswordField = view.findViewById(R.id.dialog_password);

        if (mTeacher != null) { //change user
            mUuidField = mTeacher.getUUID();
            mFirstNameField.getEditText().setText(mTeacher.getFirstName());
            mLastNameField.getEditText().setText(mTeacher.getLastName());
            mEmailField.getEditText().setText(mTeacher.getEmail());
            mDepartmentField.getEditText().setText(mTeacher.getDepartment());
            mPasswordField.getEditText().setText(mTeacher.getPassword());
        } else {
            mUuidField = UUID.randomUUID().toString(); //create user
        }

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle(getResources().getString(R.string.edit_teacher))
                .setView(view)
                .setPositiveButton(getResources()
                        .getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isFieldsValid()) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.not_all_field_are_filled),
                                    Toast.LENGTH_LONG).show();
                            sendResult(Activity.RESULT_CANCELED, null);
                            return;
                        }
                        if (!isEmailValid()) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.incorrect_email),
                                    Toast.LENGTH_LONG).show();
                            sendResult(Activity.RESULT_CANCELED, null);
                            return;
                        }
                        if (mIsRegisteredEmail) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.registered_email),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String id = mUuidField;
                            String firstName = mFirstNameField.getEditText().getText().toString();
                            String lastName = mLastNameField.getEditText().getText().toString();
                            String email = mEmailField.getEditText().getText().toString();
                            String department = mDepartmentField.getEditText().getText().toString();
                            String password = mPasswordField.getEditText().getText().toString();
                            Teacher teacher = new Teacher(
                                    id, firstName, lastName, email, department, password, 0
                            );
                            sendResult(Activity.RESULT_OK, teacher);
                        }
                    }
                })
                .setNegativeButton(getResources()
                        .getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, null);
                    }
                });

        return dialog.create();
    }

    private boolean isFieldsValid() {
        return mFirstNameField.getEditText().getText().length() != 0
                && mLastNameField.getEditText().getText().length() != 0
                && mEmailField.getEditText().getText().length() != 0
                && mDepartmentField.getEditText().getText().length() != 0
                && mPasswordField.getEditText().getText().length() != 0;
    }

    private boolean isEmailValid() {
        String email = mEmailField.getEditText().getText().toString();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setRegisteredEmail() {
        final String email = mEmailField.getEditText().getText().toString();
        Query checkTeacher = FirebaseDatabase.getInstance().getReference("Teachers");
        checkTeacher.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Teacher currentTeacher = dataSnapshot.getValue(Teacher.class);
                    assert currentTeacher != null;
                    if (currentTeacher.getEmail().equals(email)) {
                        mIsRegisteredEmail = true;
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
