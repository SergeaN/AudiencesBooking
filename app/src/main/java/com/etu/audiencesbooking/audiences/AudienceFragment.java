package com.etu.audiencesbooking.audiences;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.etu.audiencesbooking.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

public class AudienceFragment extends DialogFragment {

    public static final String EXTRA_AUDIENCE = "com.etu.audiencesbooking.audience_dialog";

    private static final String ARG_AUDIENCE_ID = "audience_id";

    private String mUuidField;
    private TextInputLayout mNumberField;
    private TextInputLayout mCapacityField;

    private Audience mAudience;

    public static AudienceFragment newInstance(String id) {
        Bundle arg = new Bundle();
        arg.putString(ARG_AUDIENCE_ID, id);
        AudienceFragment fragment = new AudienceFragment();
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String id = getArguments().getString(ARG_AUDIENCE_ID);
        if (id != null) mAudience = AudienceLab.get(getContext()).getAudience(id);
    }

    private void sendResult(int resultCode, Audience audience) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_AUDIENCE, audience);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.audience_dialog_fragment, null);

        mNumberField = view.findViewById(R.id.dialog_number);
        mCapacityField = view.findViewById(R.id.dialog_capacity);

        if (mAudience != null) {
            mUuidField = mAudience.getUUID();
            mNumberField.getEditText().setText(mAudience.getNumber());
            String capacityString = Integer.toString(mAudience.getCapacity());
            mCapacityField.getEditText().setText(capacityString);
        } else {
            mUuidField = UUID.randomUUID().toString();
        }

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle(getResources().getString(R.string.edit_audience))
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isNumberValid()) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.incorrect_number),
                                    Toast.LENGTH_LONG).show();
                            sendResult(Activity.RESULT_CANCELED, null);
                            return;
                        }
                        if (!isCapacityValid()) {
                            Toast.makeText(getContext(),
                                    getResources().getString(R.string.incorrect_capacity),
                                    Toast.LENGTH_LONG).show();
                            sendResult(Activity.RESULT_CANCELED, null);
                            return;
                        }

                        String id = mUuidField;
                        String number = mNumberField.getEditText().getText().toString();
                        String capacity = mCapacityField.getEditText().getText().toString();
                        Audience audience = new Audience(id, number, Integer.parseInt(capacity));

                        sendResult(AudienceListFragment.RESULT_ADD, audience);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED, null);
                    }
                });
        if (mAudience != null) {
            dialog.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String id = mAudience.getUUID();
                    Audience audience = new Audience();
                    audience.setUUID(id);
                    sendResult(AudienceListFragment.RESULT_DELETE, audience);
                }
            });
        }

        return dialog.create();
    }

    private boolean isNumberValid() {
        return mNumberField.getEditText().getText().length() == 4;
    }

    private boolean isCapacityValid() {
        return mCapacityField.getEditText().getText().length() > 0
                && mCapacityField.getEditText().getText().length() < 6;
    }
}
