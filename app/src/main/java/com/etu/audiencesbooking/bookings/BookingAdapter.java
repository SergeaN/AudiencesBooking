package com.etu.audiencesbooking.bookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.audiences.Audience;
import com.etu.audiencesbooking.audiences.AudienceLab;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingHolder> {

    private static final int REQUEST_CHANGE_BOOKING = 0;

    private List<Booking> mBookings;
    private Context mContext;

    private Fragment mFragment;

    public BookingAdapter(List<Booking> bookings, Context context, Fragment fragment) {
        mBookings = bookings;
        mContext = context;
        mFragment = fragment;
    }

    @NonNull
    @Override
    public BookingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_list_item, parent, false);
        return new BookingHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingHolder holder, int position) {
        Booking booking = mBookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }

    public void setBookings(List<Booking> bookings) {
        mBookings = bookings;
    }

    public void addItem(int position, Booking booking) {
        mBookings.add(position, booking);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mBookings.remove(position);
        notifyItemRemoved(position);
    }

    public class BookingHolder extends RecyclerView.ViewHolder {

        private Booking mBooking;
        private Audience mAudience;
        private Date mStartDate;
        private Date mEndDate;
        private Date mCurrentDate;

        private TextView mNumber;
        private TextView mDate;

        public ConstraintLayout mFrontView;
        public RelativeLayout mBackgroundView;

        public BookingHolder(@NonNull View itemView) {
            super(itemView);
            mNumber = (TextView) itemView.findViewById(R.id.booking_number_text);
            mDate = (TextView) itemView.findViewById(R.id.booking_date);
            mFrontView = (ConstraintLayout) itemView
                    .findViewById(R.id.booking_list_item_view);
            mBackgroundView = (RelativeLayout) itemView
                    .findViewById(R.id.background_list_booking_item_view);
            mFrontView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookingFragment fragment = BookingFragment.newInstance(mBooking.getUUID(),
                            mBooking.getTeacherID());
                    FragmentManager fragmentManager = mFragment.getFragmentManager();
                    fragment.setTargetFragment(mFragment, REQUEST_CHANGE_BOOKING);
                    fragmentManager.beginTransaction().replace(R.id.container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        public void bind(Booking booking) {
            mBooking = booking;
            mAudience = AudienceLab.get(mContext).getAudience(mBooking.getAudienceID());
            mStartDate = mBooking.getStartDate();
            mEndDate = mBooking.getEndDate();
            mCurrentDate = Calendar.getInstance().getTime();

            DateFormat startDateFormat = new SimpleDateFormat("dd.MM EEE HH:mm",
                    Locale.ENGLISH);
            DateFormat endDateFormat = new SimpleDateFormat("HH:mm",
                    Locale.ENGLISH);

            String number;
            if(mAudience != null){
                number = "№" + mAudience.getNumber();
            }
            else{
                number = "not found";
            }
            String startDate = startDateFormat.format(mStartDate);
            String endDate = endDateFormat.format(mEndDate);
            String date = startDate + " - " + endDate;

            mNumber.setText(number);
            mDate.setText(date);
            if (mCurrentDate.after(mEndDate)) {
                mFrontView.setBackgroundResource(R.drawable.list_item_booking_overdue_bg);
            }
        }
    }
}
