package com.example.rishabh.lapit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShowUserActivity extends AppCompatActivity {

    TextView userEmail, userDisplayName;
    ImageView userProfilePic;
    Button followBtn;

    private DatabaseReference mNotificationRef;
    private DatabaseReference mUserRef;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        userProfilePic = (ImageView) findViewById(R.id.userProfilePic);
        userDisplayName = (TextView) findViewById(R.id.userDisplayName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        followBtn = (Button) findViewById(R.id.followBtn);

        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        key = getIntent().getStringExtra("user_key");

        mUserRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userDisplayName.setText((String) dataSnapshot.child("name").getValue());
                userEmail.setText((String) dataSnapshot.child("email").getValue());
                Picasso.with(ShowUserActivity.this).load((String) dataSnapshot.child("image").getValue()).into(userProfilePic);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference newNotif = mNotificationRef.child(key).push();
                newNotif.child("followerId").setValue(Profile.getCurrentProfile().getId());
                newNotif.child("followerName").setValue(Profile.getCurrentProfile().getName());
                /*
                DatabaseReference newFollowing = mUserRef.child(Profile.getCurrentProfile().getId()).child("following").push();
                newFollowing.child("id").setValue(key);
                newFollowing.child("name").setValue(userDisplayName.getText().toString());
                DatabaseReference newFollower = mUserRef.child(key).child("follower").push();
                newFollower.child("id").setValue(Profile.getCurrentProfile().getId());
                newFollower.child("name").setValue(Profile.getCurrentProfile().getName());
                */
                startActivity(new Intent(ShowUserActivity.this,MainActivity.class));
                finish();

            }
        });

    }
}
