package com.etu.audiencesbooking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.audiences.Audience;
import com.etu.audiencesbooking.audiences.AudienceLab;
import com.etu.audiencesbooking.bookings.Booking;
import com.etu.audiencesbooking.bookings.BookingAdapter;
import com.etu.audiencesbooking.bookings.BookingComparator;
import com.etu.audiencesbooking.bookings.BookingFragment;
import com.etu.audiencesbooking.bookings.BookingLab;
import com.etu.audiencesbooking.schedule.Schedule;
import com.etu.audiencesbooking.schedule.ScheduleLab;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class OverviewFragment extends Fragment {

    private static final String DIALOG_AUDIENCE = "DialogAudience";
    private static final int REQUEST_AUDIENCE = 0;

    private List<Booking> mBookings;
    private List<Schedule> mSchedule;

    private Date mDate;
    private DateFormat mDateFormat;
    private Audience mAudience;

    private RecyclerView mRecyclerView;
    private OverviewAdapter mAdapter;
    private Button mChooseDateButton;
    private Button mChooseAudienceButton;
    private TextView mResetView;


    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSchedule = new ArrayList<>();
        mDateFormat = new SimpleDateFormat("dd MMMM yyyy",
                Locale.ENGLISH);
        mDate = Calendar.getInstance().getTime();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.overview);

        View view = inflater.inflate(R.layout.overview_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.overview_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mChooseDateButton = view.findViewById(R.id.choose_date);
        mChooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
                        mChooseDateButton.setText(mDateFormat.format(mDate));
                        updateList();
                    }
                }, year, month, day);
                datePickerDialog.setCancelable(false);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis()
                        + (1000 * 60 * 60 * 24 * 150L)); //ms*s*min*hours*days
                datePickerDialog.show();
            }
        });

        mChooseAudienceButton = view.findViewById(R.id.choose_audiece);
        mChooseAudienceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudiencePickerFragment dialog = AudiencePickerFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                dialog.setTargetFragment(OverviewFragment.this, REQUEST_AUDIENCE);
                dialog.setCancelable(false);
                dialog.show(fragmentManager, DIALOG_AUDIENCE);
            }
        });

        mResetView = view.findViewById(R.id.overview_reset);
        mResetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDate = Calendar.getInstance().getTime();
                mChooseDateButton.setText(getResources().getString(R.string.choose_date));
                mAudience = null;
                mChooseAudienceButton.setText(getResources().getString(R.string.choose_audinece));
                updateList();
            }
        });

        updateList();

        return view;
    }


    private void updateList() {
        mBookings = new ArrayList<>();
        if (mAudience == null) {
            mBookings = BookingLab.get(getContext()).findByDate(mDate);
            mSchedule = ScheduleLab.get(getContext()).getFullSchedule();
            DateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            List<Booking> bookingsFromSchedule = new ArrayList<>();
            for (Schedule schedule : mSchedule) {
                if (schedule.getDayOfWeek().equals(dateFormat.format(mDate).toUpperCase())) {
                    bookingsFromSchedule.add(getBookingFromSchedule(schedule));
                }
            }
            mBookings.addAll(bookingsFromSchedule);
        } else {
            List<Booking> bookingsByDate = BookingLab.get(getContext())
                    .findByDate(mDate);
            List<Booking> bookingsByAudience = BookingLab.get(getContext())
                    .findByAudience(mAudience.getUUID());
            bookingsByAudience.retainAll(bookingsByDate);

            mSchedule = ScheduleLab.get(getContext()).findByAudience(mAudience.getUUID());
            DateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            List<Booking> bookingsFromSchedule = new ArrayList<>();
            for (Schedule schedule : mSchedule) {
                if (schedule.getDayOfWeek().equals(dateFormat.format(mDate).toUpperCase())
                        && schedule.getAudienceID().equals(mAudience.getUUID())) {
                    bookingsFromSchedule.add(getBookingFromSchedule(schedule));
                }
            }

            mBookings.addAll(bookingsByAudience);
            mBookings.addAll(bookingsFromSchedule);
        }
        mBookings.sort(new BookingComparator());
        mAdapter = new OverviewAdapter(mBookings);
        mRecyclerView.setAdapter(mAdapter);
    }

    private Booking getBookingFromSchedule(Schedule schedule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int startHour = Integer.parseInt(schedule.getStartTime().substring(0, 2));
        int startMinute = Integer.parseInt(schedule.getStartTime().substring(3, 5));
        Date startDate = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                startHour, startMinute).getTime();

        int endHour = Integer.parseInt(schedule.getEndTime().substring(0, 2));
        int endMinute = Integer.parseInt(schedule.getEndTime().substring(3, 5));
        Date endDate = new GregorianCalendar(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                endHour, endMinute).getTime();

        return new Booking(
                UUID.randomUUID().toString(), schedule.getAudienceID(),
                schedule.getTeacherID(), startDate, endDate
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_AUDIENCE) {
            mAudience = (Audience) data
                    .getSerializableExtra(AudiencePickerFragment.EXTRA_PICK_AUDIENCE);
            mChooseAudienceButton.setText(mAudience.getNumber());
            updateList();
        }
    }

    private class OverviewHolder extends RecyclerView.ViewHolder {
        private Booking mBooking;
        private Audience mAudience;
        private Date mStartDate;
        private Date mEndDate;

        private TextView mNumber;
        private TextView mDate;

        public OverviewHolder(@NonNull View itemView) {
            super(itemView);
            mNumber = itemView.findViewById(R.id.booking_number_text);
            mDate = itemView.findViewById(R.id.booking_date);
        }

        public void bind(Booking booking) {
            mBooking = booking;
            mAudience = AudienceLab.get(getContext()).getAudience(mBooking.getAudienceID());
            mStartDate = mBooking.getStartDate();
            mEndDate = mBooking.getEndDate();

            DateFormat startDateFormat = new SimpleDateFormat("dd.MM EEE HH:mm",
                    Locale.ENGLISH);
            DateFormat endDateFormat = new SimpleDateFormat("HH:mm",
                    Locale.ENGLISH);

            String number = "№" + mAudience.getNumber();
            String startDate = startDateFormat.format(mStartDate);
            String endDate = endDateFormat.format(mEndDate);
            String date = startDate + " - " + endDate;

            mNumber.setText(number);
            mDate.setText(date);
        }
    }

    private class OverviewAdapter extends RecyclerView.Adapter<OverviewHolder> {

        private final List<Booking> mBookings;

        public OverviewAdapter(List<Booking> bookings) {
            mBookings = bookings;
        }

        @NonNull
        @Override
        public OverviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.booking_list_item, parent, false);
            return new OverviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OverviewHolder holder, int position) {
            Booking booking = mBookings.get(position);
            holder.bind(booking);
        }

        @Override
        public int getItemCount() {
            return mBookings.size();
        }
    }
}
