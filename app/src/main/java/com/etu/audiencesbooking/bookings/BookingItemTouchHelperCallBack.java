package com.etu.audiencesbooking.bookings;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.CallBackItemTouch;
import com.etu.audiencesbooking.R;

public class BookingItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    CallBackItemTouch callBackItemTouch;

    public BookingItemTouchHelperCallBack(CallBackItemTouch callBackItemTouch) {
        this.callBackItemTouch = callBackItemTouch;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        //in this when we move our item then what we do we just get position and send in interface
        callBackItemTouch.itemTouchMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        callBackItemTouch.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDrawOver(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive);
        } else {
            final View foregroundView = ((BookingAdapter.BookingHolder) viewHolder).mBackgroundView;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView,
                    dX, dY, actionState, isCurrentlyActive);
        }
        //in this we will show delete button when we swipe
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
            final View foregroundView = ((BookingAdapter.BookingHolder) viewHolder).mFrontView;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView,
                    dX, dY, actionState, isCurrentlyActive);
        }
        //when we swipe so with smooth animation
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {

        //this will set color of item when we drag and leave any position so we want original color
        final View foregroundView = ((BookingAdapter.BookingHolder) viewHolder).mFrontView;
        foregroundView.setBackgroundResource(R.drawable.list_item_booking_bg);

        getDefaultUIUtil().clearView(foregroundView);
        //this will clear view when we swipe and drag
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        //in this we change color when item selected ok
        if (viewHolder != null) {
            final View foregroundView = ((BookingAdapter.BookingHolder) viewHolder).mFrontView;
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                foregroundView.setBackgroundColor(Color.LTGRAY);
            }
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
