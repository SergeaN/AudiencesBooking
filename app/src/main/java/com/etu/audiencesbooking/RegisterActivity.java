package com.etu.audiencesbooking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.etu.audiencesbooking.teachers.Teacher;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_PASSWORD = "password";

    private TextInputLayout mFirsNameField;
    private TextInputLayout mLastNameField;
    private TextInputLayout mEmailField;
    private TextInputLayout mDepartmentField;
    private TextInputLayout mPasswordField;
    private Button mRegButton;

    private DatabaseReference mDatabase;
    private boolean mIsRegisteredEmail;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FIRSTNAME, mFirsNameField.getEditText().getText().toString());
        outState.putString(KEY_LASTNAME, mLastNameField.getEditText().getText().toString());
        outState.putString(KEY_EMAIL, mEmailField.getEditText().getText().toString());
        outState.putString(KEY_DEPARTMENT, mDepartmentField.getEditText().getText().toString());
        outState.putString(KEY_PASSWORD, mPasswordField.getEditText().getText().toString());
        Bundle bundle = new Bundle();
        outState.putBundle("SaveFields", bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mFirsNameField = findViewById(R.id.reg_first_name);
        mLastNameField = findViewById(R.id.reg_last_name);
        mEmailField = findViewById(R.id.reg_email);
        mDepartmentField = findViewById(R.id.reg_department);
        mPasswordField = findViewById(R.id.reg_password);

        mIsRegisteredEmail = false;

        if (savedInstanceState != null) {
            mFirsNameField.getEditText()
                    .setText(savedInstanceState.getString(KEY_FIRSTNAME, ""));
            mLastNameField.getEditText()
                    .setText(savedInstanceState.getString(KEY_LASTNAME, ""));
            mEmailField.getEditText()
                    .setText(savedInstanceState.getString(KEY_EMAIL, ""));
            mDepartmentField.getEditText()
                    .setText(savedInstanceState.getString(KEY_DEPARTMENT, ""));
            mPasswordField.getEditText()
                    .setText(savedInstanceState.getString(KEY_PASSWORD, ""));
        }

        mRegButton = findViewById(R.id.reg_button);
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isFieldsValid()) {
                    Toast.makeText(RegisterActivity.this,
                            getResources().getString(R.string.not_all_field_are_filled),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid()) {
                    Toast.makeText(RegisterActivity.this,
                            getResources().getString(R.string.incorrect_email),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                setRegisteredEmail();

                if (mIsRegisteredEmail) {
                    Toast.makeText(RegisterActivity.this,
                            getResources().getString(R.string.registered_email),
                            Toast.LENGTH_SHORT).show();
                } else {

                    mDatabase = FirebaseDatabase.getInstance().getReference("Teachers");

                    UUID id = UUID.randomUUID();
                    String firstName = mFirsNameField.getEditText().getText().toString();
                    String secondName = mLastNameField.getEditText().getText().toString();
                    String email = mEmailField.getEditText().getText().toString();
                    String department = mDepartmentField.getEditText().getText().toString();
                    String password = mPasswordField.getEditText().getText().toString();
                    int type = 0; //0 - user, 1 - admin

                    Teacher teacher = new Teacher(
                            id.toString(), firstName, secondName, email, department, password, type
                    );

                    mDatabase.child(id.toString()).setValue(teacher);

                    finish();
                }
            }
        });
    }

    private boolean isFieldsValid() {
        return mFirsNameField.getEditText().getText().length() != 0
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
                Toast.makeText(RegisterActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
