package com.etu.audiencesbooking.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.audiences.Audience;
import com.etu.audiencesbooking.audiences.AudienceLab;
import com.etu.audiencesbooking.teachers.Teacher;
import com.etu.audiencesbooking.teachers.TeacherLab;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> {

    private static final int REQUEST_ADD_SCHEDULE = 0;

    private List<Schedule> mSchedule;
    private Context mContext;

    private Fragment mFragment;
    private int mTypeOfUser;

    public ScheduleAdapter(List<Schedule> schedules, Context context,
                           Fragment fragment, int typeOfUser) {
        mSchedule = schedules;
        mContext = context;
        mFragment = fragment;
        mTypeOfUser = typeOfUser;
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleHolder holder, int position) {
        Schedule schedule = mSchedule.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return mSchedule.size();
    }

    public void setSchedule(List<Schedule> schedule) {
        mSchedule = schedule;
    }

    public void addItem(int position, Schedule schedule) {
        mSchedule.add(position, schedule);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mSchedule.remove(position);
        notifyItemRemoved(position);
    }

    public class ScheduleHolder extends RecyclerView.ViewHolder {
        private Schedule mSchedule;
        private Audience mAudience;
        private Teacher mTeacher;

        private TextView mNumber;
        private TextView mCapacity;
        private TextView mDayOfWeek;
        private TextView mTime;
        private TextView mNameOfTeacher;

        private CardView mView;

        public ScheduleHolder(@NonNull View itemView) {
            super(itemView);
            mNumber = itemView.findViewById(R.id.schedule_number_text);
            mCapacity = itemView.findViewById(R.id.schedule_capacity_text);
            mDayOfWeek = itemView.findViewById(R.id.schedule_day_of_week_text);
            mTime = itemView.findViewById(R.id.schedule_time_text);
            mNameOfTeacher = itemView.findViewById(R.id.schedule_teacher_name_text);

            mView = itemView.findViewById(R.id.schedule_list_item_view);

            if (mTypeOfUser == 1) { //set admin function
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleFragment fragment = ScheduleFragment
                                .newInstance(mSchedule.getUUID());
                        FragmentManager fragmentManager = mFragment.getFragmentManager();
                        fragment.setTargetFragment(mFragment, REQUEST_ADD_SCHEDULE);
                        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        }

        public void bind(Schedule schedule) {
            mSchedule = schedule;
            mAudience = AudienceLab.get(mContext).getAudience(mSchedule.getAudienceID());
            mTeacher = TeacherLab.get(mContext).getTeacher(mSchedule.getTeacherID());

            String number = "№" + mAudience.getNumber();
            String capacity = Integer.toString(mAudience.getCapacity());
            String dayOfWeek = mSchedule.getDayOfWeek();
            String time = mSchedule.getStartTime() + " - " + mSchedule.getEndTime();
            String nameOfTeacher = mTeacher.getFirstName() + " " + mTeacher.getLastName();

            mNumber.setText(number);
            mCapacity.setText(capacity);
            mDayOfWeek.setText(dayOfWeek);
            mTime.setText(time);
            mNameOfTeacher.setText(nameOfTeacher);
        }
    }

}
