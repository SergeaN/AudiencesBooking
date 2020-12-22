package com.etu.audiencesbooking.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.etu.audiencesbooking.AudiencePickerFragment;
import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.TimePickerFragment;
import com.etu.audiencesbooking.audiences.Audience;
import com.etu.audiencesbooking.audiences.AudienceLab;
import com.etu.audiencesbooking.teachers.Teacher;
import com.etu.audiencesbooking.teachers.TeacherLab;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ScheduleFragment extends Fragment {

    private static final String DIALOG_DAY_OF_WEEK = "DialogDay";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_AUDIENCE = "DialogAudience";
    private static final String DIALOG_TEACHER = "DialogTeacher";

    private static final int REQUEST_DAY_OF_WEEK = 0;
    private static final int REQUEST_START_TIME = 1;
    private static final int REQUEST_END_TIME = 2;
    private static final int REQUEST_AUDIENCE = 3;
    private static final int REQUEST_TEACHER = 4;

    private static final String ARG_SCHEDULE_ID = "schedule_id";
    public static final String EXTRA_SCHEDULE = "com.etu.audiencesbooking.schedules.addschedule";

    private MaterialTextView mDayOfWeekField;
    private MaterialTextView mStartTimeField;
    private MaterialTextView mEndTimeField;
    private MaterialTextView mAudienceNumberField;
    private MaterialTextView mTeacherNameField;
    private Button mSaveButton;
    private Button mCancelButton;
    private Button mDeleteButton;

    private Schedule mSchedule;

    private String mUUID;
    private String mDayOfWeek;
    private Date mStartTime;
    private Date mEndTime;
    private Audience mAudience;
    private Teacher mTeacher;

    public static ScheduleFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(ARG_SCHEDULE_ID, id);
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Schedule schedule) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SCHEDULE, schedule);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getArguments().getString(ARG_SCHEDULE_ID);
        if (id != null) mSchedule = ScheduleLab.get(getContext()).getSchedule(id);
        if (mStartTime == null) {
            mStartTime = new GregorianCalendar(2000, 11,
                    22, 12, 0).getTime(); // random date
        }
        if (mEndTime == null) {
            mEndTime = new GregorianCalendar(2000, 11,
                    22, 12, 0).getTime();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater
                .inflate(R.layout.schedule_dialog_fragment, container, false);

        mDayOfWeekField = view.findViewById(R.id.set_day_of_week);
        mDayOfWeekField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerDialog(REQUEST_DAY_OF_WEEK, DIALOG_DAY_OF_WEEK,
                        PickDayOfWeekFragment.newInstance());
            }
        });

        mStartTimeField = view.findViewById(R.id.set_start_time);
        mStartTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(REQUEST_START_TIME, mStartTime);
            }
        });

        mEndTimeField = view.findViewById(R.id.set_end_time);
        mEndTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(REQUEST_END_TIME, mEndTime);
            }
        });

        mAudienceNumberField = view.findViewById(R.id.set_audience);
        mAudienceNumberField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerDialog(REQUEST_AUDIENCE, DIALOG_AUDIENCE,
                        AudiencePickerFragment.newInstance());
            }
        });

        mTeacherNameField = view.findViewById(R.id.set_teacher);
        mTeacherNameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerDialog(REQUEST_TEACHER, DIALOG_TEACHER,
                        PickTeacherFragment.newInstance());
            }
        });

        mSaveButton = view.findViewById(R.id.save_schedule_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFieldsValidate()) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.not_all_field_are_filled),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isCorrectTime()) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.start_time_is_greater),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isFreeTime(0)) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.audience_busy),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isFreeTime(1)) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.teacher_busy),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Schedule schedule = new Schedule(
                        mUUID, mAudience.getUUID(), mTeacher.getUUID(),
                        mDayOfWeek, mStartTimeField.getText().toString(),
                        mEndTimeField.getText().toString()
                );
                sendResult(ScheduleListFragment.RESULT_ADD, schedule);
            }
        });

        mCancelButton = view.findViewById(R.id.cancel_schedule_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_CANCELED, null);
            }
        });

        mDeleteButton = view.findViewById(R.id.delete_schedule_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Schedule schedule = new Schedule();
                schedule.setUUID(mUUID);
                sendResult(ScheduleListFragment.RESULT_DELETE, schedule);
            }
        });

        if (mSchedule != null) {
            mUUID = mSchedule.getUUID();

            mDayOfWeek = mSchedule.getDayOfWeek();
            mDayOfWeekField.setText(mDayOfWeek);
            mDayOfWeekField.setTextColor(getResources().getColor(R.color.black, null));

            mStartTimeField.setText(mSchedule.getStartTime());
            mStartTimeField.setTextColor(getResources().getColor(R.color.black, null));

            mEndTimeField.setText(mSchedule.getEndTime());
            mEndTimeField.setTextColor(getResources().getColor(R.color.black, null));

            mAudience = AudienceLab.get(getContext()).getAudience(mSchedule.getAudienceID());
            String number = mAudience.getNumber() + " (" + mAudience.getCapacity() + ")";
            mAudienceNumberField.setText(number);
            mAudienceNumberField.setTextColor(getResources().getColor(R.color.black, null));

            mTeacher = TeacherLab.get(getContext()).getTeacher(mSchedule.getTeacherID());
            String fullName = mTeacher.getFirstName() + " " + mTeacher.getLastName();
            mTeacherNameField.setText(fullName);
            mTeacherNameField.setTextColor(getResources().getColor(R.color.black, null));

        } else {
            mUUID = UUID.randomUUID().toString();
            mDeleteButton.setVisibility(View.GONE);
        }

        return view;
    }

    public void showTimePickerDialog(int requestCode, Date time) {
        TimePickerFragment dialog = TimePickerFragment.newInstance(time);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.setTargetFragment(ScheduleFragment.this, requestCode);
        dialog.setCancelable(false);
        dialog.show(fragmentManager, DIALOG_TIME);
    }

    public void showPickerDialog(int requestCode, String tag, DialogFragment dialog) {
        FragmentManager fragmentManager = getFragmentManager();
        dialog.setTargetFragment(ScheduleFragment.this, requestCode);
        dialog.setCancelable(false);
        dialog.show(fragmentManager, tag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DAY_OF_WEEK) {
            mDayOfWeek = (String) data
                    .getSerializableExtra(PickDayOfWeekFragment.EXTRA_PICK_DAY_OF_WEEK);
            mDayOfWeekField.setText(mDayOfWeek);
            mDayOfWeekField.setTextColor(getResources().getColor(R.color.black, null));
        } else if (requestCode == REQUEST_START_TIME) {
            mStartTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mStartTimeField.setText(dateFormat.format(mStartTime));
            mStartTimeField.setTextColor(getResources().getColor(R.color.black, null));
        } else if (requestCode == REQUEST_END_TIME) {
            mEndTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            mEndTimeField.setText(dateFormat.format(mEndTime));
            mEndTimeField.setTextColor(getResources().getColor(R.color.black, null));
        } else if (requestCode == REQUEST_AUDIENCE) {
            mAudience = (Audience) data
                    .getSerializableExtra(AudiencePickerFragment.EXTRA_PICK_AUDIENCE);
            String number = mAudience.getNumber() + " (" + mAudience.getCapacity() + ")";
            mAudienceNumberField.setText(number);
            mAudienceNumberField.setTextColor(getResources().getColor(R.color.black, null));
        } else if (requestCode == REQUEST_TEACHER) {
            mTeacher = (Teacher) data
                    .getSerializableExtra(PickTeacherFragment.EXTRA_PICK_TEACHER);
            String fullName = mTeacher.getFirstName() + " " + mTeacher.getLastName();
            mTeacherNameField.setText(fullName);
            mTeacherNameField.setTextColor(getResources().getColor(R.color.black, null));
        }
    }

    private boolean isFieldsValidate() {
        return mDayOfWeekField.getText() != getResources().getString(R.string.day_of_week)
                && mStartTimeField.getText() != getResources().getString(R.string.start_time)
                && mEndTimeField.getText() != getResources().getString(R.string.end_time)
                && mAudienceNumberField.getText() != getResources().getString(R.string.audience)
                && mTeacherNameField.getText() != getResources().getString(R.string.teacher);
    }

    private boolean isCorrectTime() {
        return mStartTime.compareTo(mEndTime) <= 0;
    }

    private boolean isFreeTime(int mode) {
        List<Schedule> scheduleList;
        if (mode == 0) scheduleList = ScheduleLab.get(getContext())
                .findByAudience(mAudience.getUUID());
        else scheduleList = ScheduleLab.get(getContext())
                .findByTeacher(mTeacher.getUUID());

        for (Schedule schedule : scheduleList) {
            if (mSchedule.getUUID().equals(schedule.getUUID())) {
                break;
            }
            if (mDayOfWeek.equals(schedule.getDayOfWeek())) {
                String startTime = mStartTimeField.getText().toString();
                String endTime = mEndTimeField.getText().toString();
                if (startTime.equals(schedule.getStartTime())
                        || endTime.equals(schedule.getEndTime())) {
                    return false;
                }
                if (startTime.compareTo(schedule.getStartTime()) > 0
                        && startTime.compareTo(schedule.getEndTime()) < 0) {
                    return false;
                }
                if (endTime.compareTo(schedule.getStartTime()) > 0
                        && endTime.compareTo(schedule.getEndTime()) < 0) {
                    return false;
                }
                if (startTime.compareTo(schedule.getStartTime()) < 0
                        && endTime.compareTo(schedule.getEndTime()) > 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
