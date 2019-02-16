package com.example.hw.blogapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class NotificationFragment extends Fragment {

    private TextView mTextView;
    private FirebaseDatabase mfirebaseDB = FirebaseDatabase.getInstance();
    final DatabaseReference mRef = mfirebaseDB.getReference("Notification");


    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        mTextView = view.findViewById(R.id.textView3);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mNotification = String.valueOf(dataSnapshot.getValue());
                if(!TextUtils.isEmpty(mNotification)){
                    mTextView.setText(mNotification);
                } else {
                    mTextView.setText("No new notification");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

}
