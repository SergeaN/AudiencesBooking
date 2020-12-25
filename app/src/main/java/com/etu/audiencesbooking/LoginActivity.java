package com.etu.audiencesbooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.etu.audiencesbooking.teachers.Teacher;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CHECKBOX = "checkbox";

    private Button mLoginButton;
    private Button mRegisterButton;
    private RelativeLayout mProgressBar;
    private CheckBox mCheckBox;
    private Button mForgetPassButton;

    private TextInputLayout mEmailField;
    private TextInputLayout mPasswordField;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EMAIL, mEmailField.getEditText().getText().toString());
        outState.putString(KEY_PASSWORD, mPasswordField.getEditText().getText().toString());
        outState.putBoolean(KEY_CHECKBOX, mCheckBox.isChecked());
        Bundle bundle = new Bundle();
        outState.putBundle("SaveFields", bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mProgressBar = findViewById(R.id.login_progress_bar);
        mCheckBox = findViewById(R.id.remember_me);

        mEmailField = findViewById(R.id.login_email);
        mPasswordField = findViewById(R.id.login_password);

        if (savedInstanceState != null) {
            mEmailField.getEditText()
                    .setText(savedInstanceState.getString(KEY_EMAIL, ""));
            mPasswordField.getEditText()
                    .setText(savedInstanceState.getString(KEY_PASSWORD, ""));
            mCheckBox.setChecked(savedInstanceState.getBoolean(KEY_CHECKBOX, true));
        }

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

        mRegisterButton = findViewById(R.id.sign_up_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });mCheckBox.setChecked(true);

        mForgetPassButton = findViewById(R.id.forget_password_button);
        mForgetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,
                        getResources().getString(R.string.forget_password_toast),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkData() {

        if (!isConnected(this)) {
            showConnectionDialog();
            return;
        }

        if (!isFieldsValid()) {
            Toast.makeText(LoginActivity.this,
                    getResources().getString(R.string.not_all_field_are_filled),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValid()) {
            Toast.makeText(LoginActivity.this,
                    getResources().getString(R.string.incorrect_email),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);

        final String email = mEmailField.getEditText().getText().toString();
        final String password = mPasswordField.getEditText().getText().toString();

        Query checkTeacher = FirebaseDatabase.getInstance().getReference("Teachers");
        checkTeacher.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Teacher teacher = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Teacher currentTeacher = dataSnapshot.getValue(Teacher.class);
                    assert currentTeacher != null;
                    if (currentTeacher.getEmail().equals(email)) {
                        teacher = currentTeacher;
                        break;
                    }
                }
                if (teacher == null) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.no_user_exist),
                            Toast.LENGTH_SHORT).show();
                } else if (teacher.getPassword().equals(password)) {
                    //Create a session
                    SessionManager sessionManager = new SessionManager(LoginActivity.this,
                            SessionManager.SESSION_USERSESSION);
                    sessionManager.createLoginSession(teacher.getUUID());

                    //Create a remember me session
                    if (mCheckBox.isChecked()) {
                        SessionManager sessionManagerRememberMe
                                = new SessionManager(LoginActivity.this,
                                SessionManager.SESSION_REMEMBERME);
                        sessionManagerRememberMe.createRememberMeSession(email, password);
                    }

                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.password_doesnt_match),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConnect = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConnect = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConnect != null && wifiConnect.isConnected()) ||
                (mobileConnect != null && mobileConnect.isConnected());
    }

    private void showConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setMessage(getResources().getString(R.string.connect_to_the_internet))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.connect),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        null);
        builder.show();
    }

    private boolean isFieldsValid() {
        return mEmailField.getEditText().getText().length() != 0
                && mPasswordField.getEditText().getText().length() != 0;
    }

    private boolean isEmailValid() {
        String email = mEmailField.getEditText().getText().toString();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}