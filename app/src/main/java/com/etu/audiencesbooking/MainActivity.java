package com.etu.audiencesbooking;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.etu.audiencesbooking.audiences.AudienceLab;
import com.etu.audiencesbooking.audiences.AudienceListFragment;
import com.etu.audiencesbooking.bookings.BookingLab;
import com.etu.audiencesbooking.bookings.BookingListFragment;
import com.etu.audiencesbooking.navigationmenu.DrawerAdapter;
import com.etu.audiencesbooking.navigationmenu.DrawerItem;
import com.etu.audiencesbooking.navigationmenu.SimpleItem;
import com.etu.audiencesbooking.navigationmenu.SpaceItem;
import com.etu.audiencesbooking.schedule.ScheduleLab;
import com.etu.audiencesbooking.schedule.ScheduleListFragment;
import com.etu.audiencesbooking.teachers.TeacherLab;
import com.etu.audiencesbooking.teachers.TeacherListFragment;
import com.etu.audiencesbooking.teachers.TeacherProfileFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_CLOSE = 0;
    private static final int POS_BOOKING = 1;
    private static final int POS_MY_PROFILE = 2;
    private static final int POS_OVERVIEW = 3;
    private static final int POS_AUDIENCES = 4;
    private static final int POS_TEACHERS = 5;
    private static final int POS_SCHEDULE = 6;
    private static final int POS_ABOUT_US = 7;
    private static final int POS_LOGOUT = 9;

    private static final int REQUEST_LASTFRAGMENT = 0;

    private String[] mScreenTitles;
    private Drawable[] mScreenIcons;
    private Toolbar mToolbar;

    private SlidingRootNav mSlidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isAlreadyLogin()) {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class),
                    REQUEST_LASTFRAGMENT);
        }

        setContentView(R.layout.main_activity);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        BookingLab.get(this);
        TeacherLab.get(this);
        AudienceLab.get(this);
        ScheduleLab.get(this);

        mSlidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(mToolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        mScreenTitles = loadScreenTitles();
        mScreenIcons = loadScreenIcons();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_BOOKING).setChecked(true),
                createItemFor(POS_MY_PROFILE),
                createItemFor(POS_OVERVIEW),
                createItemFor(POS_AUDIENCES),
                createItemFor(POS_TEACHERS),
                createItemFor(POS_SCHEDULE),
                createItemFor(POS_ABOUT_US),
                new SpaceItem(260),
                createItemFor(POS_LOGOUT)
        ));
        adapter.setSelectedListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_BOOKING);
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(mScreenIcons[position], mScreenTitles[position])
                .withIconTint(color(R.color.colorPrimaryDark))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.colorPrimaryDark))
                .withSelectedTextTint(color(R.color.black));
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            int id = typedArray.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        typedArray.recycle();
        return icons;
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (position == POS_BOOKING) {
            BookingListFragment bookingListFragment = BookingListFragment.newInstance();
            transaction.replace(R.id.container, bookingListFragment);
        } else if (position == POS_MY_PROFILE) {
            TeacherProfileFragment teacherProfileFragment =
                    TeacherProfileFragment.newInstance();
            transaction.replace(R.id.container, teacherProfileFragment);
        } else if (position == POS_OVERVIEW) {
            OverviewFragment overviewFragment = OverviewFragment.newInstance();
            transaction.replace(R.id.container, overviewFragment);
        } else if (position == POS_AUDIENCES) {
            AudienceListFragment audienceListFragment = AudienceListFragment.newInstance();
            transaction.replace(R.id.container, audienceListFragment);
        } else if (position == POS_TEACHERS) {
            TeacherListFragment teacherListFragment = TeacherListFragment.newInstance();
            transaction.replace(R.id.container, teacherListFragment);
        } else if (position == POS_SCHEDULE) {
            ScheduleListFragment scheduleListFragment = ScheduleListFragment.newInstance();
            transaction.replace(R.id.container, scheduleListFragment);
        } else if (position == POS_ABOUT_US) {
            AboutFragment aboutFragment = AboutFragment.newInstance();
            transaction.replace(R.id.container, aboutFragment);
        } else if (position == POS_LOGOUT) {
            SessionManager sessionManager = new SessionManager(MainActivity.this,
                    SessionManager.SESSION_REMEMBERME);
            sessionManager.logoutUserFromSession();
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class),
                    REQUEST_LASTFRAGMENT);
            mSlidingRootNav.closeMenu();
            return;
        }

        mSlidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean isAlreadyLogin() {
        //check weather email&password is already saved in SharedPreference or not
        SessionManager sessionManager = new SessionManager(MainActivity.this,
                SessionManager.SESSION_REMEMBERME);
        return sessionManager.checkRememberMe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_LASTFRAGMENT) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BookingListFragment bookingListFragment = BookingListFragment.newInstance();
            transaction.replace(R.id.container, bookingListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        //block
    }
}