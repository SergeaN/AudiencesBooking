package com.etu.audiencesbooking.schedule;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.teachers.Teacher;
import com.etu.audiencesbooking.teachers.TeacherLab;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class PickTeacherFragment extends DialogFragment {

    public static final String EXTRA_PICK_TEACHER
            = "com.etu.audiencesbooking.schedules.pickteacher";

    private List<Teacher> mTeachers;

    private RecyclerView mRecyclerView;
    private PickTeacherAdapter mAdapter;

    public static PickTeacherFragment newInstance() {
        return new PickTeacherFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.pick_recyclerview_fragment, null);

        mRecyclerView = view.findViewById(R.id.pick_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTeachers = TeacherLab.get(getContext()).getTeachers();
        mAdapter = new PickTeacherAdapter(mTeachers);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Pick Teacher: ")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, null);
                    }
                });
        return dialog.create();
    }

    private void sendResult(int resultCode, Teacher teacher) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PICK_TEACHER, teacher);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private class PickTeacherHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Teacher mTeacher;
        private TextView mNameOfTeacher;

        public PickTeacherHolder(@NonNull View itemView) {
            super(itemView);
            mNameOfTeacher = (TextView) itemView;
            mNameOfTeacher.setOnClickListener(this);
        }

        public void bind(Teacher teacher) {
            mTeacher = teacher;
            String fullName = mTeacher.getFirstName() + " " + mTeacher.getLastName();
            mNameOfTeacher.setText(fullName);
        }

        @Override
        public void onClick(View v) {
            sendResult(Activity.RESULT_OK, mTeacher);
            dismiss();
        }
    }

    private class PickTeacherAdapter extends RecyclerView.Adapter<PickTeacherHolder> {

        private final List<Teacher> mTeachers;

        public PickTeacherAdapter(List<Teacher> teachers) {
            mTeachers = teachers;
        }

        @NonNull
        @Override
        public PickTeacherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new PickTeacherHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PickTeacherHolder holder, int position) {
            holder.bind(mTeachers.get(position));
        }

        @Override
        public int getItemCount() {
            return mTeachers.size();
        }
    }

}
