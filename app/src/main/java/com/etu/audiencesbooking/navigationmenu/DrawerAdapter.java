package com.etu.audiencesbooking.navigationmenu;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private List<DrawerItem> mItems;
    private Map<Class<? extends DrawerItem>, Integer> mViewTypes;
    private SparseArray<DrawerItem> mHolderFactories;

    private OnItemSelectedListener mSelectedListener;

    public DrawerAdapter(List<DrawerItem> items) {
        mItems = items;
        mViewTypes = new HashMap<>();
        mHolderFactories = new SparseArray<>();

        processViewTypes();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = mHolderFactories.get(viewType).createViewHolder(parent);
        holder.mDrawerAdapter = this;
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mItems.get(position).bindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mViewTypes.get(mItems.get(position).getClass());
    }

    private void processViewTypes() {
        int type = 0;
        for (DrawerItem item : mItems) {
            if (!mViewTypes.containsKey(item.getClass())) {
                mViewTypes.put(item.getClass(), type);
                mHolderFactories.put(type, item);
                type++;
            }
        }
    }

    public void setSelected(int position) {
        DrawerItem newChecked = mItems.get(position);
        if (!newChecked.isSelectable()) {
            return;
        }

        for (int i = 0; i < mItems.size(); i++) {
            DrawerItem item = mItems.get(i);
            if (item.isChecked()) {
                item.setChecked(false);
                notifyItemChanged(i);
                break;
            }
        }

        newChecked.setChecked(true);
        notifyItemChanged(position);

        if (mSelectedListener != null) {
            mSelectedListener.onItemSelected(position);
        }
    }

    public void setSelectedListener(OnItemSelectedListener listener) {
        mSelectedListener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    static abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private DrawerAdapter mDrawerAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mDrawerAdapter.setSelected(getBindingAdapterPosition());
        }
    }

}
