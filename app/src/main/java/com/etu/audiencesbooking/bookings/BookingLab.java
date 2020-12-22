package com.etu.audiencesbooking.bookings;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BookingLab {
    private static BookingLab sBookingLab;

    private Context mContext;
    private Map<String, Booking> mBookings;
    private DatabaseReference mDatabase;

    public static BookingLab get(Context context) {
        if (sBookingLab == null) {
            sBookingLab = new BookingLab(context);
        }
        return sBookingLab;
    }

    private BookingLab(final Context context) {
        mBookings = new TreeMap<>();
        mContext = context.getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mBookings.size() > 0) mBookings.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    assert booking != null;
                    mBookings.put(booking.getUUID(), booking);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }

    public void addBooking(Booking booking) {
        mDatabase.child(booking.getUUID()).setValue(booking);
    }

    public void removeBooking(String id) {
        mDatabase.child(id).removeValue();
    }

    public List<Booking> getBookings() {
        clearOldBooking();
        List<Booking> bookings = new ArrayList<>(mBookings.values());
        bookings.sort(new BookingComparator());
        return bookings;
    }

    public Booking getBooking(String id) {
        return mBookings.get(id);
    }

    public List<Booking> findByAudience(String id) {
        clearOldBooking();
        List<Booking> result = new ArrayList<>();
        for (Booking booking : mBookings.values()) {
            if (booking.getAudienceID().equals(id)) result.add(booking);
        }
        return result;
    }

    public List<Booking> findByTeacher(String id) {
        clearOldBooking();
        List<Booking> result = new ArrayList<>();
        for (Booking booking : mBookings.values()) {
            if (booking.getTeacherID().equals(id)) result.add(booking);
        }
        return result;
    }

    public List<Booking> findByDate(Date date) {
        clearOldBooking();
        List<Booking> result = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (Booking booking : mBookings.values()) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(booking.getStartDate());
            if (calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                    && calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                    && calendar.get(Calendar.DAY_OF_MONTH)
                    == currentCalendar.get(Calendar.DAY_OF_MONTH)) {
                result.add(booking);
            }
        }
        return result;
    }

    private void clearOldBooking() {
        for (Booking booking : mBookings.values()) {
            if (Calendar.getInstance().getTime().after(booking.getEndDate())) {
                mDatabase.child(booking.getUUID()).removeValue();
            }
        }
    }
}
