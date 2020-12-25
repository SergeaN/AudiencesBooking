package com.etu.audiencesbooking.teachers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.CallBackItemTouch;
import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.SessionManager;
import com.etu.audiencesbooking.bookings.BookingLab;
import com.etu.audiencesbooking.schedule.ScheduleLab;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeacherListFragment extends Fragment {
    private static final String DIALOG_ADD_TEACHER = "DialogTeacher";
    private static final int REQUEST_CHANGE = 0;

    private List<Teacher> mTeachers;
    private DatabaseReference mDatabase;
    private int mTypeOfUser;
    private String mPattern;

    private RecyclerView mRecyclerView;
    private TeacherAdapter mAdapter;
    private FloatingActionButton mAddButton;

    private EditText mSearch;
    private ImageView mClearSearchButton;

    public static TeacherListFragment newInstance() {
        return new TeacherListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeachers = new ArrayList<>();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().
                setTitle(getResources().getString(R.string.teachers));

        View view = inflater.inflate(R.layout.teacher_list_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.teacher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddButton = view.findViewById(R.id.add_teacher_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherFragment dialog = TeacherFragment.newInstance(null);
                FragmentManager manager = getFragmentManager();
                dialog.setTargetFragment(TeacherListFragment.this, REQUEST_CHANGE);
                dialog.setCancelable(false);
                dialog.show(manager, DIALOG_ADD_TEACHER);
                mAddButton.setVisibility(View.INVISIBLE);
            }
        });

        ItemTouchHelper.Callback callback
                = new TeacherItemTouchHelperCallBack(new CallBackItemTouch() {
            @Override
            public void itemTouchMove(int oldPosition, int newPosition) {
                //nothing
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
                //we will delete and also we want to undo
                String id = mTeachers
                        .get(viewHolder.getBindingAdapterPosition())
                        .getUUID();

                //backup of removed item for undo
                final Teacher deletedTeacher = mTeachers.get(viewHolder
                        .getBindingAdapterPosition());
                final int deletedIndex = viewHolder.getBindingAdapterPosition();

                if (!isInvolvedTeacher(id)) {
                    //remove item from recyclerView
                    mAdapter.removeItem(viewHolder.getBindingAdapterPosition());
                    TeacherLab.get(getContext()).removeTeacher(id);

                    //showing shackbar
                    Snackbar snackbar = Snackbar.make(getActivity()
                                    .findViewById(R.id.teacher_recycler_view),
                            deletedTeacher.getFirstName() + " " + deletedTeacher.getLastName()
                                    + " removed...", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAdapter.addItem(deletedIndex, deletedTeacher);
                            mTeachers.add(deletedIndex, deletedTeacher);
                            TeacherLab.get(getContext()).addTeacher(deletedTeacher);
                        }
                    });
                    snackbar.setActionTextColor(Color.GREEN);
                    snackbar.show();
                } else {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.involved_teacher),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        updateList();

        if (mTypeOfUser == 1) { //set admin function
            mAddButton.setVisibility(View.VISIBLE);
            touchHelper.attachToRecyclerView(mRecyclerView);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("Teachers");
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

        mClearSearchButton = view.findViewById(R.id.clear_view);
        mClearSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClearSearchButton.setVisibility(View.GONE);
                mSearch.getText().clear();
                mPattern = null;
                updateList();
            }
        });

        mSearch = view.findViewById(R.id.search_teacher);
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mClearSearchButton.setVisibility(View.VISIBLE);
                mPattern = s.toString();
                updateList();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mPattern = null;
                    mClearSearchButton.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    private boolean isInvolvedTeacher(String id) {
        return ScheduleLab.get(getContext()).findByTeacher(id).size() != 0
                || BookingLab.get(getContext()).findByTeacher(id).size() != 0;
    }

    public void updateList() {
        if (mPattern == null) {
            mTeachers = TeacherLab.get(getContext()).getTeachers();
        } else {
            mTeachers = TeacherLab.get(getContext()).findByPattern(mPattern);
        }
        mAdapter = new TeacherAdapter(mTeachers, this, mTypeOfUser);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            mAddButton.setVisibility(View.VISIBLE);
            return;
        }
        if (requestCode == REQUEST_CHANGE) {
            Teacher teacher = (Teacher) data.getSerializableExtra(TeacherFragment.EXTRA_TEACHER);
            TeacherLab.get(getContext()).addTeacher(teacher);
            mAddButton.setVisibility(View.VISIBLE);
        }
    }


}
