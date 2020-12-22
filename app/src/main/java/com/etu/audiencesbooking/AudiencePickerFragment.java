package com.etu.audiencesbooking;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.audiences.Audience;
import com.etu.audiencesbooking.audiences.AudienceLab;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class AudiencePickerFragment extends DialogFragment {
    public static final String EXTRA_PICK_AUDIENCE
            = "com.etu.audiencesbooking.schedules.pickaudience";

    private List<Audience> mAudiences;

    private RecyclerView mRecyclerView;
    private PickAudienceAdapter mAdapter;

    public static AudiencePickerFragment newInstance() {
        return new AudiencePickerFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.pick_recyclerview_fragment, null);

        mRecyclerView = view.findViewById(R.id.pick_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAudiences = AudienceLab.get(getContext()).getAudiences();
        mAdapter = new PickAudienceAdapter(mAudiences);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Pick Audience: ")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, null);
                    }
                });
        return dialog.create();
    }

    private void sendResult(int resultCode, Audience audience) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PICK_AUDIENCE, audience);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private class PickAudienceHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Audience mAudience;
        private TextView mNumberOfAudience;

        public PickAudienceHolder(@NonNull View itemView) {
            super(itemView);
            mNumberOfAudience = (TextView) itemView;
            mNumberOfAudience.setOnClickListener(this);
        }

        public void bind(Audience audience) {
            mAudience = audience;
            String audienceString = "№" + mAudience.getNumber()
                    + " (" + mAudience.getCapacity() + ")";
            mNumberOfAudience.setText(audienceString);
        }

        @Override
        public void onClick(View v) {
            sendResult(Activity.RESULT_OK, mAudience);
            dismiss();
        }
    }

    private class PickAudienceAdapter extends RecyclerView.Adapter<PickAudienceHolder> {

        private final List<Audience> mAudiences;

        public PickAudienceAdapter(List<Audience> audiences) {
            mAudiences = audiences;
        }

        @NonNull
        @Override
        public PickAudienceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new PickAudienceHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PickAudienceHolder holder, int position) {
            holder.bind(mAudiences.get(position));
        }

        @Override
        public int getItemCount() {
            return mAudiences.size();
        }
    }
}
