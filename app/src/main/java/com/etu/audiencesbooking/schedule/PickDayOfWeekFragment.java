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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.List;

public class PickDayOfWeekFragment extends DialogFragment {
    public static final String EXTRA_PICK_DAY_OF_WEEK
            = "com.etu.audiencesbooking.schedules.pickdayofweek";

    private String[] mDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
            "FRIDAY", "SATURDAY", "SUNDAY"};

    private RecyclerView mRecyclerView;
    private PickDayOfWeekAdapter mAdapter;

    public static PickDayOfWeekFragment newInstance() {
        return new PickDayOfWeekFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.pick_recyclerview_fragment, null);

        mRecyclerView = view.findViewById(R.id.pick_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new PickDayOfWeekAdapter(Arrays.asList(mDays));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Pick Day Of Week: ")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, null);
                    }
                });
        return dialog.create();
    }

    private void sendResult(int resultCode, String dayOfWeek) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PICK_DAY_OF_WEEK, dayOfWeek);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private class PickDayOfWeekHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mDayOfWeek;

        public PickDayOfWeekHolder(@NonNull View itemView) {
            super(itemView);
            mDayOfWeek = (TextView) itemView;
            mDayOfWeek.setOnClickListener(this);
        }

        public void bind(String dayOfWeek) {
            mDayOfWeek.setText(dayOfWeek);
        }

        @Override
        public void onClick(View v) {
            sendResult(Activity.RESULT_OK, mDayOfWeek.getText().toString());
            dismiss();
        }
    }

    private class PickDayOfWeekAdapter extends RecyclerView.Adapter<PickDayOfWeekHolder> {

        private final List<String> mDays;

        public PickDayOfWeekAdapter(List<String> days) {
            mDays = days;
        }

        @NonNull
        @Override
        public PickDayOfWeekHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new PickDayOfWeekHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PickDayOfWeekHolder holder, int position) {
            holder.bind(mDays.get(position));
        }

        @Override
        public int getItemCount() {
            return mDays.size();
        }
    }
}
