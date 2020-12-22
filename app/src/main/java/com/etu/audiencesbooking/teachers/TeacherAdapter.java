package com.etu.audiencesbooking.teachers;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.etu.audiencesbooking.R;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherHolder> {

    public TeacherAdapter(List<Teacher> teachers, Fragment fragment, int typeOfUser) {
    }

    @NonNull
    @Override
    public TeacherHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TeacherHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TeacherHolder extends RecyclerView.ViewHolder {

        public TeacherHolder(@NonNull View itemView) {
            super(itemView);

        }

    }
}

