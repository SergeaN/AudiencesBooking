package com.etu.audiencesbooking.audiences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etu.audiencesbooking.R;

import java.util.List;

public class AudienceAdapter extends RecyclerView.Adapter<AudienceAdapter.AudienceHolder> {

    private static final String DIALOG_ADD_AUDIENCE = "DialogAddAudience";
    private static final int REQUEST_ADD_AUDIENCE = 0;

    private List<Audience> mAudiences;
    private Fragment mFragment;
    private int mTypeOfUser;

    public AudienceAdapter(List<Audience> audiences, Fragment fragment, int typeOfUser) {
        mAudiences = audiences;
        mFragment = fragment;
        mTypeOfUser = typeOfUser;
    }

    @NonNull
    @Override
    public AudienceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audience_list_item, parent, false);
        return new AudienceHolder(view);
    }

    @Override
    public void onBindViewHolder(AudienceHolder holder, int position) {
        Audience audience = mAudiences.get(position);
        holder.bind(audience);
    }

    @Override
    public int getItemCount() {
        return mAudiences.size();
    }

    public void setAudiences(List<Audience> audiences) {
        mAudiences = audiences;
    }

    public void addItem(int position, Audience audience) {
        mAudiences.add(position, audience);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        mAudiences.remove(position);
        notifyItemRemoved(position);
    }

    public class AudienceHolder extends RecyclerView.ViewHolder {

        private Audience mAudience;
        private TextView mNumber;
        private TextView mCapacity;
        private CardView mView;

        public AudienceHolder(@NonNull View itemView) {
            super(itemView);
            mNumber = (TextView) itemView.findViewById(R.id.audience_number);
            mCapacity = (TextView) itemView.findViewById(R.id.audience_capacity);
            mView = (CardView) itemView.findViewById(R.id.audience_list_item_view);
            if (mTypeOfUser == 1) { //set admin function
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentManager manager = mFragment.getFragmentManager();
                        AudienceFragment dialog = AudienceFragment.newInstance(mAudience.getUUID());
                        dialog.setTargetFragment(mFragment, REQUEST_ADD_AUDIENCE);
                        dialog.setCancelable(false);
                        dialog.show(manager, DIALOG_ADD_AUDIENCE);
                    }
                });
            }
        }

        public void bind(Audience audience) {
            mAudience = audience;
            String number = "№" + mAudience.getNumber();
            String capacity = Integer.toString(mAudience.getCapacity());
            mNumber.setText(number);
            mCapacity.setText(capacity);
        }
    }
}

