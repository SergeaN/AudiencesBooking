package com.etu.audiencesbooking.teachers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.etu.audiencesbooking.R;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherHolder> {
    private static final String DIALOG_ADD_TEACHER = "DialogAddTeacher";
    private static final int REQUEST_ADD_TEACHER = 0;

    private List<Teacher> mTeachers;
    private Fragment mFragment;
    private int mTypeOfUser;

    public TeacherAdapter(List<Teacher> teachers, Fragment fragment, int typeOfUser) {
        mTeachers = teachers;
        mFragment = fragment;
        mTypeOfUser = typeOfUser;
    }

    @NonNull
    @Override
    public TeacherHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_list_item, parent, false);
        return new TeacherHolder(view);
    }

    @Override
    public void onBindViewHolder(TeacherHolder holder, int position) {
        Teacher teacher = mTeachers.get(position);
        holder.bind(teacher);
    }

    @Override
    public int getItemCount() {
        return mTeachers.size();
    }

    public void setTeachers(List<Teacher> teachers) {
        mTeachers = teachers;
    }

    public void addItem(int position, Teacher teacher) {
        mTeachers.add(position, teacher);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mTeachers.remove(position);
        notifyItemRemoved(position);
    }

    public class TeacherHolder extends RecyclerView.ViewHolder {
        private Teacher mTeacher;
        private TextView mFullNameView;
        private TextView mInfoView;
        public LinearLayout viewFront;
        public RelativeLayout viewBackground;

        public TeacherHolder(@NonNull View itemView) {
            super(itemView);
            mFullNameView = (TextView) itemView.findViewById(R.id.teacher_name);
            mInfoView = (TextView) itemView.findViewById(R.id.teacher_info);
            viewFront = (LinearLayout) itemView
                    .findViewById(R.id.list_teacher_item_view);
            viewBackground = (RelativeLayout) itemView
                    .findViewById(R.id.background_list_teacher_item_view);
            if (mTypeOfUser == 1) { //set admin function
                viewFront.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = mFragment.getFragmentManager();
                        TeacherFragment dialog = TeacherFragment.newInstance(mTeacher.getUUID());
                        dialog.setTargetFragment(mFragment, REQUEST_ADD_TEACHER);
                        dialog.setCancelable(false);
                        dialog.show(manager, DIALOG_ADD_TEACHER);
                    }
                });
            }

        }
        public void bind(Teacher teacher) {
            mTeacher = teacher;
            String fullName = mTeacher.getFirstName() + " " + mTeacher.getLastName()
                    + " (" + mTeacher.getDepartment() + ")";
            String info = mTeacher.getEmail();
            mFullNameView.setText(fullName);
            mInfoView.setText(info);
        }

    }
}

