package com.etu.audiencesbooking.bookings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.CallBackItemTouch;
import com.etu.audiencesbooking.R;
import com.etu.audiencesbooking.SessionManager;
import com.etu.audiencesbooking.audiences.AudienceLab;
import com.etu.audiencesbooking.schedule.ScheduleFragment;
import com.etu.audiencesbooking.schedule.ScheduleListFragment;
import com.etu.audiencesbooking.teachers.Teacher;
import com.etu.audiencesbooking.teachers.TeacherLab;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BookingListFragment extends Fragment {

    private static final int REQUEST_CHANGE = 0;

    public static BookingListFragment newInstance() {
        return new BookingListFragment();
    }

    private List<Booking> mBookings;
    private DatabaseReference mDatabase;
    private int mTypeOfUser;
    private String mTeacherId;

    private RecyclerView mRecyclerView;
    private BookingAdapter mAdapter;
    private FloatingActionButton mAddButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookings = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(getContext(),
                SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        mTeacherId = userDetails.get(SessionManager.KEY_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.booking));

        final View view = inflater.inflate(R.layout.booking_list_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.booking_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddButton = view.findViewById(R.id.add_booking_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingFragment fragment = BookingFragment.newInstance(null, mTeacherId);
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setTargetFragment(BookingListFragment.this, REQUEST_CHANGE);
                fragmentManager.beginTransaction().replace(R.id.container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ItemTouchHelper.Callback callback
                = new BookingItemTouchHelperCallBack(new CallBackItemTouch() {
            @Override
            public void itemTouchMove(int oldPosition, int newPosition) {
                //nothing
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
                //we will delete and also we want to undo
                String id = mBookings.get(viewHolder.getBindingAdapterPosition()).getUUID();

                //backup of removed item for undo
                final Booking deletedBooking = mBookings
                        .get(viewHolder.getBindingAdapterPosition());
                final int deletedIndex = viewHolder.getBindingAdapterPosition();

                //remove item from recyclerView
                mAdapter.removeItem(viewHolder.getBindingAdapterPosition());
                BookingLab.get(getContext()).removeBooking(id);

                //showing shackbar
                Snackbar snackbar = Snackbar.make(getActivity()
                                .findViewById(R.id.booking_recycler_view),
                        "Booking removed...", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAdapter.addItem(deletedIndex, deletedBooking);
                        mBookings.add(deletedIndex, deletedBooking);
                        BookingLab.get(getContext()).addBooking(deletedBooking);
                    }
                });
                snackbar.setActionTextColor(getResources()
                        .getColor(R.color.colorAccent, null));
                snackbar.show();
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        updateList();

        mDatabase = FirebaseDatabase.getInstance().getReference("Bookings");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });

        return view;
    }

    private void updateList() {
        if (mTeacherId != null) {
            Teacher user = TeacherLab.get(getContext()).getTeacher(mTeacherId);
            if (user != null) {
                mTypeOfUser = user.getType();
            } else {
                mTypeOfUser = 0;
            }
        } else {
            mTypeOfUser = 1;
        }

        if (mTypeOfUser == 1) {
            mBookings = BookingLab.get(getContext()).getBookings();
        } else {
            mBookings = BookingLab.get(getContext()).findByTeacher(mTeacherId);
        }

        mAdapter = new BookingAdapter(mBookings, getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CHANGE) {
            Booking booking = (Booking) data.getSerializableExtra(BookingFragment.EXTRA_BOOKING);
            BookingLab.get(getContext()).addBooking(booking);
        }
    }
}
