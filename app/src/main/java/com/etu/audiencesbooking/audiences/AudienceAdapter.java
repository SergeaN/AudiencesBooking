package com.etu.audiencesbooking.audiences;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AudienceAdapter extends RecyclerView.Adapter<AudienceAdapter.AudienceHolder> {

    public AudienceAdapter(List<Audience> audiences, Fragment fragment, int typeOfUser) {
    }

    @NonNull
    @Override
    public AudienceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        return new AudienceHolder(view);
    }

    @Override
    public void onBindViewHolder(AudienceHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setAudiences(List<Audience> audiences) {

    }

    public void addItem(int position, Audience audience) {
    }

    public void removeItem(int position) {
    }

    public class AudienceHolder extends RecyclerView.ViewHolder {

        public AudienceHolder(@NonNull View itemView) {
            super(itemView);

        }

        public void bind(Audience audience) {

        }
    }
}

