package com.etu.audiencesbooking.bookings;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.audiences.Audience;
import com.etu.audiencesbooking.AudiencePickerFragment;
import com.etu.audiencesbooking.TimePickerFragment;
import com.etu.audiencesbooking.audiences.AudienceLab;
import com.etu.audiencesbooking.schedule.Schedule;
import com.etu.audiencesbooking.schedule.ScheduleLab;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BookingFragment extends Fragment {

    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_AUDIENCE = "DialogAudience";

    private static final int REQUEST_START_TIME = 0;
    private static final int REQUEST_END_TIME = 1;
    private static final int REQUEST_AUDIENCE = 2;

    private static final String ARG_BOOKING_ID = "booking_id";
    private static final String ARG_TEACHER_ID = "teacher_id";
    public static final String EXTRA_BOOKING = "com.etu.audiencesbooking.bookings.addbooking";

    private MaterialTextView mDateField;
    private MaterialTextView mStartTimeField;
    private MaterialTextView mEndTimeField;
    private MaterialTextView mAudienceNumberField;

    private Button mSaveButton;
    private Button mCancelButton;

    private Booking mBooking;

    private String mUUID;
    private Date mDate;
    private Date mStartTime;
    private Date mEndTime;
    private Audience mAudience;
    private String mTeacherId;

    private DateFormat mTimeFormat;
    private DateFormat mDateFormat;

    public static BookingFragment newInstance(String bookingId, @NonNull String teacherId) {
        Bundle args = new Bundle();
        args.putString(ARG_BOOKING_ID, bookingId);
        args.putString(ARG_TEACHER_ID, teacherId);
        BookingFragment fragment = new BookingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Booking booking) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BOOKING, booking);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String bookingId = getArguments().getString(ARG_BOOKING_ID);
        if (bookingId != null) {
            mBooking = BookingLab.get(getContext()).getBooking(bookingId);
        }
        mTeacherId = getArguments().getString(ARG_TEACHER_ID);
        mTimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        mDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater
                .inflate(R.layout.booking_dialog_fragment, container, false);

        mDateField = view.findViewById(R.id.booking_set_date);
        mDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        mStartTimeField = view.findViewById(R.id.booking_set_start_time);
        mStartTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(REQUEST_START_TIME, mStartTime);
            }
        });

        mEndTimeField = view.findViewById(R.id.booking_set_end_time);
        mEndTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(REQUEST_END_TIME, mEndTime);
            }
        });

        mAudienceNumberField = view.findViewById(R.id.booking_set_audience);
        mAudienceNumberField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudiencePickerFragment dialog = AudiencePickerFragment.newInstance();
                dialog.setTargetFragment(BookingFragment.this, REQUEST_AUDIENCE);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), DIALOG_AUDIENCE);
            }
        });

        mSaveButton = view.findViewById(R.id.save_booking_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFieldValidate()) {
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
                if (!isFreeTimeBySchedule(0)
                        || !isFreeTimeByBooking(0)) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.audience_busy),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isFreeTimeBySchedule(1)
                        || !isFreeTimeByBooking(1)) {
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.teacher_busy),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Booking booking = new Booking(
                        mUUID, mAudience.getUUID(), mTeacherId,
                        mStartTime, mEndTime
                );
                sendResult(Activity.RESULT_OK, booking);
            }
        });

        mCancelButton = view.findViewById(R.id.cancel_booking_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_CANCELED, null);
            }
        });

        if (mBooking != null) {
            mUUID = mBooking.getUUID();

            mDate = mBooking.getStartDate();
            mDateField.setText(mDateFormat.format(mBooking.getStartDate()));
            mDateField.setTextColor(getResources().getColor(R.color.black, null));

            mStartTime = mBooking.getStartDate();
            mStartTimeField.setText(mTimeFormat.format(mBooking.getStartDate()));
            mStartTimeField.setTextColor(getResources().getColor(R.color.black, null));

            mEndTime = mBooking.getEndDate();
            mEndTimeField.setText(mTimeFormat.format(mBooking.getEndDate()));
            mEndTimeField.setTextColor(getResources().getColor(R.color.black, null));

            mAudience = AudienceLab.get(getContext()).getAudience(mBooking.getAudienceID());
            String number = mAudience.getNumber() + " (" + mAudience.getCapacity() + ")";
            mAudienceNumberField.setText(number);
            mAudienceNumberField.setTextColor(getResources().getColor(R.color.black, null));

        } else {
            mUUID = UUID.randomUUID().toString();
            mDate = Calendar.getInstance().getTime();
            mStartTime = mDate;
            mEndTime = mDate;
        }

        return view;
    }

    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                mDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
                mDateField.setText(dateFormat.format(mDate));
                mDateField.setTextColor(getResources().getColor(R.color.black, null));

                Calendar date = Calendar.getInstance();
                date.setTime(mDate);
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(mStartTime);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(mEndTime);
                mStartTime = new GregorianCalendar(
                        date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.HOUR_OF_DAY),
                        startDate.get(Calendar.MINUTE)
                ).getTime();
                mEndTime = new GregorianCalendar(
                        date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                        date.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.HOUR_OF_DAY),
                        endDate.get(Calendar.MINUTE)
                ).getTime();
            }
        }, year, month, day);
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis()
                + (1000 * 60 * 60 * 24 * 150L)); //ms*s*min*hours*days
        datePickerDialog.show();
    }

    public void showTimePickerDialog(int requestCode, Date time) {
        TimePickerFragment dialog = TimePickerFragment.newInstance(time);
        dialog.setTargetFragment(BookingFragment.this, requestCode);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), DIALOG_TIME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_START_TIME) {
            mStartTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            mStartTimeField.setText(dateFormat.format(mStartTime));
            mStartTimeField.setTextColor(getResources().getColor(R.color.black, null));
        } else if (requestCode == REQUEST_END_TIME) {
            mEndTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            mEndTimeField.setText(dateFormat.format(mEndTime));
            mEndTimeField.setTextColor(getResources().getColor(R.color.black, null));
        } else if (requestCode == REQUEST_AUDIENCE) {
            mAudience = (Audience) data
                    .getSerializableExtra(AudiencePickerFragment.EXTRA_PICK_AUDIENCE);
            String number = mAudience.getNumber() + " (" + mAudience.getCapacity() + ")";
            mAudienceNumberField.setText(number);
            mAudienceNumberField.setTextColor(getResources().getColor(R.color.black, null));
        }
    }

    private boolean isFieldValidate() {
        return mDateField.getText() != getResources().getString(R.string.date)
                && mStartTimeField.getText() != getResources().getString(R.string.start_time)
                && mEndTimeField.getText() != getResources().getString(R.string.end_time)
                && mAudienceNumberField.getText() != getResources().getString(R.string.audience);
    }

    private boolean isCorrectTime() {
        return mStartTime.before(mEndTime);
    }

    private boolean isFreeTimeBySchedule(int mode) {
        List<Schedule> scheduleList;
        if (mode == 0) scheduleList = ScheduleLab.get(getContext())
                .findByAudience(mAudience.getUUID());
        else scheduleList = ScheduleLab.get(getContext())
                .findByTeacher(mTeacherId);

        DateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        for (Schedule schedule : scheduleList) {
            String dayOfWeek = dateFormat.format(mStartTime).toUpperCase();
            if (dayOfWeek.equals(schedule.getDayOfWeek())) {
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

    private boolean isFreeTimeByBooking(int mode) {
        List<Booking> bookingList;
        if (mode == 0) bookingList = BookingLab.get(getContext())
                .findByAudience(mAudience.getUUID());
        else bookingList = BookingLab.get(getContext())
                .findByTeacher(mTeacherId);

        for (Booking booking : bookingList) {
            if(mBooking != null && mBooking.getUUID().equals(booking.getUUID())){
                break;
            }
            if (mStartTime.equals(booking.getStartDate())
                    || mEndTime.equals(booking.getEndDate())) {
                return false;
            }
            if (mStartTime.after(booking.getStartDate())
                    && mStartTime.before(booking.getEndDate())) {
                return false;
            }
            if (mEndTime.after(booking.getStartDate())
                    && mEndTime.before(booking.getEndDate())) {
                return false;
            }
            if (mStartTime.before(booking.getStartDate())
                    && mEndTime.after(booking.getEndDate())) {
                return false;
            }
        }
        return true;
    }

}
