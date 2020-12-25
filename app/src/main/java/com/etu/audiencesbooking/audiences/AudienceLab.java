package com.etu.audiencesbooking.audiences;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AudienceLab {

    private static AudienceLab sAudienceLab;
    private Context mContext;
    private Map<String, Audience> mAudiences;
    private DatabaseReference mDatabase;

    public static AudienceLab get(Context context) {
        if (sAudienceLab == null) {
            sAudienceLab = new AudienceLab(context);
        }
        return sAudienceLab;
    }

    private AudienceLab(final Context context) {
        mAudiences = new TreeMap<>();
        mContext = context.getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference("Audiences");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mAudiences.size() > 0) mAudiences.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Audience audience = dataSnapshot.getValue(Audience.class);
                    assert audience != null;
                    mAudiences.put(audience.getUUID(), audience);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FIREBASE", error.getMessage());
            }
        });
    }
    public void addAudience(Audience audience) {
        mDatabase.child(audience.getUUID()).setValue(audience);
    }

    public void removeAudience(String id) {
        mDatabase.child(id).removeValue();
    }

    public List<Audience> getAudiences() {
        List<Audience> audiences = new ArrayList<>(mAudiences.values());
        audiences.sort(new AudienceComparator());
        return audiences;
    }

    public Audience getAudience(String id) {
        return mAudiences.get(id);
    }
}