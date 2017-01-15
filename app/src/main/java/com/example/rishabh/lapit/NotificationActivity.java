package com.example.rishabh.lapit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationActivity extends AppCompatActivity {

    ListView notificationList;

    private DatabaseReference mNotificationRef, mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationList = (ListView) findViewById(R.id.notificationList);

        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(Profile.getCurrentProfile().getId());
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseListAdapter<Notification> firebaseListAdapter = new FirebaseListAdapter<Notification>(
                NotificationActivity.this,
                Notification.class,
                android.R.layout.two_line_list_item,
                mNotificationRef
        ) {
            @Override
            protected void populateView(View v, Notification model, final int position) {

                ((TextView)v.findViewById(android.R.id.text1)).setText(model.getFollowerName());
                ((TextView)v.findViewById(android.R.id.text2)).setText(model.getFollowerId());

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Notification fdetail = getItem(position);
                        Log.d("-----detail-----",fdetail.getFollowerName());
                        Log.d("-----detail-----",fdetail.getFollowerId());
                        DatabaseReference newFollowing = mUserRef.child(fdetail.getFollowerId()).child("following").push();
                        newFollowing.child("id").setValue(Profile.getCurrentProfile().getId());
                        newFollowing.child("name").setValue(Profile.getCurrentProfile().getName());
                        DatabaseReference newFollower = mUserRef.child(Profile.getCurrentProfile().getId()).child("follower").push();
                        newFollower.child("id").setValue(fdetail.getFollowerId());
                        newFollower.child("name").setValue(fdetail.getFollowerName());

                    }
                });

            }
        };

        notificationList.setAdapter(firebaseListAdapter);

    }
}
