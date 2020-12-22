package com.etu.audiencesbooking.navigationmenu;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.etu.audiencesbooking.R;

public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

    private int mSelectedItemIconTint;
    private int mSelectedItemTextTint;

    private int mNormalItemIconTint;
    private int mNormalItemTextTint;

    private Drawable mIcon;
    private String mTitle;

    public SimpleItem(Drawable icon, String title) {
        mIcon = icon;
        mTitle = title;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(mTitle);
        holder.icon.setImageDrawable(mIcon);

        holder.title.setTextColor(isChecked ? mSelectedItemTextTint : mNormalItemTextTint);
        holder.icon.setColorFilter(isChecked ? mSelectedItemIconTint : mNormalItemIconTint);
    }

    public SimpleItem withSelectedTextTint(int selectedItemTextTint) {
        mSelectedItemTextTint = selectedItemTextTint;
        return this;
    }

    public SimpleItem withSelectedIconTint(int selectedItemIconTint) {
        mSelectedItemIconTint = selectedItemIconTint;
        return this;
    }

    public SimpleItem withTextTint(int normalItemTextTint) {
        mNormalItemTextTint = normalItemTextTint;
        return this;
    }

    public SimpleItem withIconTint(int normalItemIconTint) {
        mNormalItemIconTint = normalItemIconTint;
        return this;
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder {

        private ImageView icon;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

}
